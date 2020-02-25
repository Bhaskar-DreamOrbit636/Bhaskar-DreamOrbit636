package com.reminder.dao;

import java.util.Date;
import java.util.List;

import com.reminder.model.Contract;
import com.reminder.model.ContractConfig;
import com.reminder.model.Contract_Has_Status;
import com.reminder.model.Contract_Status;
import com.reminder.model.Summary;
import com.reminder.model.User;
import com.reminder.request.model.ContractRequest;
import com.reminder.request.model.ContractSearchCriteria;
import com.reminder.response.model.Contracts;
import com.reminder.response.model.MyContractResponse;

public interface ContractDAO
{
    public int createContract(Contract contract, ContractRequest contractRequest, User createdUser);
    
    public Contract getContractById(int contractNumber);
    
    public void createContractConfig(ContractConfig contractConfig);
    
    public List<ContractConfig> getContractConfig();
    
    //public List<Contract> getAllContracts();
    
    public List<Contract> getAllContracts(String sortBy, boolean isVerified, String referenceNumber, String title, Integer offset,
			Integer numberOfRecords ,int userId);
    
    public void updateContract(Contract contract);
    
    public void deleteContract(int contractNumber);
    
   // public List<Contract> getAllContractsWithStatus(String searchCriteria);
    
    public void createContractHasStatus(Contract_Has_Status contractHasStatus);
    
    public List<Contract> getSingleContract(int contractId);
    
    //public Contract_Has_Status createContractStatus(Contract_Has_Status contractStatus);
    
    public Contract_Status getContractStatus(Integer contractStatusId);
    
    //public Contract contractCreate(Contract contract);

	public int getContractCount(String sortBy, boolean isVerified, String referenceNumber, String title, int userId);

	public void deleteContractPerm(Integer contractId, boolean deleteParent);
	
	public Contracts getAllContracts(int userId, String sort_by, String order, boolean isVerified, Integer limit,
			Integer page_no, ContractSearchCriteria contractSearchCriteria, String searchCriteria);

	public List<Summary> getContractExpireCalendar(int userId, Date date);

	public Contracts getContractReviewData(String sortBy, String order, boolean isVerified, Integer limit, Integer page_no, String searchCriteria, int userId);

	int createContract(Contract contract, User createdUser);

	public List<Integer> getUserBasedContractIds(int userId);

	public List<Integer> getUserBasedUnVerifiedContractIds(int userId);
	
	public void deleteContractReview(int contractId);

	public List<Contract> contractAdvanceSearch(String search, String column);

	public List<MyContractResponse> searchByOfficerInCharge(String name);

	//public Long getAllContractsCount(int userId, boolean isVerified, ContractSearchCriteria contractSearchCriteria, String searchCriteria);

	//public Long getContractReviewDataCount(int userId, boolean isVerified, String searchCriteria);
}