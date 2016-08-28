package com.crossover.airline.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = -1202743309902073149L;

	@CreatedDate
	@Column(name = "created_date", nullable = false)
	private Date createdDate;
	
	@LastModifiedDate
	@Column(name = "last_modified_date", nullable = false)
	private Date lastModifiedDate;

	@CreatedBy
	@Column(name = "created_by", nullable = false)
	private String createdBy;
	
	@LastModifiedBy
	@Column(name = "last_modified_by", nullable = false)
	private String lastModifiedBy; 
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
}
