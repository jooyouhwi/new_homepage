package com.wow.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wow.api.entity.SmWownetPg;
import com.wow.api.model.PgModel;


public interface PgDao extends JpaRepository<SmWownetPg,Long>{
	@Query(value = "SELECT PG_CARD_KEY       as pgCardKey    "
			+ "          , PG_CARD_MID       as pgCardMid    "
			+ "          , PG_CARD_AES_KEY   as pgCardAesKey "
			+ "          , PG_VBANK_KEY      as pgVbankKey   "
			+ "          , PG_VBANK_MID      as pgVbankMid   "
			+ "          , PG_VBANK_AES_KEY  as pgVbankAesKey"
			+ "       FROM SM_WOWNET_PG                      "
			+ "      WHERE COM_ID = :comId                   "
			, nativeQuery = true)
	public PgModel getwowConfigPGInfoQuery(String comId);
}