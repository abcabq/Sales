package com.renyu.sales.model;

import java.io.Serializable;

public class City implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int id=0;
	String name="";
	int provinceId=0;
	
	public City(int id, String name, int provinceId) {
		this.id=id;
		this.name=name;
		this.provinceId=provinceId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getProvinceId() {
		return provinceId;
	}
	
}
