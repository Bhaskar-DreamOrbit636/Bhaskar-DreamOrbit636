package com.reminder.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reminder.dao.GroupDAO;
import com.reminder.model.Asset;
import com.reminder.model.AssetType;
import com.reminder.model.ResponseModel;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.AssetRequest;
import com.reminder.request.model.AssetSearchCriteria;
import com.reminder.request.model.AssetTypeRequest;
import com.reminder.request.model.Constants;
import com.reminder.response.model.AssetResponse;
import com.reminder.response.model.MyGroupDropDown;
import com.reminder.security.JwtTokenUtil;
import com.reminder.service.AssetService;
import com.reminder.service.UserService;
import com.reminder.utils.CurrentDate;

@RestController
public class AssetController {
	private Logger logger = Logger.getLogger(AssetController.class);
	@Autowired
	private AssetService assetService;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private GroupDAO groupDAO;

	
	CurrentDate curentDate = new CurrentDate();

	/*** create a single Asset ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/createAsset", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public void createAsset(@RequestBody Asset asset) {
		try{
			logger.info("createAsset(asset=" + asset + ") - Creating asset using /createAsset with asset description = " + asset.getAssetDescription());
			assetService.createAsset(asset);
		}catch(Exception e){
			logger.error("Error while creating Asset: " + e);
		}
	}

	/*** Retrieve a single Asset ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/asset/{assetId}", produces = "application/json", method = RequestMethod.GET)
	public AssetResponse getAssetById(@PathVariable("assetId") int assetId) {
		logger.info("getAssetById(assetId=" + assetId + ") - start - Retrieving asset using /asset/{assetId} api");
		AssetResponse asset = assetService.getAssetById(assetId);
		Asset as = asset.getAsset();
		User user1 = userService.getUserById(as.getReminder().getLastModifiedById());
		User user2 = userService.getUserById(as.getReminder().getCreatedById());
		asset.setLastModifiedBy(user1.getAdUserId());
		asset.setCreatedBy(user2.getAdUserId());
		return asset;
	}

	// --- list of assets with pagination and search criteria -----------
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/assets", method = RequestMethod.GET)
	public ResponseEntity<List<AssetResponse>> getAllAssets(@RequestParam(required = false) String sort_by,
			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page_no,
		    HttpServletRequest request) {
		
		User createdUser = getCreatedUser(request);
		List<AssetResponse> assets = assetService.getAllAssets(sort_by, order, searchCriteria, limit, page_no,createdUser.getUserId());
		if (assets.isEmpty()) {
			ResponseEntity<List<AssetResponse>> returnResponseEntity = new ResponseEntity<List<AssetResponse>>(HttpStatus.NO_CONTENT);
			logger.info("getAllAssets(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", request=" + request + ") - end - No Asset created yet - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		ResponseEntity<List<AssetResponse>> returnResponseEntity = new ResponseEntity<List<AssetResponse>>(assets, HttpStatus.OK);
		logger.info("getAllAssets(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", request=" + request + ") - end - Fetching data for all assets - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}

	/*** Update a assets 
	 * @return ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/updateAsset/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> updateAsset(@RequestBody AssetRequest asset, HttpServletRequest request,
			@PathVariable("id") Integer id) {
		logger.info("updateAsset(asset=" + asset + ", request=" + request + ", id=" + id + ") - start - Updating assets : /updateAsset");
		User createdUser = getCreatedUser(request);
		Boolean result = isAuthorized(createdUser.getUserId(), asset.getGroupId(), Constants.UPDATE);
		if (!result) {
			return new ResponseEntity<String>("Not Authorized", HttpStatus.UNAUTHORIZED);
		}
		assetService.updateAsset(asset, id, createdUser);
		ResponseEntity<String> returnResponseEntity = new ResponseEntity<String>("Success", HttpStatus.OK);
		logger.info("updateAsset(asset=" + asset + ", request=" + request + ", id=" + id + ") - end - Asset updated at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " - return value=" + returnResponseEntity);
		return returnResponseEntity;

	}

	private User getCreatedUser(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		User createdUser = null;
		if (header.startsWith("Bearer ")) {
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserByName(username);
		}
		return createdUser;
	}
	
	private int getCreatedUserForSearch(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		int createdUserId = 0;
		if (header.startsWith("Bearer ")) {
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUserId = userService.getUserIdByName(username);
		}
		return createdUserId;
	}
	
	/**
	 * @param userId
	 * @param groupId
	 * @param permissionType
	 * @return
	 */
	public Boolean isAuthorized(int userId , int groupId,String permissionType) {
		List<String> list = groupDAO.getGroupRolesAction(userId, groupId);

		if (list.contains(permissionType)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
	

	/*** Delete a asset 
	 * @return ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteAsset/{assetId}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<String> deleteAsset(@PathVariable("assetId") int assetId,
			@RequestParam(required = false) int groupId, HttpServletRequest request) {
		logger.info("deleteAsset(assetId=" + assetId + ", groupId=" + groupId + ", request=" + request + ") - start - Deleting asset with assetId = " + assetId);
		User createdUser = getCreatedUser(request);
		Boolean result = isAuthorized(createdUser.getUserId(), groupId, Constants.DELETE);
		if (!result) {
			return new ResponseEntity<String>("Not Authorized", HttpStatus.UNAUTHORIZED);
		}
		assetService.deleteAsset(assetId,createdUser);
		ResponseEntity<String> returnResponseEntity = new ResponseEntity<String>("Success", HttpStatus.OK);
		logger.info("deleteAsset(assetId=" + assetId + ", groupId=" + groupId + ", request=" + request + ") - end - Asset deleted at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}

	// ----------------------------code for AssetType
	// ---------------------------------------

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/createAssetType", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public void createAssetType(@RequestBody AssetTypeRequest assetType, HttpServletRequest request) {
		
		User createdUser = getCreatedUser(request);
		logger.info("createAssetType(assetType=" + assetType + ", request=" + request + ") -  Asset Type deleted by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		assetService.createAssetType(assetType,createdUser);
	}

	/*** Retrieve a single AssetType ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/assetType/{assetTypeId}", produces = "application/json", method = RequestMethod.GET)
	public AssetType getAssetTypeById(@PathVariable("assetTypeId") int assetTypeId) {
		logger.info("getAssetTypeById(assetTypeId=" + assetTypeId + ") - start - Retrieving asset using /assetType/{assetTypeId} api");
		AssetType assetType = assetService.getAssetTypeById(assetTypeId);
		return assetType;
	}

	// --- list of AssetTypes with pagination and search criteria -----------
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/assetTypes", method = RequestMethod.GET)
	public ResponseEntity<AssetResponse> getAllAssetTypes(@RequestParam(required = false) String sort_by,
			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
			@RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page_no,
			@RequestParam(required = false) String subType) {

		AssetResponse assetTypes = assetService.getAllAssetTypes(sort_by, order, searchCriteria, limit, page_no, subType);
		if (assetTypes==null) {
			ResponseEntity<AssetResponse> returnResponseEntity = new ResponseEntity<AssetResponse>(HttpStatus.NO_CONTENT);
			logger.info("getAllAssetTypes(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", subType=" + subType + ") - end - No AssetType created yet - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		ResponseEntity<AssetResponse> returnResponseEntity = new ResponseEntity<AssetResponse>(assetTypes, HttpStatus.OK);
		logger.info("getAllAssetTypes(sort_by=" + sort_by + ", order=" + order + ", searchCriteria=" + searchCriteria + ", limit=" + limit + ", page_no=" + page_no + ", subType=" + subType + ") - end - Fetching data for all assets - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}

	/*** Update a AssetType ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/updateAssetType/{assettypeId}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public void updateAssetType(@RequestBody AssetTypeRequest assetType, @PathVariable("assettypeId") int id,
			HttpServletRequest request) {
		User createdUser = getCreatedUser(request);
		logger.info("updateAssetType(assetType=" + assetType + ", id=" + id + ", request=" + request + ") -  Asset Type updated by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		assetService.updateAssetType(assetType,id,createdUser);
	}

	/*** Delete a AssetType ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteAssetType/{assetTypeId}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteAssetType(@PathVariable("assetTypeId") int assetTypeId,HttpServletRequest request,HttpServletResponse response) {
		User createdUser = getCreatedUser(request);
		logger.info("deleteAssetType(assetTypeId=" + assetTypeId + ", request=" + request + ", response=" + response + ") -  Asset Type deleted by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
		try{
		 assetService.deleteAssetType(assetTypeId);
		}catch(Exception ex){
			try {
				response.sendError(403, "Forbidden");
			} catch (IOException e) {
				logger.error("Error in deleting AssetType: "+e);
				e.printStackTrace();
			}
		}
	}

	/*** Creating a new Asset Reminder ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/createAssetReminder", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<ResponseModel> createAssetReminder(@RequestBody AssetRequest assetRequest,
			HttpServletRequest request) {
		ResponseModel response = new ResponseModel();
		try{
		User createdUser = getCreatedUser(request);

		Boolean result = isAuthorized(createdUser.getUserId(), assetRequest.getGroupId(), Constants.CREATE);
		if (!result) {
			return new ResponseEntity<ResponseModel>(response, HttpStatus.UNAUTHORIZED);
		}

			logger.info("createAssetReminder(assetRequest=" + assetRequest + ", request=" + request + ") -  Asset Reminder created by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " on" + curentDate.getCurrentDate());
			Asset asset = assetService.createAssetReminder(assetRequest, createdUser);
			logger.info("createAssetReminder(assetRequest=" + assetRequest + ", request=" + request + ") - Asset reminder created at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName());
			if (asset == null)
			response.setFailure("fail to create asset");
		else {
			response.setReminderId(asset.getReminder().getReminderId());
			User createdBy = userService.getUserById(asset.getReminder().getCreatedById());
			User lastModifiedBy = userService.getUserById(asset.getReminder().getLastModifiedById());
			response.setCreatedBy(createdBy.getUserName());
			response.setCreatedAt(asset.getReminder().getCreatedAt());
			response.setLastModifiedBy(lastModifiedBy.getUserName());
			response.setLastModifiedAt(asset.getReminder().getLastModifiedAt());
			response.setSuccess("sucessfuly inserted");
		}

		return new ResponseEntity<ResponseModel>(response, HttpStatus.OK);
		}catch(Exception e){
			ResponseEntity<ResponseModel> returnResponseEntity = new ResponseEntity<ResponseModel>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("createAssetReminder(assetRequest=" + assetRequest + ", request=" + request + ") - end - Error creating Contract Remonder - return value=" + returnResponseEntity, e);
			return returnResponseEntity;
		}

	}

	/** Api to get dates for Expire calendar 
	 * @throws ParseException */
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/expireCal", method = RequestMethod.GET, produces = "application/json")
	public List<Summary> getExpiryCalendar(HttpServletRequest request,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "date", required = false) Date date) throws ParseException {
		logger.info("getExpiryCalendar(request=" + request + ", date=" + date + ") - start - getting the dates expiring in current month");
		User loginUser = getCreatedUser(request);
		if (date == null) {
			date = new Date();
		}
	
		return assetService.getExpiryCalendar(loginUser.getUserId(),date);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/getGroupDetailByReminderId/{reminderId}", 
	produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<MyGroupDropDown> getGroupDetailByReminderId(@PathVariable("reminderId") int reminderId) {
		MyGroupDropDown myGroupDropDown = null;
		try {
			myGroupDropDown = assetService.getGroupDetailByReminderId(reminderId);
		} catch (Exception e) {
			return new ResponseEntity<MyGroupDropDown>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<MyGroupDropDown>(myGroupDropDown, HttpStatus.OK);
	}
	
	/*** searching Asset ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/searchAsset", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<List<AssetResponse>> searchAsset(
			@RequestParam(name = "sortBy", required = false) String sort_by,
			@RequestParam(name = "order", required = false) String order,
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "page_no", required = false) Integer page_no,
			@RequestParam(required = false) String searchCriteria,
			@RequestBody(required = false) AssetSearchCriteria assetSearchCriteria, HttpServletRequest request) {
		logger.info("searchAsset(sort_by=" + sort_by + ", order=" + order + ", limit=" + limit + ", page_no=" + page_no + ", searchCriteria=" + searchCriteria + ", assetSearchCriteria=" + assetSearchCriteria + ", request=" + request + ") - start - Searching Asset using searchAsset()");
		int createdUser = getCreatedUserForSearch(request);
		List<AssetResponse> assets = assetService.searchAsset(sort_by, order, limit, page_no, assetSearchCriteria,
				createdUser, searchCriteria);
		if (assets == null || assets.isEmpty()) {
			ResponseEntity<List<AssetResponse>> returnResponseEntity = new ResponseEntity<List<AssetResponse>>(HttpStatus.NO_CONTENT);
			logger.info("searchAsset(sort_by=" + sort_by + ", order=" + order + ", limit=" + limit + ", page_no=" + page_no + ", searchCriteria=" + searchCriteria + ", assetSearchCriteria=" + assetSearchCriteria + ", request=" + request + ") - end - No Asset found - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		return new ResponseEntity<List<AssetResponse>>(assets, HttpStatus.OK);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/searchByAssetType/{assetType}", 
	produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<List<AssetType>> searchByAssetType (@PathVariable("assetType") String assetType){
		logger.info("searchByAssetType(assetType=" + assetType + ") - start - AdvanceSearch By AssetType");
		List<AssetType> assets = assetService.searchByAssetType(assetType);
		if (assets == null || assets.isEmpty()) {
			ResponseEntity<List<AssetType>> returnResponseEntity = new ResponseEntity<List<AssetType>>(HttpStatus.NO_CONTENT);
			logger.info("searchByAssetType(assetType=" + assetType + ") - end - No Asset found - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		return new ResponseEntity<List<AssetType>>(assets, HttpStatus.OK);
 		
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/searchByAssetSubType/{assetSubType}", 
	produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<List<AssetType>> searchByAssetSubType (@PathVariable("assetSubType") String assetSubType){
		logger.info("searchByAssetSubType(assetSubType=" + assetSubType + ") - start - AdvanceSearch By AssetSubType");
		List<AssetType> assets = assetService.searchByAssetSubType(assetSubType);
		if (assets == null || assets.isEmpty()) {
			ResponseEntity<List<AssetType>> returnResponseEntity = new ResponseEntity<List<AssetType>>(HttpStatus.NO_CONTENT);
			logger.info("searchByAssetSubType(assetSubType=" + assetSubType + ") - end - No Asset found - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		return new ResponseEntity<List<AssetType>>(assets, HttpStatus.OK);
 		
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/searchByAssetID/{assetID}", 
	produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<List<Asset>> searchByAssetID (@PathVariable("assetID") String assetID){
		logger.info("searchByAssetID(assetID=" + assetID + ") - start - AdvanceSearch By assetID");
		List<Asset> assets = assetService.searchByAssetID(assetID);
		if (assets == null || assets.isEmpty()) {
			ResponseEntity<List<Asset>> returnResponseEntity = new ResponseEntity<List<Asset>>(HttpStatus.NO_CONTENT);
			logger.info("searchByAssetID(assetID=" + assetID + ") - end - No Asset found - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		return new ResponseEntity<List<Asset>>(assets, HttpStatus.OK);
 		
	}
}
