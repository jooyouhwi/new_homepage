package com.wow.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wow.api.entity.Member;
import com.wow.api.model.ServerModel;


@Repository
public interface ServerDao extends JpaRepository <Member, Long> {
	

	@Query(value = "SELECT Userid   as userId                                              "			
			+ "          , Username as username                                            "
			+ "          , Cnt_Cd   as cntCd                                               "
			+ "       FROM Member                                                          "
			+ "      WHERE Com_ID = :comId                                                 "
			+ "        AND Userid = :userid                                                "
			, nativeQuery = true)
	public ServerModel getUserInfoQuery(String comId, String userid);
	
	
	
	
	
}
