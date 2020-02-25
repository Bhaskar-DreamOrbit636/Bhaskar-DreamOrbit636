package com.reminder.controller;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.reminder.exception.FileUploadException;
import com.reminder.model.FileUpload;
import com.reminder.model.User;
import com.reminder.request.model.FileRequest;
import com.reminder.security.JwtTokenUtil;
import com.reminder.service.FileService;
import com.reminder.service.UserService;
import com.reminder.utils.NRICSecurity;

@RestController
@PropertySource("classpath:/application.properties")
public class FileUploadController {

	private Logger logger = Logger.getLogger(FileUploadController.class);

	@Autowired
	private FileService fileService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userService;

	@PersistenceContext
	private EntityManager entity;

	/*@Value("${r365.fileupload.location}")
	private String path;
	
	@Value("${r365.FAQPDF.location}")
	private String pdfPath;*/

	 private String path = System.getProperty("r365.fileupload.location");
	 private String pdfPath = System.getProperty("r365.FAQPDF.location");

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/savefile", method = RequestMethod.POST)
	public void upload(@RequestParam CommonsMultipartFile file, HttpSession session) {
		String path = session.getServletContext().getRealPath("/");
		String filename = file.getOriginalFilename();
		logger.info("upload(file=" + file + ", session=" + session + ") - File upload path : " + path + " & filename : "
				+ filename);
		// System.out.println(path + " " + filename);
		try {
			byte barr[] = file.getBytes();

			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(path + "/" + filename));
			bout.write(barr);
			bout.flush();
			bout.close();

		} catch (Exception e) {
			logger.info("upload(file=" + file + ", session=" + session + ") - Exception occured when uploading file : "
					+ filename + " with Exception : " + e);
			System.out.println(e);
		}

	}

	@Transactional
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/multipleSave/{module}/{reminderId}", method = RequestMethod.POST)
	public @ResponseBody String uploadMultiple(@RequestParam("file") MultipartFile[] files, @PathVariable String module,
			@PathVariable int reminderId, HttpSession session, HttpServletResponse response,
			HttpServletRequest request) {
		logger.info("uploadMultiple(files=" + files + ", module=" + module + ", reminderId=" + reminderId + ", session="
				+ session + ", response=" + response + ") - File upload starts------------------" + path);
		String header = request.getHeader("Authorization");
		User createdUser = null;
		if (header.startsWith("Bearer ")) {
			String username = jwtTokenUtil.getUsernameFromToken(header.substring(7));
			createdUser = userService.getUserByName(username);
		}
		long limit = 2 * 1024 * 1024;
		String fileName = null;
		String msg = "";
		String modulePath = "";
		modulePath = path + "/" + module;
		if (files.length > 5) {
			throw new FileUploadException("Max number of Files to upload is 5");
		}
		if (files != null && files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				try {
					if (files[i].getSize() > limit) {
						throw new FileUploadException("File upload Limit is 2 mb, please check the size");
					}
					FileUpload file = new FileUpload();
					fileName = files[i].getOriginalFilename();
					String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
					String dateTime = getCurrentDateTime();
					String localDate = dateTime.toString().replaceAll("[^0-9]", "");
					String renameFile = fileName.substring(0, fileName.lastIndexOf('.')) + "_" + localDate.toString()
							+ "." + ext;
					logger.info("uploadMultiple(files=" + files + ", module=" + module + ", reminderId=" + reminderId
							+ ", session=" + session + ", response=" + response
							+ ") - ------file upload file reading--------");
					byte[] bytes = files[i].getBytes();
					BufferedOutputStream buffStream = new BufferedOutputStream(
							new FileOutputStream(modulePath + "/" + renameFile));
					logger.info("uploadMultiple(files=" + files + ", module=" + module + ", reminderId=" + reminderId
							+ ", session=" + session + ", response=" + response
							+ ") - ------before write file--------");
					buffStream.write(bytes);
					buffStream.close();
					logger.info("uploadMultiple(files=" + files + ", module=" + module + ", reminderId=" + reminderId
							+ ", session=" + session + ", response=" + response + ") - ------after write file--------");
					/*
					 * File destination = new File (path + "/" + renameFile);
					 * files[i].transferTo(destination);
					 */
					int size = (int) files[i].getSize();
					logger.info("uploadMultiple(files=" + files + ", module=" + module + ", reminderId=" + reminderId
							+ ", session=" + session + ", response=" + response + ") - fileNmae = " + fileName);
					logger.info("uploadMultiple(files=" + files + ", module=" + module + ", reminderId=" + reminderId
							+ ", session=" + session + ", response=" + response + ") - RenamefileNmae = " + renameFile);
					file.setCreatedById(createdUser.getUserId());
					file.setLastModifiedById(createdUser.getUserId());
					file.setFileName(renameFile);
					file.setFilePath(modulePath);
					file.setFileSize(size);
					file.setFile_format(ext);
					try{
					NRICSecurity sec = new NRICSecurity();
					sec.encrypt(modulePath + "/" + renameFile, modulePath + "/" + renameFile + ".enc");
					}catch(Exception e){
						logger.error("Fail to encrypt file "+e);
					}
					fileService.uploadMultiple(file, reminderId);
					msg += "You have successfully uploaded " + fileName + "<br/>";
				} catch (Exception e) {
					logger.error("Exception while uploadin the file" + e);
					try {
						logger.info("uploadMultiple(files=" + files + ", module=" + module + ", reminderId="
								+ reminderId + ", session=" + session + ", response=" + response
								+ ") - -deleteing reminder due to file upload fails-----------");
						// delete asset
						String assetdeleteQuery = "DELETE FROM asset where reminder_id=" + reminderId;
						entity.createNativeQuery(assetdeleteQuery).executeUpdate();
						// delete staff_record
						String staffdeleteQuery = "DELETE FROM staff_record where reminder_id=" + reminderId;
						entity.createNativeQuery(staffdeleteQuery).executeUpdate();
						// delete Contract
						String contractdeleteQuery = "DELETE FROM contract where reminder_id=" + reminderId;
						entity.createNativeQuery(contractdeleteQuery).executeUpdate();
						// delete Reminder
						String reminderdeleteQuery = "DELETE FROM reminder where reminder_id=" + reminderId;
						entity.createNativeQuery(reminderdeleteQuery).executeUpdate();
						logger.info("uploadMultiple(files=" + files + ", module=" + module + ", reminderId="
								+ reminderId + ", session=" + session + ", response=" + response
								+ ") - ------In Exception reminder deleted--------" + reminderId);
					} catch (Exception ex) {
						logger.info("uploadMultiple(files=" + files + ", module=" + module + ", reminderId="
								+ reminderId + ", session=" + session + ", response=" + response
								+ ") - ------Error in deleting inside file upload--------");
						logger.error("Exception while deleting reminder inside file" + ex);
						session.invalidate();
					}
					logger.info("uploadMultiple(files=" + files + ", module=" + module + ", reminderId=" + reminderId
							+ ", session=" + session + ", response=" + response
							+ ") - reminder is deleted due to file upload error: " + reminderId);
					try {
						response.reset();
						response.sendError(500, "upload failed");
					} catch (IOException e1) {
						logger.error("Error in sending status code: " + e);
						e1.printStackTrace();
						session.invalidate();
					}

				}
			}
			return msg;
		} else {
			return "Unable to upload. File is empty.";
		}

	}

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/deleteFile/{FileId}", method = RequestMethod.DELETE, produces = "application/json")
	public void deleteFile(@PathVariable("FileId") int id) {
		logger.info("deleteFile(id=" + id + ") - start - deleting the file");
		fileService.deleteFile(id);
	}

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/getFile/{reminderId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<FileUpload>> getFileById(@PathVariable("reminderId") int id) {
		logger.info("getFileById(id=" + id + ") - start - getting the file");
		List<FileUpload> f = fileService.getFileById(id);
		return new ResponseEntity<List<FileUpload>>(f, HttpStatus.OK);
	}

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/copyFileToReminder/{reminderId}", method = RequestMethod.PUT)
	public ResponseEntity<String> copyFileToReminder(@PathVariable("reminderId") int reminderId,
			@RequestBody FileRequest f) {
		logger.info("copyFileToReminder(reminderId=" + reminderId + ", f=" + f
				+ ") - start - CopyFileToReminder to new contract");
		for (Integer fileId : f.getIds()) {
			FileUpload fileUpload = fileService.getFileByFileId(fileId);
			fileUpload.setFileId(0);
			fileService.uploadMultiple(fileUpload, reminderId);
		}
		return new ResponseEntity<String>("Saved SuccessFully", HttpStatus.OK);
	}

	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/downloadFile/{fileId}", method = RequestMethod.GET)
	public void downloadFile(@PathVariable("fileId") int fileId, HttpServletResponse response) {
		logger.info("downloadFile(fileId=" + fileId + ", response=" + response + ") - start - Downloading the file");
		try {
			fileService.downloadFile(response, fileId);
		} catch (Exception e) {
			logger.error("Error while downloading File with Id - " + fileId + e);
			e.printStackTrace();
		}
	}
	
	@CrossOrigin(maxAge = 4800, allowCredentials = "false")
	@RequestMapping(value = "/openPdf", method = RequestMethod.GET)
	public void openPDF(HttpServletResponse response){
		logger.info("openPDF() start - Downloading the PDF file");
		try {
			fileService.openPDF(response, pdfPath);
		} catch (Exception e) {
			logger.error("Error while downloading PDFFile"+ e);
			e.printStackTrace();
		}
	}

	public String getCurrentDateTime() {

		Date date = new Date();
		// String localDate = new
		// Date().toString().replaceAll("\\D+","").substring(0, 8);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
		String timeString = df.format(date);

		return timeString;
	}

}
