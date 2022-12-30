package com.wow.api.dao;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import com.wow.api.model.CompanyModel;


@Mapper
public interface MaccoMapper {
	//public CompanyModel listCompany(String comId);

	
	public void insertLogMacco( HashMap<String,Object>  req);
	
	public void insertOrdGuild( HashMap<String,Object>  paramObj);
	
	public void updateOrdGuildSuccess( HashMap<String,Object>  paramObj);
	
	public void updateOrdGuildFail( HashMap<String,Object>  paramObj);
	
	
	
	
}