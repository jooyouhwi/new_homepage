package com.wow.api.dao;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import com.wow.api.model.CertifiedModel;


@Mapper
public interface CertifiedMapper {
	
	public CertifiedModel getwowConfigAccountInfo(String comId);

	public CertifiedModel getDecryptAccountInfo(String accNo, String birthday);
	
	public CertifiedModel getDecryptNameInfo(String sJumin1, String sJumin2);
	
	public void insertCertifyLog(HashMap<String, Object> paramObj);

}