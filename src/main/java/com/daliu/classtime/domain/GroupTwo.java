package com.daliu.classtime.domain;

public class GroupTwo {
	
	private int id;
	
	public GroupTwo(){
		this.id=2;
	}
	

	@Override
	public String toString() {
		return "Group [id=" + id + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
