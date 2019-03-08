package com.daliu.classtime.test;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="test")
public class testEntity {
	
	@Id
	private String ids;
	
	private String name;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "testEntity [ids=" + ids + ", name=" + name + "]";
	}
	
	

}
