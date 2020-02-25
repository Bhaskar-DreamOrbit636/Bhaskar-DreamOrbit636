package com.reminder.service;

import java.util.Date;
import java.util.List;

import com.reminder.model.Asset;
import com.reminder.model.AssetType;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.AssetRequest;
import com.reminder.request.model.AssetSearchCriteria;
import com.reminder.request.model.AssetTypeRequest;
import com.reminder.response.model.AssetResponse;
import com.reminder.response.model.MyGroupDropDown;

public interface AssetService
{
    public void createAsset(Asset asset);
    
    public Asset createAssetReminder(AssetRequest assetRequest, User user);
    
    public AssetResponse getAssetById(int assetId);
    
    //public List<Asset> getAllAssets();
    
    public void updateAsset(AssetRequest asset,  Integer id, User createdUser);
    
    public void deleteAsset(int assetId, User createdUser);
    
    public List<AssetResponse> getAllAssets(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no, int userId);
    
    //------------------------- AssetType ------------------------------------
    
    public void createAssetType(AssetTypeRequest assetType, User createdUser);
    
    public AssetType getAssetTypeById(int assetTypeId);
    
    //public List<AssetType> getAllAssetTypes();
    
    public void updateAssetType(AssetTypeRequest assetType, int id, User createdUser);
    
    public void deleteAssetType(int assetTypeId);
    
    public AssetResponse  getAllAssetTypes(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no, String subType);

	public List<Summary> getExpiryCalendar(int userId, Date date);

	public MyGroupDropDown getGroupDetailByReminderId(int reminderId);

	public List<AssetResponse> searchAsset(String sort_by, String order, Integer limit, Integer page_no,
			AssetSearchCriteria assetSearchCriteria, int userId, String searchCriteria);

	public List<AssetType> searchByAssetType(String assetType);

	public List<AssetType> searchByAssetSubType(String assetSubType);

	public List<Asset> searchByAssetID(String assetID);


}
