package com.reminder.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.reminder.utils.DateTimeUtil;

@Entity
@Table(name = "files")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "files")
public class FileUpload implements Serializable {

	private static final long serialVersionUID = -1232395859408322328L;
	
	@Id
     @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "file_id")
	private int fileId;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_path")
	private String filePath;

	@Column(name = "file_size")
	private int fileSize;

	@Column(name = "fileFormat")
	private String file_format;

	@Column(name = "created_by_id")
	private int createdById;

	@Column(name = "created_at")
	private DateTime createdAt;

	@Column(name = "last_modified_by_id")
	private int lastModifiedById;

	@Column(name = "last_modified_at")
	private DateTime lastModifiedAt;
	

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "reminder_id")
	private Reminder reminders;

	@PrePersist
	  protected void onCreate() {
		 createdAt = DateTimeUtil.now();
		 lastModifiedAt = DateTimeUtil.now();
	  }

	  @PreUpdate
	  protected void onUpdate() {
		  lastModifiedAt = DateTimeUtil.now();
	  }
	  
	public Reminder getReminders() {
		return reminders;
	}

	public void setReminders(Reminder reminders) {
		this.reminders = reminders;
	}

	public FileUpload() {
		super();
	}

	public FileUpload(int fileId, String fileName, String filePath, int fileSize, String file_format, int createdById,
			DateTime createdAt, int lastModifiedById, DateTime lastModifiedAt) {
		super();
		this.fileId = fileId;
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileSize = fileSize;
		this.file_format = file_format;
		this.createdById = createdById;
		this.createdAt = createdAt;
		this.lastModifiedById = lastModifiedById;
		this.lastModifiedAt = lastModifiedAt;
	}

	/** Setter $ Getter */

	public int getFileId() {
		return fileId;
	}


	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		String file = fileName.substring(0,fileName.lastIndexOf("_"));
		 String filewithout = file + "." + this.getFile_format();
		return filewithout;
	}
	
	public String getFileNameOrg() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getFile_format() {
		return file_format;
	}

	public void setFile_format(String file_format) {
		this.file_format = file_format;
	}

	public int getCreatedById() {
		return createdById;
	}

	public void setCreatedById(int createdById) {
		this.createdById = createdById;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	public int getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(int lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}

	public DateTime getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(DateTime lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

}
