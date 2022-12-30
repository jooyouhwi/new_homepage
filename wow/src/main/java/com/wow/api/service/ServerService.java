package com.wow.api.service;

import java.util.List;
import java.util.HashMap;

import com.wow.api.model.CompanyModel;
import com.wow.api.model.PgModel;
import com.wow.api.model.ServerModel;

public interface ServerService {
	
	public ServerModel getuserInfo(String comId, String userId);
	
	public PgModel getwowConfigPGInfo(String comId);
	
	public CompanyModel listCompany();

}
