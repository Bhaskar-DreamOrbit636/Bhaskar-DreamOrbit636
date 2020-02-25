package com.reminder.dao;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.model.FileUpload;


@Repository
public class FileDAOImpl implements FileDAO{

	private Logger logger = Logger.getLogger(FileDAOImpl.class);
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
	public void deleteFile(int id) {
		try{
		logger.info("deleting the file :"+id);
		FileUpload file = entityManager.find(FileUpload.class, id);
		File del = new File(file.getFilePath()+File.separator+file.getFileNameOrg()+".enc");
		del.delete();
		String deleteFiles = "delete from files where file_id = "+id;
		entityManager.createNativeQuery(deleteFiles).executeUpdate();
		}catch(Exception e){
			logger.error("Error in deleteing the file: "+e);
			entityManager.flush();
			entityManager.close();
		}
	}

	@Override
	public String uploadMultiple(FileUpload file) {
		logger.info("inserting in the file ");
		entityManager.clear();
		try{
		entityManager.merge(file);
		entityManager.flush();
		entityManager.close();
		}catch(Exception e){
			logger.error("Error in uploading multiple files: "+e);
			entityManager.flush();
			entityManager.close();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FileUpload> getFileById(int id) {
		logger.info("getting the file by id ");
		//List<FileUpload> fileupload = new ArrayList<>();
		List<FileUpload> fileupload =null;
		try{
		 fileupload = entityManager.createNativeQuery
				("select a.* from files a where a.reminder_id=:reminderID",FileUpload.class)
				.setParameter("reminderID",id).getResultList();
		 
		 //removing the timestamp from file name
		/* for(FileUpload fu: fileupload ){
			 String filewithTime = fu.getFileName();
			 String file = filewithTime.substring(0,filewithTime.lastIndexOf("_"));
			 String filewithout = file + "." + fu.getFile_format();
			// fu.setFileName(filewithout);
		 }*/
		}catch (Exception e) {
			logger.error("Error in getting file by reminder id: "+e);
			entityManager.flush();
			entityManager.close();
		}
		return fileupload;
	}
	
	@Override
	public FileUpload getFileByFileId(int fileId) {
		logger.info("Getting the file by fileId");
		FileUpload fileupload = null;
		try {
			fileupload = (FileUpload) entityManager
					.createNativeQuery("select f.* from files f where f.file_id=:fileID", FileUpload.class)
					.setParameter("fileID", fileId).getSingleResult();
			fileupload.setFileName(fileupload.getFileNameOrg());
		} catch (NoResultException e) {
			logger.error("Error in getFileByFileID: "+e);
			entityManager.flush();
			entityManager.close();
			return null;
		} catch (Exception e) {
			logger.error("Error in getFileByFileID: "+e);
			entityManager.flush();
			entityManager.close();
		}
		return fileupload;
	}
	
	
}
