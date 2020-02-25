package com.reminder.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.reminder.dao.FileDAO;
import com.reminder.dao.ReminderDAO;
import com.reminder.dao.RoleDAOImpl;
import com.reminder.model.FileUpload;
import com.reminder.model.Reminder;
import com.reminder.utils.NRICSecurity;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class FileServiceImpl implements FileService {

	private Logger logger = Logger.getLogger(FileServiceImpl.class);

	@Autowired
	private FileDAO fileDAO;

	@Autowired
	private ReminderDAO reminderDao;

	@Override
	public String uploadMultiple(FileUpload file, int id) {
		try {
			Reminder rem = reminderDao.getReminderById(id);
			file.setReminders(rem);
			return fileDAO.uploadMultiple(file);
		} catch (Exception e) {
			logger.error("Error in uploading file" + e);
			return "File upload fails";
		}
	}

	@Override
	public void deleteFile(int id) {
		fileDAO.deleteFile(id);

	}

	@Override
	public List<FileUpload> getFileById(int id) {
		return fileDAO.getFileById(id);

	}

	@Override
	public void downloadFile(HttpServletResponse response, int fileId) throws FileNotFoundException, IOException {
		FileUpload fileUpload = fileDAO.getFileByFileId(fileId);
		if (fileUpload != null) {
			try {
				new NRICSecurity().decrypt(fileUpload.getFilePath() + "/" + fileUpload.getFileNameOrg() + ".enc",
						fileUpload.getFilePath() + "/" + fileUpload.getFileNameOrg());
			} catch (Exception e) {
				logger.error("Erro in decrypting the file= " + fileUpload.getFileNameOrg());
			}
			Path file = Paths.get(fileUpload.getFilePath(), fileUpload.getFileNameOrg());
			if (Files.exists(file)) {
				try {
					String contentType = Files.probeContentType(file);
					response.setContentType(contentType);
					response.setHeader("Content-disposition", "attachment; filename=" + fileUpload.getFileName());
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
					NRICSecurity.DeleteFile(fileUpload.getFilePath() + "/" + fileUpload.getFileNameOrg());
				} catch (IOException ex) {
					logger.error("Error in doenloading file: " + fileUpload.getFileName() + "/n" + ex);
				}
			} else {
				response.sendError(404, new FileNotFoundException().getMessage());
			}
		} else {
			response.sendError(404, new FileNotFoundException().getMessage());
		}
	}

	@Override
	public FileUpload getFileByFileId(int id) {
		return fileDAO.getFileByFileId(id);
	}
	
	@Override
	public void openPDF(HttpServletResponse response, String pdfPath) throws IOException {
		Path file = Paths.get(pdfPath);
		Path fileName = file.getFileName();
		if (Files.exists(file)) {
			try {
				String contentType = Files.probeContentType(file);
				response.setContentType(contentType);
				response.setHeader("Content-disposition", "filename=" + fileName);
				Files.copy(file, response.getOutputStream());
				response.getOutputStream().flush();
			} catch (IOException ex) {
				logger.error("Error in downloading file: " + pdfPath + "/n" + ex);
			}
		} else {
			response.sendError(404, new FileNotFoundException().getMessage());
		}
	
	}

}
