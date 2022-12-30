package com.wow.api.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the MEMBER database table.
 * 
 */
@Entity
@NamedQuery(name="Member.findAll", query="SELECT m FROM Member m")
public class Member implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String userid;

	@Column(name="AB_POS")
	private BigDecimal abPos;

	@Temporal(TemporalType.DATE)
	@Column(name="ACC_DATE")
	private Date accDate;

	@Column(name="ACC_NO")
	private String accNo;

	private String addr1;

	private String addr2;

	@Column(name="BB_BIRTHDAY")
	private String bbBirthday;

	@Column(name="BB_E_MAIL")
	private String bbEMail;

	@Column(name="BB_FAMILY_NAME")
	private String bbFamilyName;

	@Column(name="BB_GIVEN_NAME")
	private String bbGivenName;

	@Column(name="BB_MIDDLE_NAME")
	private String bbMiddleName;

	@Column(name="BB_MOBILE")
	private String bbMobile;

	@Column(name="BB_NAME")
	private String bbName;

	@Column(name="BB_NAME_KANA")
	private String bbNameKana;

	@Column(name="BB_REG_DATE")
	private String bbRegDate;

	private String birthday;

	@Column(name="C_ID")
	private String cId;

	@Column(name="CHK_AB_POS")
	private BigDecimal chkAbPos;

	@Temporal(TemporalType.DATE)
	@Column(name="CHK_DATE")
	private Date chkDate;

	@Column(name="CHK_RESULT")
	private String chkResult;

	private String city;

	@Column(name="COMPANY_NAME")
	private String companyName;

	private String county;

	@Column(name="DATE_EDU")
	private String dateEdu;

	@Column(name="DATE_GET_BANK")
	private String dateGetBank;

	@Column(name="DATE_GET_JUMIN")
	private String dateGetJumin;

	@Column(name="DATE_GET_PARENT")
	private String dateGetParent;

	@Column(name="DATE_GET_REGIST")
	private String dateGetRegist;

	@Column(name="DATE_GET_TAX")
	private String dateGetTax;

	@Column(name="DATE_ORD")
	private String dateOrd;

	@Column(name="DATE_SEND_BOOK")
	private String dateSendBook;

	@Column(name="DATE_SEND_CERT")
	private String dateSendCert;

	private String depositor;

	@Column(name="E_MAIL")
	private String eMail;

	@Column(name="FAMILY_NAME")
	private String familyName;

	private String gender;

	@Column(name="GIVEN_NAME")
	private String givenName;

	@Temporal(TemporalType.DATE)
	@Column(name="INS_DATE")
	private Date insDate;

	@Column(name="INS_USER")
	private String insUser;

	@Temporal(TemporalType.DATE)
	@Column(name="JUMIN_DATE")
	private Date juminDate;

	@Column(name="JUMIN_NO")
	private String juminNo;

	@Temporal(TemporalType.DATE)
	@Column(name="LOGIN_DATE")
	private Date loginDate;

	@Column(name="LOGIN_ID")
	private String loginId;

	@Column(name="MIDDLE_NAME")
	private String middleName;

	private String mobile;

	private String nickname;

	private String notice;

	@Temporal(TemporalType.DATE)
	@Column(name="OK_DATE_E_MAIL")
	private Date okDateEMail;

	@Temporal(TemporalType.DATE)
	@Column(name="OK_DATE_SMS")
	private Date okDateSms;

	private String passwd;

	@Temporal(TemporalType.DATE)
	@Column(name="PASSWD_DATE")
	private Date passwdDate;

	private String post;

	@Column(name="RANK_MAX_DATE")
	private String rankMaxDate;

	@Column(name="REG_DATE")
	private String regDate;

	@Column(name="REG_KIND")
	private String regKind;

	private String remark;

	@Column(name="S_ID")
	private String sId;

	@Column(name="STATE")
	private String state;

	@Column(name="TAX_KIND")
	private String taxKind;

	@Column(name="TAX_NO")
	private String taxNo;

	private String tel;

	@Column(name="TERM_DATE")
	private String termDate;

	@Column(name="TOKEN_KEY")
	private String tokenKey;

	@Temporal(TemporalType.DATE)
	@Column(name="UPD_DATE")
	private Date updDate;

	@Column(name="UPD_USER")
	private String updUser;

	@Column(name="USER_CNT")
	private BigDecimal userCnt;

	@Column(name="USER_KIND_DATE")
	private String userKindDate;

	private String userid2;
	
	@Basic(fetch = FetchType.LAZY)
	private String username;
	
	@Column(name="USERNAME_KANA")
	private String usernameKana;
	

	//bi-directional many-to-one association to Member
	@ManyToOne
	@JoinColumn(name="P_ID", insertable=false, updatable=false)//, referencedColumnName = "userid"
	private Member member1;

	//bi-directional many-to-one association to Member
	@OneToMany(mappedBy="member1")
	private List<Member> members1;

	//bi-directional many-to-one association to Member
	@ManyToOne
	@JoinColumn(name="R_ID", insertable=false, updatable=false)
	private Member member2;

	//bi-directional many-to-one association to Member
	@OneToMany(mappedBy="member2")
	private List<Member> members2;



}