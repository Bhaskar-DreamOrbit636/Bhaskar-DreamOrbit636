package com.reminder.service;

import java.util.Date;
import java.util.List;

import com.reminder.model.Contract;
import com.reminder.model.ContractConfig;
import com.reminder.model.Contract_Has_Status;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.ContractRequest;
import com.reminder.request.model.ContractSearchCriteria;
import com.reminder.response.model.ContractDropDownValue;
import com.reminder.response.model.ContractResponse;
import com.reminder.response.model.MyContractResponse;


public interface ContractService
{
    public int createContract(ContractRequest contract, User createdUser);
    
    public Contract getContractById(int contractNumber);
    
    //public void createContractConfig(ContractConfig contractConfig);
    
   // public List<ContractConfig> getContractConfig();
    
    //public List<Contract> getAllContracts();
    
    public Integer updateContract(ContractRequest contract, Integer id, User createdUser);
    
    public boolean deleteContract(int contractNumber, User createdUser);
    
    public MyContractResponse getAllContracts(String sortBy, boolean isVerified, String referenceNumber, String title, Integer offset,
			Integer numberOfRecords ,int userId);
    
    //public List<Contract> getAllContractsWithStatus(String searchCriteria);
    
    public void createContractHasStatus(Contract_Has_Status contractHasStatus);
    
    public List<ContractResponse> getSingleContract(int contractId);
    
    public Contract_Has_Status contractHasStatus(Contract_Has_Status contractHasStatus);
    
    public boolean verifyContract(ContractRequest contract,Integer id, User createdUser);
    
    public boolean rejectContract(ContractRequest contract,Integer id, User createdUser);

	public ContractDropDownValue getAllContractDropDownValue(int groupId, int moduleTypeId, int userId);

	///List<ContractConfig> getContractConfig();

    public int getContractCount(String sortBy, boolean isVerified, String referenceNumber, String title, int userId);

	public MyContractResponse getContractWithActions(int userId, String sort_by, String order, boolean isVerified,
			Integer limit, Integer page_no, ContractSearchCriteria contractSearchCriteria, String searchCriteria);

	public void createContractConfig(ContractConfig config);

	public List<ContractConfig> getContractConfig();

	public List<Summary> getContractExpireCalendar(int userId, Date date);

	public MyContractResponse getContractReviewData(String sortBy, String order, boolean isVerified, Integer limit, Integer page_no, String searchCriteria, int userId);

	public int deleteContractReminder(ContractRequest contract, User createdUser);

	public void revertContract(Integer contractId);

	public void deleteContractPerm(Integer contractId, boolean deleteParent, User createdUser);

	public List<Contract> contractAdvanceSearch(String search, String column);

	public List<MyContractResponse> searchByOfficerInCharge(String name);
}
