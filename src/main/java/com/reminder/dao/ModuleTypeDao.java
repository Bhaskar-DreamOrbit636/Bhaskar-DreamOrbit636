package com.reminder.dao;

import java.util.List;

import com.reminder.model.ModuleType;


public interface ModuleTypeDao
{
    public void createModuleType(ModuleType group);
    
    public ModuleType getModuleTypeId(int groupId);
    
    public List<ModuleType> getAllModuleType(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no);
    
    public void updateModuleType(ModuleType group);
    
    public void deleteModuleType(int groupId);

	ModuleType getModuleType(String moduleName);
    
}
