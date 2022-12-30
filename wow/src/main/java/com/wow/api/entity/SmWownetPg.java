package com.wow.api.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the SM_WOWNET_PG database table.
 * 
 */
@Entity
@Table(name="SM_WOWNET_PG")
@NamedQuery(name="SmWownetPg.findAll", query="SELECT s FROM SmWownetPg s")
public class SmWownetPg implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COM_ID")
	private String comId;

	@Column(name="CF_MAIL_YN")
	private String cfMailYn;

	@Column(name="CF_PHONE_YN")
	private String cfPhoneYn;

	@Column(name="CT_BANK_CD")
	private String ctBankCd;

	@Column(name="CT_BANK_PW")
	private String ctBankPw;

	@Column(name="CT_BANK_YN")
	private String ctBankYn;

	@Column(name="CT_IPIN_CD")
	private String ctIpinCd;

	@Column(name="CT_IPIN_PW")
	private String ctIpinPw;

	@Column(name="CT_IPIN_YN")
	private String ctIpinYn;

	@Column(name="CT_PHONE_CD")
	private String ctPhoneCd;

	@Column(name="CT_PHONE_PW")
	private String ctPhonePw;

	@Column(name="CT_PHONE_YN")
	private String ctPhoneYn;

	@Column(name="CT_SITE_CD")
	private String ctSiteCd;

	@Column(name="CT_SITE_F_CD")
	private String ctSiteFCd;

	@Column(name="CT_SITE_F_PW")
	private String ctSiteFPw;

	@Column(name="CT_SITE_F_YN")
	private String ctSiteFYn;

	@Column(name="CT_SITE_PW")
	private String ctSitePw;

	@Column(name="CT_SITE_YN")
	private String ctSiteYn;

	@Column(name="CT_VENDOR")
	private String ctVendor;

	@Column(name="PG_2_BANK_KEY")
	private String pg2BankKey;

	@Column(name="PG_2_BANK_MID")
	private String pg2BankMid;

	@Column(name="PG_2_BANK_AES_KEY")
	private String pg2BankAesKey;
	
	@Column(name="PG_2_CARD_KEY")
	private String pg2CardKey;

	@Column(name="PG_2_CARD_MID")
	private String pg2CardMid;

	@Column(name="PG_2_CARD_AES_KEY")
	private String pg2CardAesKey;
	
	@Column(name="PG_2_PREPAY_KEY")
	private String pg2PrepayKey;

	@Column(name="PG_2_PREPAY_MID")
	private String pg2PrepayMid;

	@Column(name="PG_2_PREPAY_AES_KEY")
	private String pg2PrepayAesKey;
	
	@Column(name="PG_2_VBANK_KEY")
	private String pg2VbankKey;

	@Column(name="PG_2_VBANK_MID")
	private String pg2VbankMid;

	@Column(name="PG_2_VBANK_AES_KEY")
	private String pg2VbankAesKey;
	
	@Column(name="PG_2_VENDOR")
	private String pg2Vendor;

	@Column(name="PG_BANK_KEY")
	private String pgBankKey;

	@Column(name="PG_BANK_MID")
	private String pgBankMid;

	@Column(name="PG_BANK_AES_KEY")
	private String pgBankAesKey;
	
	@Column(name="PG_CARD_KEY")
	private String pgCardKey;

	@Column(name="PG_CARD_MID")
	private String pgCardMid;

	@Column(name="PG_CARD_AES_KEY")
	private String pgCardAesKey;
	
	@Column(name="PG_PREPAY_KEY")
	private String pgPrepayKey;

	@Column(name="PG_PREPAY_MID")
	private String pgPrepayMid;

	@Column(name="PG_PREPAY_AES_KEY")
	private String pgPrepayAesKey;
	
	@Column(name="PG_VBANK_KEY")
	private String pgVbankKey;

	@Column(name="PG_VBANK_MID")
	private String pgVbankMid;

	@Column(name="PG_VBANK_AES_KEY")
	private String pgVbankAesKey;
	
	@Column(name="PG_VENDOR")
	private String pgVendor;

	@Column(name="REMARK")
	private String remark;

	@Column(name="SETT_M_ARS")
	private String settMArs;

	@Column(name="SETT_M_BANK")
	private String settMBank;

	@Column(name="SETT_M_CARD")
	private String settMCard;

	@Column(name="SETT_M_CASH")
	private String settMCash;

	@Column(name="SETT_M_COIN")
	private String settMCoin;

	@Column(name="SETT_M_ETC")
	private String settMEtc;

	@Column(name="SETT_M_POINT")
	private String settMPoint;

	@Column(name="SETT_M_PREPAY")
	private String settMPrepay;

	@Column(name="SETT_M_VBANK")
	private String settMVbank;

	@Column(name="SETT_W_ARS")
	private String settWArs;

	@Column(name="SETT_W_BANK")
	private String settWBank;

	@Column(name="SETT_W_CARD")
	private String settWCard;

	@Column(name="SETT_W_CASH")
	private String settWCash;

	@Column(name="SETT_W_COIN")
	private String settWCoin;

	@Column(name="SETT_W_ETC")
	private String settWEtc;

	@Column(name="SETT_W_POINT")
	private String settWPoint;

	@Column(name="SETT_W_PREPAY")
	private String settWPrepay;

	@Column(name="SETT_W_VBANK")
	private String settWVbank;

	@Temporal(TemporalType.DATE)
	@Column(name="WORK_DATE")
	private Date workDate;

	@Column(name="WORK_USER")
	private String workUser;

	//bi-directional one-to-one association to Company
	/*
	 * @OneToOne
	 * 
	 * @JoinColumn(name="COM_ID", insertable=false, updatable=false) private Company
	 * company;
	 */
	public SmWownetPg() {
	}

	public String getComId() {
		return this.comId;
	}

	public void setComId(String comId) {
		this.comId = comId;
	}

	public String getCfMailYn() {
		return this.cfMailYn;
	}

	public void setCfMailYn(String cfMailYn) {
		this.cfMailYn = cfMailYn;
	}

	public String getCfPhoneYn() {
		return this.cfPhoneYn;
	}

	public void setCfPhoneYn(String cfPhoneYn) {
		this.cfPhoneYn = cfPhoneYn;
	}

	public String getCtBankCd() {
		return this.ctBankCd;
	}

	public void setCtBankCd(String ctBankCd) {
		this.ctBankCd = ctBankCd;
	}

	public String getCtBankPw() {
		return this.ctBankPw;
	}

	public void setCtBankPw(String ctBankPw) {
		this.ctBankPw = ctBankPw;
	}

	public String getCtBankYn() {
		return this.ctBankYn;
	}

	public void setCtBankYn(String ctBankYn) {
		this.ctBankYn = ctBankYn;
	}

	public String getCtIpinCd() {
		return this.ctIpinCd;
	}

	public void setCtIpinCd(String ctIpinCd) {
		this.ctIpinCd = ctIpinCd;
	}

	public String getCtIpinPw() {
		return this.ctIpinPw;
	}

	public void setCtIpinPw(String ctIpinPw) {
		this.ctIpinPw = ctIpinPw;
	}

	public String getCtIpinYn() {
		return this.ctIpinYn;
	}

	public void setCtIpinYn(String ctIpinYn) {
		this.ctIpinYn = ctIpinYn;
	}

	public String getCtPhoneCd() {
		return this.ctPhoneCd;
	}

	public void setCtPhoneCd(String ctPhoneCd) {
		this.ctPhoneCd = ctPhoneCd;
	}

	public String getCtPhonePw() {
		return this.ctPhonePw;
	}

	public void setCtPhonePw(String ctPhonePw) {
		this.ctPhonePw = ctPhonePw;
	}

	public String getCtPhoneYn() {
		return this.ctPhoneYn;
	}

	public void setCtPhoneYn(String ctPhoneYn) {
		this.ctPhoneYn = ctPhoneYn;
	}

	public String getCtSiteCd() {
		return this.ctSiteCd;
	}

	public void setCtSiteCd(String ctSiteCd) {
		this.ctSiteCd = ctSiteCd;
	}

	public String getCtSiteFCd() {
		return this.ctSiteFCd;
	}

	public void setCtSiteFCd(String ctSiteFCd) {
		this.ctSiteFCd = ctSiteFCd;
	}

	public String getCtSiteFPw() {
		return this.ctSiteFPw;
	}

	public void setCtSiteFPw(String ctSiteFPw) {
		this.ctSiteFPw = ctSiteFPw;
	}

	public String getCtSiteFYn() {
		return this.ctSiteFYn;
	}

	public void setCtSiteFYn(String ctSiteFYn) {
		this.ctSiteFYn = ctSiteFYn;
	}

	public String getCtSitePw() {
		return this.ctSitePw;
	}

	public void setCtSitePw(String ctSitePw) {
		this.ctSitePw = ctSitePw;
	}

	public String getCtSiteYn() {
		return this.ctSiteYn;
	}

	public void setCtSiteYn(String ctSiteYn) {
		this.ctSiteYn = ctSiteYn;
	}

	public String getCtVendor() {
		return this.ctVendor;
	}

	public void setCtVendor(String ctVendor) {
		this.ctVendor = ctVendor;
	}

	public String getPg2BankKey() {
		return this.pg2BankKey;
	}

	public void setPg2BankKey(String pg2BankKey) {
		this.pg2BankKey = pg2BankKey;
	}

	public String getPg2BankMid() {
		return this.pg2BankMid;
	}

	public void setPg2BankMid(String pg2BankMid) {
		this.pg2BankMid = pg2BankMid;
	}

	public String getPg2CardKey() {
		return this.pg2CardKey;
	}

	public void setPg2CardKey(String pg2CardKey) {
		this.pg2CardKey = pg2CardKey;
	}

	public String getPg2CardMid() {
		return this.pg2CardMid;
	}

	public void setPg2CardMid(String pg2CardMid) {
		this.pg2CardMid = pg2CardMid;
	}

	public String getPg2PrepayKey() {
		return this.pg2PrepayKey;
	}

	public void setPg2PrepayKey(String pg2PrepayKey) {
		this.pg2PrepayKey = pg2PrepayKey;
	}

	public String getPg2PrepayMid() {
		return this.pg2PrepayMid;
	}

	public void setPg2PrepayMid(String pg2PrepayMid) {
		this.pg2PrepayMid = pg2PrepayMid;
	}

	public String getPg2VbankKey() {
		return this.pg2VbankKey;
	}

	public void setPg2VbankKey(String pg2VbankKey) {
		this.pg2VbankKey = pg2VbankKey;
	}

	public String getPg2VbankMid() {
		return this.pg2VbankMid;
	}

	public void setPg2VbankMid(String pg2VbankMid) {
		this.pg2VbankMid = pg2VbankMid;
	}

	public String getPg2Vendor() {
		return this.pg2Vendor;
	}

	public void setPg2Vendor(String pg2Vendor) {
		this.pg2Vendor = pg2Vendor;
	}

	public String getPgBankKey() {
		return this.pgBankKey;
	}

	public void setPgBankKey(String pgBankKey) {
		this.pgBankKey = pgBankKey;
	}

	public String getPgBankMid() {
		return this.pgBankMid;
	}

	public void setPgBankMid(String pgBankMid) {
		this.pgBankMid = pgBankMid;
	}

	public String getPgCardKey() {
		return this.pgCardKey;
	}

	public void setPgCardKey(String pgCardKey) {
		this.pgCardKey = pgCardKey;
	}

	public String getPgCardMid() {
		return this.pgCardMid;
	}

	public void setPgCardMid(String pgCardMid) {
		this.pgCardMid = pgCardMid;
	}

	public String getPgPrepayKey() {
		return this.pgPrepayKey;
	}

	public void setPgPrepayKey(String pgPrepayKey) {
		this.pgPrepayKey = pgPrepayKey;
	}

	public String getPgPrepayMid() {
		return this.pgPrepayMid;
	}

	public void setPgPrepayMid(String pgPrepayMid) {
		this.pgPrepayMid = pgPrepayMid;
	}

	public String getPgVbankKey() {
		return this.pgVbankKey;
	}

	public void setPgVbankKey(String pgVbankKey) {
		this.pgVbankKey = pgVbankKey;
	}

	public String getPgVbankMid() {
		return this.pgVbankMid;
	}

	public void setPgVbankMid(String pgVbankMid) {
		this.pgVbankMid = pgVbankMid;
	}

	public String getPgVendor() {
		return this.pgVendor;
	}

	public void setPgVendor(String pgVendor) {
		this.pgVendor = pgVendor;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSettMArs() {
		return this.settMArs;
	}

	public void setSettMArs(String settMArs) {
		this.settMArs = settMArs;
	}

	public String getSettMBank() {
		return this.settMBank;
	}

	public void setSettMBank(String settMBank) {
		this.settMBank = settMBank;
	}

	public String getSettMCard() {
		return this.settMCard;
	}

	public void setSettMCard(String settMCard) {
		this.settMCard = settMCard;
	}

	public String getSettMCash() {
		return this.settMCash;
	}

	public void setSettMCash(String settMCash) {
		this.settMCash = settMCash;
	}

	public String getSettMCoin() {
		return this.settMCoin;
	}

	public void setSettMCoin(String settMCoin) {
		this.settMCoin = settMCoin;
	}

	public String getSettMEtc() {
		return this.settMEtc;
	}

	public void setSettMEtc(String settMEtc) {
		this.settMEtc = settMEtc;
	}

	public String getSettMPoint() {
		return this.settMPoint;
	}

	public void setSettMPoint(String settMPoint) {
		this.settMPoint = settMPoint;
	}

	public String getSettMPrepay() {
		return this.settMPrepay;
	}

	public void setSettMPrepay(String settMPrepay) {
		this.settMPrepay = settMPrepay;
	}

	public String getSettMVbank() {
		return this.settMVbank;
	}

	public void setSettMVbank(String settMVbank) {
		this.settMVbank = settMVbank;
	}

	public String getSettWArs() {
		return this.settWArs;
	}

	public void setSettWArs(String settWArs) {
		this.settWArs = settWArs;
	}

	public String getSettWBank() {
		return this.settWBank;
	}

	public void setSettWBank(String settWBank) {
		this.settWBank = settWBank;
	}

	public String getSettWCard() {
		return this.settWCard;
	}

	public void setSettWCard(String settWCard) {
		this.settWCard = settWCard;
	}

	public String getSettWCash() {
		return this.settWCash;
	}

	public void setSettWCash(String settWCash) {
		this.settWCash = settWCash;
	}

	public String getSettWCoin() {
		return this.settWCoin;
	}

	public void setSettWCoin(String settWCoin) {
		this.settWCoin = settWCoin;
	}

	public String getSettWEtc() {
		return this.settWEtc;
	}

	public void setSettWEtc(String settWEtc) {
		this.settWEtc = settWEtc;
	}

	public String getSettWPoint() {
		return this.settWPoint;
	}

	public void setSettWPoint(String settWPoint) {
		this.settWPoint = settWPoint;
	}

	public String getSettWPrepay() {
		return this.settWPrepay;
	}

	public void setSettWPrepay(String settWPrepay) {
		this.settWPrepay = settWPrepay;
	}

	public String getSettWVbank() {
		return this.settWVbank;
	}

	public void setSettWVbank(String settWVbank) {
		this.settWVbank = settWVbank;
	}

	public Date getWorkDate() {
		return this.workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}

	public String getWorkUser() {
		return this.workUser;
	}

	public void setWorkUser(String workUser) {
		this.workUser = workUser;
	}

	
}