package com.renyu.sales.model;

public class Province {
	
	int id=0;
	String name="";
	int provinceType=0;
	
	public Province(int id, String name, int provinceType) {
		this.id=id;
		this.name=name;
		this.provinceType=provinceType;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getProvinceType() {
		return provinceType;
	}
	
}
