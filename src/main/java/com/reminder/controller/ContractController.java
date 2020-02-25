package com.reminder.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reminder.dao.ContractDAO;
import com.reminder.dao.GroupDAO;
import com.reminder.exception.NoContentException;
import com.reminder.model.Contract;
import com.reminder.model.ContractConfig;
import com.reminder.model.ResponseModel;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.Constants;
import com.reminder.request.model.ContractRequest;
import com.reminder.request.model.ContractSearchCriteria;
import com.reminder.response.model.ContractDropDownValue;
import com.reminder.response.model.ContractResponse;
import com.reminder.response.model.MyContractResponse;
import com.reminder.security.JwtTokenUtil;
import com.reminder.service.ContractService;
import com.reminder.service.UserService;
import com.reminder.utils.CurrentDate;

@RestController
public class ContractController
{
    @Autowired
    private ContractService contractService;
    
    @Autowired
    private GroupDAO groupDAO;
    
    @Autowired
    private ContractDAO contractDAO;
    
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserService userService;
	
	private final String CREATE ="Create";
	
	private final String DELETE ="Delete";
	
	private final String UPDATE ="Update";
    
    private Logger logger = Logger.getLogger(ContractController.class);
    
	/*** Retrieve a single Group ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/groupsroleaction/{groupId}", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<List<String>> getGroupRolesAction(@PathVariable("groupId") Integer groupId,
											                HttpServletRequest request) {
		User createdUser = null; 
		createdUser = getUserDetails(request, createdUser);
		try {
			if (groupId != null) {
				List<String> list = groupDAO.getGroupRolesAction(createdUser.getUserId(),groupId);
		        return new ResponseEntity<List<String>>(list, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.error("Error in fetching data for group with groupId :" + groupId);
		}
        return new ResponseEntity<List<String>>(HttpStatus.NO_CONTENT);
	}


	private User getUserDetails(HttpServletRequest request, User createdUser) {
		String header = request.getHeader("Authorization");
		if(!StringUtils.isEmpty(header) && header.startsWith("Bearer ")){
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserByName(username);
		}
		return createdUser;
	}
	
	
	public Boolean isAuthorized(int userId, int groupId,String permissionType) {
		List<String> list = groupDAO.getGroupRolesAction(userId, groupId);

		if (list.contains(permissionType)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;

	}
	
	public User getCreatedUser(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		User createdUser = null;
		if (header.startsWith("Bearer ")) {
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserByName(username);
		}
		return createdUser;
	}
    /*** Creating a new Contract Reminder 
     * @return ***/

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/createContractReminder", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<Object> createContractReminder(@RequestBody ContractRequest contract,
			HttpServletRequest request) {
		logger.info("createContractReminder(contract=" + contract + ", request=" + request + ") - start - Creating Reminder using createReminder()");
		try{
		ResponseModel response = new ResponseModel();

		User createdUser = getCreatedUser(request);
		Boolean result = isAuthorized(createdUser.getUserId(), contract.getGroupId(), CREATE);

		if (!result) {
			return new ResponseEntity<Object>("Not Authorized", HttpStatus.UNAUTHORIZED);
		}
		int reminderId = contractService.createContract(contract,createdUser);
			logger.info("createContractReminder(contract=" + contract + ", request=" + request + ") - Contract reminder created at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName());
		
		if (reminderId == 0)
			response.setFailure("fail to create contract");
		else {
			response.setReminderId(reminderId);
			response.setSuccess("sucessfuly inserted");
		}
		
		return new ResponseEntity<Object>(response, HttpStatus.OK);
		}catch(Exception e){
			ResponseEntity<Object> returnResponseEntity = new ResponseEntity<Object>("Exception", HttpStatus.INTERNAL_SERVER_ERROR);
			logger.error("createContractReminder(contract=" + contract + ", request=" + request + ") - end - Error creating Contract Remonder - return value=" + returnResponseEntity, e);
			return returnResponseEntity;
		}
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteContractReminder", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<Object> deleteContractReminder(@RequestBody ContractRequest contract,
			HttpServletRequest request) {
		logger.info("deleteContractReminder(contract=" + contract + ", request=" + request + ") - start - Creating Reminder using createReminder()");
		ResponseModel response = new ResponseModel();

		User createdUser = getCreatedUser(request);
		Boolean result = isAuthorized(createdUser.getUserId(), contract.getGroupId(), DELETE);

		if (!result) {
			return new ResponseEntity<Object>("Not Authorized", HttpStatus.UNAUTHORIZED);
		}
		int reminderId = contractService.deleteContractReminder(contract,createdUser);
		logger.info("deleteContractReminder(contract=" + contract + ", request=" + request + ") - Contract reminder deleted at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName());
		
		if (reminderId == 0)
			response.setFailure("fail to create contract");
		else {
			response.setReminderId(reminderId);
			response.setSuccess("sucessfuly inserted");
		}
		
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	

	// --- list of user with pagination and search criteria -----------
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/contracts", method = RequestMethod.GET)
	public ResponseEntity<MyContractResponse> getAllContracts(
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "numberOfRecords", required = false , defaultValue = "10") Integer numberOfRecords,
			@RequestParam(name = "offset", required = false , defaultValue = "0") Integer offset,
			@RequestParam(name = "isVerified", required = true) boolean isVerified,
			@RequestParam(name = "referenceNumber", required = false) String referenceNumber,
			@RequestParam(name = "title", required = false) String title, HttpServletRequest request) {

		User createdUser = null;
		createdUser = getUserDetails(request, createdUser);

		int count = contractService.getContractCount(sortBy, isVerified, referenceNumber, title,
				createdUser.getUserId());

		MyContractResponse myContractResponse = contractService.getAllContracts(sortBy, isVerified,
				referenceNumber, title, offset, numberOfRecords, createdUser.getUserId());

		myContractResponse.setCount(count);
		if (myContractResponse == null || CollectionUtils.isEmpty(myContractResponse.getContract())) {
			ResponseEntity<MyContractResponse> returnResponseEntity = new ResponseEntity<MyContractResponse>(HttpStatus.NO_CONTENT);
			logger.info("getAllContracts(sortBy=" + sortBy + ", numberOfRecords=" + numberOfRecords + ", offset=" + offset + ", isVerified=" + isVerified + ", referenceNumber=" + referenceNumber + ", title=" + title + ", request=" + request + ") - end - No user created yet - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		ResponseEntity<MyContractResponse> returnResponseEntity = new ResponseEntity<MyContractResponse>(myContractResponse, HttpStatus.OK);
		logger.info("getAllContracts(sortBy=" + sortBy + ", numberOfRecords=" + numberOfRecords + ", offset=" + offset + ", isVerified=" + isVerified + ", referenceNumber=" + referenceNumber + ", title=" + title + ", request=" + request + ") - end - Fetching data for all user - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}
    
    
   
    
    /*** Update a Contract 
     * @return ***/

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/updateContract/{id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public ResponseEntity<Object> updateContract(@RequestBody ContractRequest contract, @PathVariable("id") Integer id,
												  HttpServletRequest request) {
		
		Boolean result = true; 
		User createdUser = getCreatedUser(request);
		if(!contract.isResubmit())
		 result = isAuthorized(createdUser.getUserId(), contract.getGroupId(), UPDATE);

		if (!result) {
			return new ResponseEntity<Object>("Not Authorized", HttpStatus.UNAUTHORIZED);
		}
		ResponseModel response = new ResponseModel();
		logger.info("updateContract(contract=" + contract + ", id=" + id + ", request=" + request + ") - Updating contract : /updateContract");
		Integer reminderId = contractService.updateContract(contract, id ,createdUser);
		
		response.setReminderId(reminderId);
	    response.setSuccess("sucessfuly inserted");
		
		ResponseEntity<Object> returnResponseEntity = new ResponseEntity<Object>(response, HttpStatus.OK);
		logger.info("updateContract(contract=" + contract + ", id=" + id + ", request=" + request + ") - end - Contract updated at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}
    
    /*** Delete a Role 
     * @return ***/
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteContract/{contractId}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<String> deleteContract(@PathVariable("contractId") int contractNumber,
			@RequestParam(required = false) int groupId, HttpServletRequest request) {
		try{
		User createdUser = getCreatedUser(request);
		Boolean result = isAuthorized(createdUser.getUserId(), groupId, DELETE);

		if (!result) {
			return new ResponseEntity<String>("Not Authorized", HttpStatus.UNAUTHORIZED);
		}

		logger.info("deleteContract(contractNumber=" + contractNumber + ", groupId=" + groupId + ", request=" + request + ") - Deleting contracts with contractNumber = " + contractNumber);
		boolean value = contractService.deleteContract(contractNumber,createdUser);
		if (value) {
			return new ResponseEntity<String>("'No Reviewer available! Add reviewer with verify access", HttpStatus.FORBIDDEN);
		}
		ResponseEntity<String> returnResponseEntity = new ResponseEntity<String>("Success", HttpStatus.OK);
		logger.info("deleteContract(contractNumber=" + contractNumber + ", groupId=" + groupId + ", request=" + request + ") - end - Contract deleted at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName() + " - return value=" + returnResponseEntity);
		return returnResponseEntity;
		}catch(Exception e){
			logger.error("Error in deleting contract with contractId = " + contractNumber);
			return new ResponseEntity<String>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    
//    /*** Contracts to CSV 
//     * @throws IOException ***/
//	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
//	@RequestMapping(value = "/writeContractsToCSV", method = RequestMethod.POST, produces = "application/json")
//	public void getDataForCsv(@RequestParam(required = false) String sortBy,
//			@RequestParam(required = false) String order, @RequestParam(required = false) String searchCriteria,
//			@RequestParam(required = false) Integer numberOfRecords, @RequestParam(required = false) Integer offset,
//			@PathVariable(name = "isVerified") boolean isVerified,
//			@PathVariable(name = "referenceNumber") String referenceNumber, @PathVariable(name = "title") String title)
//			throws IOException {
//		String csvFile = "D:/Reminder365_AllCsvs/contract.csv";
//		FileWriter writer = new FileWriter(csvFile);
//		try {
//			List<Contract> contracts = contractService.getAllContracts(sortBy, isVerified, referenceNumber, title,
//					offset, numberOfRecords);
//			CsvUtils.writeLine(writer,
//					Arrays.asList("ContractNumber", "Description", "AgreedAmount", "EffectiveStartDate",
//							"EffectiveExpiryDate", "Supplier", "OfficerInCharge", "CreatedBy", "CreatedDate",
//							"LastModifiedBy", "LastModifiedDate", "StatusId", "DepartmentId", "ReminderId",
//							"ReminderIndicaterId"));
//
//			for (Contract contract : contracts) {
//
//				List<String> list = new ArrayList<>();
//				System.out.println("******************" + list.toString());
//
//				CsvUtils.writeLine(writer, list);
//				logger.info("CSV file created at location : " + csvFile);
//			}
//		} catch (IOException e) {
//			logger.info("CSV file couldn't be created for error : " + e);
//		}
//		writer.flush();
//		writer.close();
//	}
//    
    
   /* @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value = "/contractsWithStatus", method = RequestMethod.GET)
    public ResponseEntity<List<Contract>> getAllContractsWithStatus(@RequestParam(required = false) String status) {
    	
    	
        List<Contract> contracts = contractService.getAllContractsWithStatus(status);
        if(contracts.isEmpty()){
        	logger.info("No user created yet");
            return new ResponseEntity<List<Contract>>(HttpStatus.NO_CONTENT);
        }
        logger.info("Fetching data for all user");
        return new ResponseEntity<List<Contract>>(contracts, HttpStatus.OK);
    }*/
    
    // --- New api for one contract -----------
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value = "/contract/{contractId}", method = RequestMethod.GET, produces = { "application/json","application/xml" })
    public List<ContractResponse> getContractById(@PathVariable("contractId") int contractId, HttpServletResponse response) {
		logger.info("getContractById(contractId=" + contractId + ") - start - Fetching data for one contract");
		List<ContractResponse> contractResponseList = null;
		contractResponseList =  contractService.getSingleContract(contractId);
		if(contractResponseList == null){
			try {
				response.sendError(204,new NoContentException("No content").getMessage());
			} catch (IOException e) {
				
			}
		}
		return contractResponseList;
    }
    
    
    /**
     * @param contract
     * @param id
     * @param request
     * @return
     */
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/verifyContract/{id}", method=RequestMethod.PUT, 
            produces="application/json", consumes="application/json")
    public ResponseEntity<String> verifyContract(@RequestBody ContractRequest contract,
					    						 @PathVariable("id") Integer id,
					    						 HttpServletRequest request)
    {
		User createdUser = getCreatedUser(request);
		logger.info("verifyContract(contract=" + contract + ", id=" + id + ", request=" + request + ") - Contract reminder verified at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName());
    	Boolean result = isAuthorized(createdUser.getUserId(),contract.getGroupId(),Constants.VERIFY);

    	if(!result){
    		return new ResponseEntity<String>("Not Authorized", HttpStatus.UNAUTHORIZED);
    	}
    	boolean status = contractService.verifyContract(contract,id,createdUser);
    	if (status) {
    		return new ResponseEntity<String>("Success", HttpStatus.OK);
    	}
    	return new ResponseEntity<String>("This request cannot be modified", HttpStatus.NOT_ACCEPTABLE);
    }
    
    /**
     * @param contract
     * @param id
     * @param request
     * @return
     */
    @CrossOrigin(maxAge = 4800, allowCredentials = "false")
    @RequestMapping(value="/rejectContract/{id}", method=RequestMethod.PUT, 
            produces="application/json", consumes="application/json")
    public ResponseEntity<String> rejectContract(@RequestBody ContractRequest contract,@PathVariable("id") Integer id,
    						                     HttpServletRequest request) {
    	User createdUser = getCreatedUser(request);
		logger.info("rejectContract(contract=" + contract + ", id=" + id + ", request=" + request + ") - Contract reminder reject at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName());
    	Boolean result = isAuthorized(createdUser.getUserId(),contract.getGroupId(),Constants.VERIFY);

    	if(!result){
    		return new ResponseEntity<String>("Not Authorized", HttpStatus.UNAUTHORIZED);
    	}
    	boolean status = contractService.rejectContract(contract, id ,createdUser);
    	if (status) {
    		return new ResponseEntity<String>("Success", HttpStatus.OK);
    	}
    	return new ResponseEntity<String>("This request cannot be modified", HttpStatus.NOT_ACCEPTABLE);

    }
   
	/**
	 * @param groupId
	 * @param moduleTypeId
	 * @return
	 */
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/contractDropDownValue/{groupId}/{moduleTypeId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ContractDropDownValue> getAllContractDropDownValue(@PathVariable("groupId") int groupId,
			@PathVariable("moduleTypeId") int moduleTypeId, HttpServletRequest request) {
		User logInUser = getCreatedUser(request);
		ContractDropDownValue contractDropDownValues = contractService.getAllContractDropDownValue(groupId,
				moduleTypeId, logInUser.getUserId());
		ResponseEntity<ContractDropDownValue> returnResponseEntity = new ResponseEntity<ContractDropDownValue>(contractDropDownValues, HttpStatus.OK);
		logger.info("getAllContractDropDownValue(groupId=" + groupId + ", moduleTypeId=" + moduleTypeId + ", request=" + request + ") - end - Fetching data for UserGroup DropDown Value of Module Id " + moduleTypeId + " - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}
    
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteContractPerm/{contractId}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<String> deleteContractPerm(@PathVariable("contractId") Integer contractId,
													 @RequestParam(name = "deleteParent", required = false) boolean deleteParent,
													 HttpServletRequest request){
		if (contractId != null) {
			User createdUser = getCreatedUser(request);
			logger.info("deleteContractPerm(contractId=" + contractId + ", deleteParent=" + deleteParent + ", request=" + request + ") - Contract reminder deleted at" + (new CurrentDate()).getCurrentDate() + " by " + createdUser.getAdUserId() + "-" + createdUser.getUserName());
			try {
				contractService.deleteContractPerm(contractId ,deleteParent, createdUser);
			} catch (Exception e) {
				ResponseEntity<String> returnResponseEntity = new ResponseEntity<String>(HttpStatus.NO_CONTENT);
				logger.error("Error in deleting contract with contractId = " + contractId);
				return returnResponseEntity;
			}
			ResponseEntity<String> returnResponseEntity = new ResponseEntity<String>("Success", HttpStatus.OK);
			logger.info("deleteContractPerm(contractId=" + contractId + ", deleteParent=" + deleteParent + ", request=" + request + ") - end - Deleted contract with contract = " + contractId + " - return value=" + returnResponseEntity);
			return returnResponseEntity;
		}
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/contractWithActions", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<MyContractResponse> getContractWithActions(HttpServletRequest request,
			@RequestParam(name = "sortBy", required = false) String sort_by,
			@RequestParam(name = "order", required = false) String order,
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "page_no", required = false) Integer page_no,
			@RequestParam(name = "isVerified", required = true) boolean isVerified,
			@RequestParam(required = false) String searchCriteria,
			@RequestBody ContractSearchCriteria contractSearchCriteria) {
		int createdUser = 0;
		String header = request.getHeader("Authorization");
		if (header.startsWith("Bearer ")) {
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserIdByName(username);
		}
		MyContractResponse myContractResponse = contractService.getContractWithActions(createdUser,
				sort_by, order, isVerified, limit, page_no, contractSearchCriteria, searchCriteria);
		if (myContractResponse == null) {
			return new ResponseEntity<MyContractResponse>(myContractResponse, HttpStatus.NO_CONTENT);
		}
		ResponseEntity<MyContractResponse> returnResponseEntity = new ResponseEntity<MyContractResponse>(myContractResponse, HttpStatus.OK);
		logger.info("getContractWithActions(request=" + request + ", sort_by=" + sort_by + ", order=" + order + ", limit=" + limit + ", page_no=" + page_no + ", isVerified=" + isVerified + ", searchCriteria=" + searchCriteria + ", contractSearchCriteria=" + contractSearchCriteria + ") - end - Fetching data for Contract with Actions - return value=" + returnResponseEntity);
		return returnResponseEntity;
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/createContractConfig", method = RequestMethod.PUT, produces = "application/json")
	public void createContractConfig (@RequestBody ContractConfig config, HttpServletRequest request) {
		logger.info("createContractConfig(config=" + config + ") - start - creating contract config");
		User createdUser = getCreatedUser(request);
		config.setCreatedById(createdUser.getUserId());
		config.setLastModifiedById(createdUser.getUserId());
		contractService.createContractConfig(config);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/getContractConfig", 
	method = RequestMethod.GET, produces = "application/json")
	public List<ContractConfig> getContractConfig(){
			return contractService.getContractConfig();
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/contractExpireCalendar", method = RequestMethod.GET, produces = "application/json")
	public List<Summary> getContractExpireCalendar(HttpServletRequest request,
			@DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(name = "date", required = false) Date date) {
		logger.info("getContractExpireCalendar(request=" + request + ", date=" + date + ") - start - getting the dates expiring in current month");
		User createdUser = getCreatedUser(request);
		if (date == null) {
			date = new Date();
		}
		return contractService.getContractExpireCalendar(createdUser.getUserId(),date);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/revertContract/{contractId}", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<String> revertContract(@PathVariable("contractId") Integer contractId) {
		logger.info("revertContract(contractId=" + contractId + ") - start - calling revertContract for contractId :" + contractId);
		try {
			contractService.revertContract(contractId);
		} catch (Exception e) {
			ResponseEntity<String> returnResponseEntity = new ResponseEntity<String>("failure", HttpStatus.OK);
			logger.error("error in revertContract for contractId :" + contractId);
			return returnResponseEntity;
		}
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}
	
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/contractReviewData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<MyContractResponse> getContractReviewData(
			@RequestParam(name = "sortBy", required = false) String sortBy,
			@RequestParam(name = "order", required = false) String order,
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "page_no", required = false) Integer page_no,
			@RequestParam(name = "isVerified", required = true) boolean isVerified,
			@RequestParam(name = "searchCriteria", required = false) String searchCriteria,
			HttpServletRequest request) {
		logger.info("getContractReviewData(sortBy=" + sortBy + ", order=" + order + ", limit=" + limit + ", page_no=" + page_no + ", isVerified=" + isVerified + ", searchCriteria=" + searchCriteria + ", request=" + request + ") - start - getting the Contract Review Data");
		User createdUser = getCreatedUser(request);
		MyContractResponse myContractResponse = contractService.getContractReviewData(sortBy, order, isVerified, limit,
				page_no, searchCriteria, createdUser.getUserId());
		if (myContractResponse == null) {
			return new ResponseEntity<MyContractResponse>(myContractResponse, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<MyContractResponse>(myContractResponse, HttpStatus.OK);
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/AdvanceSearch/{search:.+}", produces = "application/json", method = RequestMethod.GET)
	public List<Contract> contractAdvanceSearch(@PathVariable("search") String search, @RequestParam(name = "column", required = true) String column,
			HttpServletResponse response) {
		logger.info("contractAdvanceSearch(search=" + search + ", column=" + column + ", response=" + response + ") - start - Retrieving Refrence number for advance search");
		List<Contract> contract = contractService.contractAdvanceSearch(search, column);
		if(contract==null){
			try {
				response.sendError(204,new NoContentException("No content").getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Erro in searching: "+e);
			}
		}
		return contract;
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials="false")
	@RequestMapping(value = "/searchByOfficerInCharge/{officerInchargeId}", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<List<MyContractResponse>> searchByOfficerInCharge(@PathVariable("officerInchargeId") String name, HttpServletResponse response){
		logger.info("searchByOfficerInCharge(name=" + name + ", response=" + response + ") - start - advance search for officer in charge");
		List<MyContractResponse> userName = contractService.searchByOfficerInCharge(name);
		if(userName == null)
				return new ResponseEntity<List<MyContractResponse>>(userName,HttpStatus.NO_CONTENT);
		return new ResponseEntity<List<MyContractResponse>>(userName,HttpStatus.OK);
	}
	
}