package com.reminder.request.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AssignRoleRequest {
	
	private static Logger logger = Logger.getLogger(AssignRoleRequest.class);
	private int roleId;
	private int groupId;
	private int moduleTypeId;
	private List<Integer> userIds;
	private int createdUserId;
	
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getModuleTypeId() {
		return moduleTypeId;
	}
	public void setModuleTypeId(int moduleTypeId) {
		this.moduleTypeId = moduleTypeId;
	}
	public List<Integer> getUserIds() {
		return userIds;
	}
	public void setUserIds(List<Integer> userIds) {
		this.userIds = userIds;
	}
	
	   public static void main(String[] a){
	         
		   AssignRoleRequest assignRoleRequestnew = new AssignRoleRequest();
		   
		   assignRoleRequestnew.setGroupId(1);
			assignRoleRequestnew.setModuleTypeId(1);
			assignRoleRequestnew.setRoleId(7);
			assignRoleRequestnew.setModuleTypeId(1);
			
			List<Integer> l = new ArrayList<>();
			l.add(new Integer(4));
			assignRoleRequestnew.setUserIds(l);
	        ObjectMapper mapperObj = new ObjectMapper();
	         
	        try {
	            // get Employee object as a json string
	            String jsonStr = mapperObj.writeValueAsString(assignRoleRequestnew);
	            System.out.println(jsonStr);
	        } catch (IOException e) {
	            logger.error("Erorr in AssignRoleRequest: "+e);
	            e.printStackTrace();
	        }
	    }
	public int getCreatedUserId() {
		return createdUserId;
	}
	public void setCreatedUserId(int createdUserId) {
		this.createdUserId = createdUserId;
	}
	
}
