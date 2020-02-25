package com.reminder.service;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.reminder.model.FileUpload;


public interface FileService {
	

	public String uploadMultiple(FileUpload file, int id) ;

	public void deleteFile(int id);

	public List<FileUpload> getFileById(int id);

	public void downloadFile(HttpServletResponse response, int fileId) throws FileNotFoundException, IOException;

	public FileUpload getFileByFileId(int id);

	public void openPDF(HttpServletResponse response, String pdfPath) throws IOException;


}
