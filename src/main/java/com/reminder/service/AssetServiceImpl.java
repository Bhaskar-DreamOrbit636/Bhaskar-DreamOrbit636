package com.reminder.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.AssetDAO;
import com.reminder.dao.GroupDAO;
import com.reminder.model.Asset;
import com.reminder.model.AssetType;
import com.reminder.model.Groups;
import com.reminder.model.Location;
import com.reminder.model.ModuleType;
import com.reminder.model.Reminder;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.AssetRequest;
import com.reminder.request.model.AssetSearchCriteria;
import com.reminder.request.model.AssetTypeRequest;
import com.reminder.request.model.ReminderRequest;
import com.reminder.response.model.AssetResponse;
import com.reminder.response.model.MyGroupDropDown;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AssetServiceImpl implements AssetService
{
	@Autowired
    private AssetDAO assetDAO;
	
	@Autowired
    private GroupDAO groupDAO;
	
	@Autowired
	private ReminderService reminderService;
	
    @Override
    public void createAsset(Asset asset)
    {
    	assetDAO.createAsset(asset);
    }
    
    @PersistenceContext
	private EntityManager entityManager;
    
    @Override
    public Asset createAssetReminder(AssetRequest assetRequest, User user)
    {
    	entityManager.clear();
    	Asset entity = new Asset();
 
    	Groups group = groupDAO.getGroupById(assetRequest.getGroupId());
    	//entity.setUserGroupId(group);
    	
    	Location assetLocation = entityManager.find(Location.class,assetRequest.getLocationId()); 
    	entity.setLocation(assetLocation);
    	
    	ModuleType moduleType = entityManager.find(ModuleType.class,assetRequest.getModuleTypeId()); 
    	entity.setModuleType(moduleType);
    	
    	AssetType assetType = entityManager.find(AssetType.class,assetRequest.getAssetTypeId()); 
    	entity.setAssetType(assetType);
    	   	
    	
    	//Location assetLocation = entityManager.find(Location.class,assetRequest.getLocationId()); 
    	/*Location assetLocation =new Location();
    	assetLocation.setLocationId(assetRequest.getLocationId());
    	System.out.println(assetLocation.getLocationId()+"==========");
    			//entityManager.createQuery("select l from Location l where locationId= :locationId").setParameter("locationId",assetRequest.getLocationId() ).getResultList().get(0));
    	entity.setLocation(assetLocation);*/
    	
    	/*AssetType assetType = entityManager.find(AssetType.class, entityManager.createQuery("select at.assetTypeId from AssetType at where assetType= :assetType").setParameter("assetType",assetRequest.getAssetTypeId()).getResultList().get(0));
    	entity.setAssetType(assetType);*/
    	entity.setAssetDescription(assetRequest.getAssetDescription());
    	entity.setAssetId(assetRequest.getAssetId());
    	entity.setId(assetRequest.getId());
    	//entity.setCcList(assetRequest.getCcList());
    	//entity.setRemarks(assetRequest.getRemarks());
    	entity.setAdditionalCcList(assetRequest.getAdditionalCcList());
    	//entity.setToList(assetRequest.getToList());
    
    	
    	ReminderRequest reminder = assetRequest.getReminder();
	   	Reminder reminderEntity = new Reminder();
	   	
    	reminderEntity.setUserGroupId(group);
    	reminderEntity.setEffectiveStartDate(reminder.getEffectiveStartDate());;
    	reminderEntity.setEffectiveExpiryDate(reminder.getEffectiveExpiryDate());
    	reminderEntity.setRemarks(reminder.getRemarks());
    	reminderEntity.setFirstReminderDate(reminder.getFirstReminderDate());
    	reminderEntity.setSecondReminderDate(reminder.getSecondReminderDate());
    	reminderEntity.setThirdReminderDate(reminder.getThirdReminderDate());
    	reminderEntity.setActive(reminder.getActive());
    	reminderEntity.setCreatedById(user.getUserId());
    	reminderEntity.setLastModifiedById(user.getUserId());
    	//reminderEntity.setStatusId(reminder.getStatusId());
    	reminderEntity.setAddCcListExpiryReminder(reminder.getAddCcListExpiryReminder());
    	reminderEntity.setAddCcListLastReminder(reminder.getAddCcListLastReminder());
    	//reminderEntity.setCcListExpiryReminder(reminder.getCcListExpiryReminder());
    	//reminderEntity.setCcListLastReminder(reminder.getCcListLastReminder());
    	Reminder reminderSaved = entityManager.merge(reminderEntity);
    	entity.setReminder(reminderSaved);
    	
    	Asset as = assetDAO.createAssetReminder(entity);
		return as;
    	
    }


    @Override
    public AssetResponse getAssetById(int assetId)
    {
        return assetDAO.getAssetById(assetId);
    }

    /*@Override
    public List<Asset> getAllAssets()
    {
        return assetDAO.getAllAssets();
    }*/
    
    /* (non-Javadoc)
     * @see com.reminder.service.AssetService#getAllAssets(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, int)
     */
    @Override
    public List<AssetResponse> getAllAssets(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no ,int userId)
    {
    	List<AssetResponse> assetsList = assetDAO.getAllAssets(sort_by,order,searchCriteria,limit,page_no,userId);
    	return setActions(assetsList,userId);
    }

	/**
	 * @param contracts
	 * @param userId
	 * @return
	 */
	private List<AssetResponse> setActions(List<AssetResponse> responses, int userId) {
		for (AssetResponse contractUserObj : responses) {
			if (contractUserObj.getGroupId()!=0) {
				contractUserObj.setActions(groupDAO.getGroupRolesAction(userId, contractUserObj.getGroupId()));
			}
		}
		return responses;
	}
	
    @Override
    public void updateAsset(AssetRequest assetRequest,Integer id, User user)
    {
    	    entityManager.clear();
    	    Asset entity = assetDAO.getAssetByIdLocal(id);
    	    
        	Groups group = groupDAO.getGroupById(assetRequest.getGroupId());
        	//entity.setUserGroupId(group);
        	
        	Location assetLocation = entityManager.find(Location.class,assetRequest.getLocationId()); 
        	entity.setLocation(assetLocation);
        	
        	ModuleType moduleType = entityManager.find(ModuleType.class,assetRequest.getModuleTypeId()); 
        	entity.setModuleType(moduleType);
        	
        	AssetType assetType = entityManager.find(AssetType.class,assetRequest.getAssetTypeId()); 
        	entity.setAssetType(assetType);
        	   	
        	entity.setAssetDescription(assetRequest.getAssetDescription());
        	entity.setAssetId(assetRequest.getAssetId());
        	entity.setId(assetRequest.getId());
        	//entity.setCcList(assetRequest.getCcList());
        	//entity.setRemarks(assetRequest.getRemarks());
        	entity.setAdditionalCcList(assetRequest.getAdditionalCcList());
        	//entity.setToList(assetRequest.getToList());
         
        	
        	if(assetRequest.getReminder() != null){
        	Reminder reminder = reminderService.getReminderById(entity.getReminder().getReminderId());
        	reminder.setUserGroupId(group);
        	reminder.setLastModifiedById(user.getUserId());
        	reminder.setActive(assetRequest.getReminder().getActive());
        	reminder.setEffectiveStartDate(assetRequest.getReminder().getEffectiveStartDate());;
        	reminder.setRemarks(assetRequest.getReminder().getRemarks());
        	reminder.setFirstReminderDate(assetRequest.getReminder().getFirstReminderDate());
        	reminder.setSecondReminderDate(assetRequest.getReminder().getSecondReminderDate());
        	reminder.setThirdReminderDate(assetRequest.getReminder().getThirdReminderDate());
        	//reminder.setStatusId(assetRequest.getReminder().getStatusId());
        	reminder.setAddCcListExpiryReminder(assetRequest.getReminder().getAddCcListExpiryReminder());
        	reminder.setAddCcListLastReminder(assetRequest.getReminder().getAddCcListLastReminder());
        	//reminder.setCcListExpiryReminder(assetRequest.getReminder().getCcListExpiryReminder());
        	//reminder.setCcListLastReminder(assetRequest.getReminder().getCcListLastReminder());
        	
        	//if( reminder.getEffectiveExpiryDate() != null && assetRequest.getReminder().getEffectiveExpiryDate() != null){
        	if((assetRequest.getReminder().getEffectiveExpiryDate()).compareTo(reminder.getEffectiveExpiryDate())!=0){
        		reminder.setFirstReminderSentAt(null);
        		reminder.setSecondReminderSentAt(null);
        		reminder.setThirdReminderSentAt(null);
        	}
        	//}
        	
        	reminder.setEffectiveExpiryDate(assetRequest.getReminder().getEffectiveExpiryDate());
        	reminderService.updateReminder(reminder);
        	}        	
        	entity.setAssetId(id);
        	assetDAO.updateAsset(entity);
	}
    

	@Override
    public void deleteAsset(int assetId, User user)
    {
   /* 	List<FileUpload> fileId=null;
    	Asset as = assetDAO.getAssetById(assetId);
    	int rid = as.getReminder().getReminderId();
    	fileId = entityManager.createNativeQuery("select a.* from files a where a.reminder_id=:reminderID",FileUpload.class)
				.setParameter("reminderID",rid).getResultList();
    	for(FileUpload fid:fileId){
    		int id = fid.getFileId();
    		filedao.deleteFile(id);
    	}*/
    	assetDAO.deleteAsset(assetId, user);
    }
    
    // ----------------------------------- AssetType -----------------------------------------
    
    @Override
    public void createAssetType(AssetTypeRequest assetTypeReq, User createdUser)
    {
    	AssetType assetType = new AssetType();
    	if(assetTypeReq.getParentAssetTypeId()==0){
    		assetType.setAssetType(assetTypeReq.getAssetType());
        	assetType.setActive(assetTypeReq.getActive());
        	assetType.setFirstReminderDay(assetTypeReq.getFirstReminderDay());
        	assetType.setSecondReminderDay(assetTypeReq.getSecondReminderDay());
        	assetType.setThirdReminderDay(assetTypeReq.getThirdReminderDay());
        	assetType.setCreatedById(createdUser.getUserId());
        	assetType.setLastModifiedById(createdUser.getUserId());
        	assetDAO.createAssetType(assetType);
    	}else{
    		assetType.setAssetType(assetTypeReq.getAssetType());
        	assetType.setActive(assetTypeReq.getActive());
        	assetType.setFirstReminderDay(assetTypeReq.getFirstReminderDay());
        	assetType.setSecondReminderDay(assetTypeReq.getSecondReminderDay());
        	assetType.setThirdReminderDay(assetTypeReq.getThirdReminderDay());
        	assetType.setCreatedById(createdUser.getUserId());
        	assetType.setLastModifiedById(createdUser.getUserId());
        	AssetType Parent = assetDAO.getAssetTypeById(assetTypeReq.getParentAssetTypeId());
        	assetType.setParentAssetType(Parent);
        	assetDAO.createAssetType(assetType);	
    	}
    }

    @Override
    public AssetType getAssetTypeById(int assetTypeId)
    {
        return assetDAO.getAssetTypeById(assetTypeId);
    }

    /*@Override
    public List<AssetType> getAllAssetTypes()
    {
        return assetDAO.getAllAssetTypes();
    }*/
    
    @Override
    public AssetResponse getAllAssetTypes(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no,String subType)
    {
        return assetDAO.getAllAssetTypes(sort_by,order,searchCriteria,limit,page_no,subType);
    }

    @Override
    public void updateAssetType(AssetTypeRequest assetTypeReq, int id, User createdUser)
    {
    	AssetType assetType = assetDAO.getAssetTypeById(id);
    	if(assetTypeReq.getParentAssetTypeId()==0){
    		assetType.setAssetType(assetTypeReq.getAssetType());
        	assetType.setActive(assetTypeReq.getActive());
        	assetType.setFirstReminderDay(assetTypeReq.getFirstReminderDay());
        	assetType.setSecondReminderDay(assetTypeReq.getSecondReminderDay());
        	assetType.setThirdReminderDay(assetTypeReq.getThirdReminderDay());
        	assetType.setCreatedById(assetType.getCreatedById());
        	assetType.setLastModifiedById(createdUser.getUserId());
        	assetDAO.createAssetType(assetType);
    	}else{
    		assetType.setAssetType(assetTypeReq.getAssetType());
        	assetType.setActive(assetTypeReq.getActive());
        	assetType.setFirstReminderDay(assetTypeReq.getFirstReminderDay());
        	assetType.setSecondReminderDay(assetTypeReq.getSecondReminderDay());
        	assetType.setThirdReminderDay(assetTypeReq.getThirdReminderDay());
        	assetType.setCreatedById(createdUser.getUserId());
        	assetType.setLastModifiedById(createdUser.getUserId());
        	AssetType Parent = assetType.getParentAssetType();
        	assetType.setParentAssetType(Parent);
        	assetDAO.createAssetType(assetType);	
    	}
    	assetDAO.updateAssetType(assetType);
    }

    @Override
    public void deleteAssetType(int assetTypeId)
    {
//    	String Checkasset = "select count(asset_type_id) from asset_type where asset_type_id="+assetTypeId;
//		Integer count = (Integer) entityManager.createNativeQuery(Checkasset).getSingleResult();
//		if(count>0)
    	 assetDAO.deleteAssetType(assetTypeId);
    }

	@Override
	public List<Summary> getExpiryCalendar(int userId ,Date date) {
		return assetDAO.getExpiryCalendar(userId,date);
	}

	@Override
	public MyGroupDropDown getGroupDetailByReminderId(int reminderId) {
		return assetDAO.getGroupDetailByReminderId(reminderId);
	}

	@Override
	public List<AssetResponse> searchAsset(String sort_by, String order, Integer limit, Integer page_no,
			AssetSearchCriteria assetSearchCriteria, int userId, String searchCriteria) {
		
		List<AssetResponse> assetsList = assetDAO.searchAsset(sort_by, order, limit, page_no, assetSearchCriteria, userId, searchCriteria);
		if(sort_by.equals("location")){
	        Collections.sort(assetsList, new SortbyLocation());
		}
		return assetsList;
	}

	class SortbyLocation implements Comparator<AssetResponse> {
		public int compare(AssetResponse a, AssetResponse b) {
			return a.getAsset().getLocation().getLocationName().compareTo(b.getAsset().getLocation().getLocationName());
		}
	}

	@Override
	public List<AssetType> searchByAssetType(String assetType) {
		List<AssetType> assetsList = assetDAO.searchByAssetType(assetType);
		return assetsList;
	}


	@Override
	public List<AssetType> searchByAssetSubType(String assetSubType) {
		List<AssetType> assetsList = assetDAO.searchByAssetSubType(assetSubType);
		return assetsList;
	}


	@Override
	public List<Asset> searchByAssetID(String assetID) {
		List<Asset> assetsList = assetDAO.searchByAssetID(assetID);
		return assetsList;
	}
}
