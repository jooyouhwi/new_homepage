package com.wow.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wow.api.dao.CompanyMapper;
import com.wow.api.dao.PgDao;
import com.wow.api.dao.ServerDao;
import com.wow.api.model.CompanyModel;
import com.wow.api.model.PgModel;
import com.wow.api.model.ServerModel;




@Service
public class ServerServiceImpl implements ServerService {
	
	
	@Autowired(required = true)
	private ServerDao serverDao;
	
	@Autowired(required = true)
	private PgDao pgDao;
	
	//@Autowired(required = true)
	private CompanyMapper companyMapper;
	
	@Override
	public ServerModel getuserInfo(String comId, String userId) {
		// TODO Auto-generated method stubs

		System.out.println("comId   : " +  comId);
		System.out.println("userId  : " +  userId);
		
		// DB 콜
		
		//System.out.println( "members : "+members);
		//List<ServerModel> members = new ArrayList<>();
		//members = this.serverDao.getUserInfoQuery(comId, userId);
	    
		return this.serverDao.getUserInfoQuery(comId, userId); 
	}

	@Override
	public PgModel getwowConfigPGInfo(String comId) {
		// TODO Auto-generated method stub
		return this.pgDao.getwowConfigPGInfoQuery(comId); 
	} 
	
	@Override
	public CompanyModel listCompany() {
		// TODO Auto-generated method stub
		try {
			System.out.println("-------------------------------");
			//CompanyModel comList = companyMapper.listCompany();
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
		  //return companyMapper.listCompany();
		return null;
	}

}
