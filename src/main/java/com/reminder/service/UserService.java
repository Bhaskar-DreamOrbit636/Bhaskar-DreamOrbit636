package com.reminder.service;

import java.util.List;

import com.reminder.model.GrouproleUser;
import com.reminder.model.LdapUser;
import com.reminder.model.User;
import com.reminder.request.model.UserSearchCriteria;
import com.reminder.response.model.UserPopUp;
import com.reminder.response.model.UserResponse;

public interface UserService
{
    public void createUser(User user, String userName);
    
    public void createUserGroup(int userId,GrouproleUser user);
    
    public User getUserById(int id);
    
    //public List<User> getAllUsers();
    
    public UserPopUp updateUser(User user, String userName, boolean validated);
    
    public void deleteUser(int id);
    
    public User getUserByName(String userName);

	public List<User> getAllUsers(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no, UserSearchCriteria userSearchCriteria);

	List<LdapUser> getUserByUsername(String userName);
	
	//public User getUserByName(String userName);

	long getAllUsersCount(String sort_by, String userName, UserSearchCriteria userSearchCriteria);
	
	public boolean getUserByUserId(String userId);

	public List<String> loadEmails(String emailId);

	public boolean getUserByUserName(String userName);

	public String getUserName(Integer createdById);
	
	public List<User> getAllActiveUsersGroup(String sort_by, String order, String searchC, Integer limit, Integer page_no);

	long getAllActiveUsersCount(String sort_by, String searchCriteria);

	public void unSuccessfullLoginDate(String userName);

	int getUserIdByName(String userName);

	public List<UserResponse> userAdvanceSearch(String search, String column);
}
