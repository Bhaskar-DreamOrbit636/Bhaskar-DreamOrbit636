package com.reminder.dao;

import java.util.Date;
import java.util.List;

import com.reminder.model.Asset;
import com.reminder.model.AssetType;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.AssetSearchCriteria;
import com.reminder.response.model.AssetResponse;
import com.reminder.response.model.MyGroupDropDown;

public interface AssetDAO
{
    public void createAsset(Asset asset);
    
    public Asset createAssetReminder(Asset asset);
    
    public AssetResponse getAssetById(int assetId);
    
    //public List<Asset> getAllAssets();
    
    public List<AssetResponse> getAllAssets(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no, int userId);
    
    public void updateAsset(Asset asset);
    
    public void deleteAsset(int assetId, User user);
    
    //-------------------- AssetType ---------------------------
    
    public void createAssetType(AssetType assetType);
    
    public AssetType getAssetTypeById(int assetTypeId);
    
    public AssetResponse getAllAssetTypes(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no, String subType);
    
    public void updateAssetType(AssetType assetType);
    
    public void deleteAssetType(int assetTypeId);

	public List<Summary> getExpiryCalendar(int userId, Date date);

	public MyGroupDropDown getGroupDetailByReminderId(int reminderId);

	public List<AssetResponse> searchAsset(String sort_by, String order, Integer limit, Integer page_no,
			AssetSearchCriteria assetSearchCriteria, int userId, String searchCriteria);

	public Asset getAssetByIdLocal(Integer id);

	public List<Integer> getUserBasedAssetIds(int userId);

	public List<AssetType> searchByAssetType(String assetType);

	public List<AssetType> searchByAssetSubType(String assetSubType);

	public List<Asset> searchByAssetID(String assetID);

}
