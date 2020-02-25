package com.reminder.dao;

import java.util.List;

import com.reminder.model.FileUpload;

public interface FileDAO {


	public void deleteFile(int id) ;
	
	public String uploadMultiple(FileUpload file) ;

	public List<FileUpload> getFileById(int id);

	public FileUpload getFileByFileId(int fileId);
	
}
