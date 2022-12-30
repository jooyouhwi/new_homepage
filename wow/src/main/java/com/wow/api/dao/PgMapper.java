package com.wow.api.dao;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import com.wow.api.model.CompanyModel;
import com.wow.api.model.PgModel;


@Mapper
public interface PgMapper {
	public PgModel getSettleCardDuplChk( HashMap<String,Object> paramObj );
	
	public PgModel getSettleVbankDuplChk( HashMap<String,Object> paramObj );
	
	public PgModel getSettleCancelDuplChk( HashMap<String,Object> paramObj );
	
	public PgModel getWowConfigPGInfo( HashMap<String,Object> paramObj );
	
	public void insertSettleCardLog( HashMap<String,Object> paramObj );
	
	public void updateSettleCardResLog( HashMap<String,Object> paramObj );
	
	public void updateSettleCardNotiLog( HashMap<String,Object> paramObj );
	
	public void insertSettleVbankLog( HashMap<String,Object> paramObj );
	
	public void updateSettleVbankResLog( HashMap<String,Object> paramObj );
	
	public void insertSettleVbankNotiLog( HashMap<String,Object> paramObj );
	
	public void updateSettleVbankNotiLog( HashMap<String,Object> paramObj );
	
	public HashMap<String,Object> getSettleCancelInfo( HashMap<String,Object> paramObj );
	
	public void insertSettleCancelLog( HashMap<String,Object> paramObj );
	
	public void updateSettleCancelResLog( HashMap<String,Object> paramObj );
	
	public void insertSettleNotiLog( HashMap<String,Object> paramObj );
	
	public void updateSettleNotiLog( HashMap<String,Object> paramObj );
}