package com.wow.api.dao;

import org.apache.ibatis.annotations.Mapper;

import com.wow.api.model.CompanyModel;


@Mapper
public interface CompanyMapper {
	public CompanyModel listCompany(String comId);

	public void insertTable(String comId);
}