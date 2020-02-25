package com.reminder.service;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.UserDAO;
import com.reminder.model.GrouproleUser;
import com.reminder.model.LdapUser;
import com.reminder.model.User;
import com.reminder.request.model.UserSearchCriteria;
import com.reminder.response.model.UserPopUp;
import com.reminder.response.model.UserResponse;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@PropertySource("classpath:/application.properties")
public class UserServiceImpl implements UserService
{
	@Autowired
    private UserDAO userDAO;
	
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
	private LdapTemplate ldapTemplate;
    
  /*  @Autowired
    private ActiveDirectoryLdapAuthenticationProvider a;
    
    @Value("${spring.ldap.userDnPatterns}")
	private String rootDn;
    
    @Value("${spring.ldap.domain}")
	private String domain;*/
    
	@Override
	public void createUser(User user, String userName) {
		userDAO.createUser(user, userName);
	}
    	

    @Override
    public User getUserById(int userId)
    {
		/*if (userId == 0) {
			return null;
		}*/
        return userDAO.getUserById(userId);
    }

    /*@Override
    public List<User> getAllUsers()
    {
        return userDAO.getAllUsers();
    }*/
    
    @Override
    public List<User> getAllUsers(String sort_by, String order, String searchCriteria, Integer limit, Integer page_no, UserSearchCriteria userSearchCriteria)
    {
        return userDAO.getAllUsers(sort_by, order, searchCriteria, limit, page_no, userSearchCriteria);
    }

    @Override
    public UserPopUp updateUser(User user, String userName, boolean validated )
	{
    	if(!validated){
			boolean active = userDAO.getActiveStatusOfUser(user.getUserId());
			if (active != user.getActive().booleanValue() && !user.getActive()) {
				UserPopUp message = userDAO.isUserEligibleForInactivation(user.getUserId());
				if(message!=null){
					return message;
				}
			}
		}
		userDAO.updateUser(user, userName);
		return null;
	}

    @Override
    public void deleteUser(int id)
    {
        userDAO.deleteUser(id);
    }

	@Override
	public void createUserGroup(int userId, GrouproleUser user) {
		userDAO.getUserById(userId);
		userDAO.createUserGroup(user);
		
	}
	
	@Override
	public List<LdapUser> getUserByUsername(String userName) {
		List<LdapUser> list = ldapTemplate.search(
		         query()
		         .attributes("cn","mail","displayNamePrintable","telephoneNumber","department")
		         .where("objectclass").is("person").and("CN").is(userName),
		          new UserAttributesMapper());
		if (list != null && !list.isEmpty()) {
			//return list.get(0);
			return list;
		}
		return null;
	}
	
	/*  private DirContextOperations getUserByUsername(DirContext context, String username)
	            throws NamingException {
	        SearchControls searchControls = new SearchControls();
	        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	        
	        //have to use plain username for sAMAccountName since bind principal adds domain
	        String searchFilter = "(&(objectClass=user)(|(userPrincipalName={0})(sAMAccountName={1})))";

	        String bindPrincipal = createBindPrincipal(username);
	        String searchRoot = rootDn ;

	        
	            try {
					return SpringSecurityLdapTemplate.searchForSingleEntryInternal(context,
					        searchControls, searchRoot, searchFilter,
					        new Object[] { bindPrincipal, username});
				} catch (javax.naming.NamingException e) {
					e.printStackTrace();
				}
	            return null;
	        }
	
	  String createBindPrincipal(String username) {
	        if (domain == null || username.toLowerCase().endsWith(domain)) {
	            return username;
	        }

	        return username + "@" + domain;
	    }*/
	  
	private class UserAttributesMapper implements AttributesMapper<LdapUser> {

		@Override
		public LdapUser mapFromAttributes(Attributes attributes) throws NamingException, javax.naming.NamingException {
			LdapUser user;
			if (attributes == null) {
				return null;
			}
			user = new LdapUser();
			user.setCn(attributes.get("cn").get().toString());
			if (attributes.get("uid") != null) {
				user.setUid(attributes.get("uid").get().toString());
			}
			if (attributes.get("sn") != null) {
				user.setSn(attributes.get("sn").get().toString());
			}
			if (attributes.get("mail") != null) {
				user.setEmail(attributes.get("mail").get().toString());
			}
			/*if (attributes.get("givenName") != null) {
				user.setGivenName(attributes.get("givenName").get().toString());
			}*/
			if (attributes.get("displayNamePrintable") != null) {
				user.setDisplayNamePrintable(attributes.get("displayNamePrintable").get().toString());
			}
			if (attributes.get("telephoneNumber") != null) {
				user.setTelephoneNumber(attributes.get("telephoneNumber").get().toString());
			}
			if (attributes.get("department") != null) {
				user.setDepartment(attributes.get("department").get().toString());
			}
			return user;
		}
	}
	
	@SuppressWarnings("unused")
	private class MultipleAttributesMapper implements AttributesMapper<String> {

		@Override
		public String mapFromAttributes(Attributes attrs) throws NamingException, javax.naming.NamingException {
			NamingEnumeration<? extends Attribute> all = attrs.getAll();
			StringBuffer result = new StringBuffer();
			result.append("\n Result { \n");
			while (all.hasMore()) {
				Attribute id = all.next();
				result.append(" \t |_  #" + id.getID() + "= [ " + id.get() + " ]  \n");
			}
			result.append("\n } ");
			return result.toString();
		}
	}
	
	@Override
	public User getUserByName(String userName) {
		return userDAO.getUserByName(userName);
	}
	
	@Override
	public int getUserIdByName(String userName) {
		return userDAO.getUserIdByName(userName);
	}

	@Override
	public long getAllUsersCount(String sort_by, String searchCriteria, UserSearchCriteria userSearchCriteria) {
		return userDAO.getAllUsersCount(sort_by, searchCriteria, userSearchCriteria);
	}
	
	@Override
	public boolean getUserByUserId(String userId){
		return userDAO.getUserByUserId(userId);
	}

	@Override
	public List<String> loadEmails(String emailId) {
		List<String>  emailList = userDAO.loadEmails(emailId);
		return emailList;
	}

	@Override
	public boolean getUserByUserName(String userName) {
		return userDAO.getUserByUserName(userName);
	}


	@Override
	public String getUserName(Integer createdById) {
		return userDAO.getUserName(createdById);
	}


	@Override
	public List<User> getAllActiveUsersGroup(String sort_by, String order, String searchC, Integer limit,
			Integer page_no) {
		 return userDAO.getAllActiveUsersGroup(sort_by,order,searchC,limit,page_no);
	}
	
	@Override
	public long getAllActiveUsersCount(String sort_by, String searchCriteria){
		return userDAO.getAllActiveUsersCount(sort_by,searchCriteria);
	}


	@Override
	public void unSuccessfullLoginDate(String userName) {
		  userDAO.unSuccessfullLoginDate(userName);
	}


	@Override
	public List<UserResponse> userAdvanceSearch(String search, String column) {
		return userDAO.userAdvanceSearch(search,column);
	}

}
