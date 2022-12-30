package com.wow.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.wow.api.common.StringUtil;
import com.wow.api.dao.CertifiedMapper;
import com.wow.api.model.CertifiedModel;

import common.NameCheck;


@Controller
public class certifiedController {

	@Autowired
	private CertifiedMapper certifiedMapper;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public String resultOrderNo = "";
	public String resultCode    = "";
	public String resultMsg     = "";
	
	/*
	 * 계좌인증 
	 */
	// @RequestMapping(value = "/nice")
	public HashMap<String,Object> niceAccData(HttpServletRequest request, HttpServletResponse response) throws Exception {		 
		request.setCharacterEncoding("utf-8");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		HashMap<String,Object> retMap = new HashMap<String, Object>();
    	 
    	String comId = request.getParameter("comId");
    	String accNo = request.getParameter("accNo");
    	       accNo = accNo.replaceAll(" ", "+");
    	String birth = request.getParameter("birthday");
    		   birth = birth.replaceAll(" ", "+");
    	String username = request.getParameter("username");
    		   username = java.net.URLDecoder.decode(username,"UTF-8");
    	String userid = request.getParameter("userid");
    	CertifiedModel accountInfo = certifiedMapper.getwowConfigAccountInfo(comId);
    	
    	CertifiedModel decryptAccountInfo = certifiedMapper.getDecryptAccountInfo(accNo, birth);
    	 
    	//##################################################
 		//###### ▣ 회원사 ID 설정   - 계약시에 발급된 회원사 ID를 설정하십시오. ▣
 		//###### ▣ 회원사 PW 설정   - 계약시에 발급된 회원사 PASSWORD를 설정하십시오. ▣
 		//###### ▣ 조회사유  설정   - 10:회원가입 20:기존회원가입 30:성인인증 40:비회원확인 90:기타사유 ▣
 		//###### ▣ 개인/사업자 설정 - 1:개인 2:사업자 ▣
 		//##################################################
 		 
 	  	String niceUid = (String) accountInfo.ctBankCd;					// 한국신용정보에서 고객사에 부여한 구분 id
 		String svcPwd  = (String) accountInfo.ctBankPw;			    	// 한국신용정보에서 고객사에 부여한 서비스 이용 패스워드
 		String inq_rsn = StringUtil.isNull( request.getParameter("sReason") );	// 조회사유 - 10:회원가입 20:기존회원가입 30:성인인증 40:비회원확인 90:기타사유
 		String strGbn  = StringUtil.isNull( request.getParameter("mGbn") );		// 1 : 개인, 2: 사업자
 		//#################################################
 		
 		logger.info("niceUid : " + niceUid);
 		logger.info("svcPwd : " + svcPwd);
 		
 		String service      	= "1"; 	//서비스구분 1=계좌소유주확인 2=계좌성명확인 3=계좌유효성확인
 		String strResId     	= "" + StringUtil.isNull( decryptAccountInfo.birthday ); 	//주민번호(사업자 번호,법인번호)
 		String strNm        	= "" + StringUtil.isNull( username );	//계좌소유주명
 		String strBankCode  	= "" + StringUtil.isNull( request.getParameter("bankCd") );	    //은행코드(전문참조)
 		String strAccountNo 	= "" + StringUtil.isNull( decryptAccountInfo.accNo );		//계좌번호
 		String svcGbn       	= "" + StringUtil.isNull( request.getParameter("svcGbn") );	//업무구분(전문참조) 고정값 "5"
 		String svc_cls      	= "" + StringUtil.isNull( request.getParameter("svcCls") ); 	//내-외국인구분
 		String strOrderNo   	= sdf.format(new Date()) + (Math.round(Math.random() * 10000000000L) + "");           //주문번호 : 매 요청마다 중복되지 않도록 유의
 		
 	  	String result = start(niceUid, svcPwd, service, strGbn, strResId, strNm, strBankCode, strAccountNo, svcGbn, strOrderNo, svc_cls, inq_rsn);
 		String[] results = result.split("\\|");
 		String status = "";
 		
 		logger.info("results : " + results);
 		
 		resultOrderNo = results[0];
 		resultCode    = results[1];
		
 		if(resultCode.equals("0000")) {
 			status		= "Y";
 			resultMsg	= "계좌인증이 완료되었습니다." +"(" + results[2] + ")";	
 		}else{
 			status		= "N";
 			resultMsg	= "계좌인증이 실패되었습니다." +"(" + results[2] + ")";
 		}

 		// 계좌인증 로그 쌓기
 		HashMap<String,Object> paramObj = new HashMap<String,Object>();
 		paramObj.put("comId",		comId);
 		paramObj.put("userid",		userid); 	
 		paramObj.put("kind",		"BANK");
 		paramObj.put("certifyName",	"계좌인증");
 		paramObj.put("status",		status);
 		paramObj.put("rstOrdNo",	resultOrderNo);
 		paramObj.put("rstCd",		resultCode);
 		paramObj.put("rstMsg",		resultMsg);
 		paramObj.put("username",	strNm);
 		paramObj.put("birthday",	decryptAccountInfo.birthday);
 		paramObj.put("cardNo",		accNo);	
 		paramObj.put("strBankCode",	strBankCode);
 		paramObj.put("workKind",	"E");
        
 		certifiedMapper.insertCertifyLog(paramObj);
 		
 		// P000: 정상응답일때 송신되는 코드
 		// E999: 시스템이상 		
 		retMap.put("result", resultCode);
 		retMap.put("message", resultMsg);
    	 
		return retMap;
	}
	
	/*
	 * 실명인증
	 */
	public HashMap<String,Object> niceNameData(HttpServletRequest request, HttpServletResponse response) throws Exception {		 
		request.setCharacterEncoding("utf-8");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		HashMap<String,Object> retMap = new HashMap<String, Object>();
    	 
    	String comId = request.getParameter("comId");
    	
    	//실명인증 내국인
		//실명인증 외국인
		String sJumin1  = request.getParameter("sJumin1");
		String sJumin2  = request.getParameter("sJumin2");
		String sJumin   = ""; //request.getParameter("sJumin");
		String username = request.getParameter("username");
    		   username = java.net.URLDecoder.decode(username,"UTF-8");
    	String userid = request.getParameter("userid");
    	CertifiedModel accountInfo = certifiedMapper.getwowConfigAccountInfo(comId);
    	logger.info("000000000");
    	
    	logger.info("sJumin1 : " + sJumin1);
    	logger.info("sJumin2 : " + sJumin2);
    	logger.info("sJumin  : " + sJumin);
    	
    	CertifiedModel decryptNameInfo = certifiedMapper.getDecryptNameInfo(sJumin1, sJumin2);
    	
    	String SITECODE    = (String)accountInfo.ctSiteCd;    
    	String SITEPW      = (String)accountInfo.ctSitePw;
    	String SITE_F_CODE = (String)accountInfo.ctSiteFCd;  
		String SITE_F_PW   = (String)accountInfo.ctSiteFPw;
		
		logger.debug("sJumin 11111:" + sJumin1);
		sJumin = (String)decryptNameInfo.sJumin1 + (String)decryptNameInfo.sJumin2;
		logger.debug("sJumin : "+sJumin);
		String status="N";
		
		String Rtn = "";
		String RMsg = "";
		
		//외국인, 내국인 구분
		String alien = (String)decryptNameInfo.sJumin2.substring(0, 1);

		NameCheck NC = new NameCheck();
		if((!sJumin.equals("")) && (!username.equals("")))
		{
			NC.setChkName(username);	 

			if(alien.equals("1") || alien.equals("2") || alien.equals("3") || alien.equals("4")|| alien.equals("9")|| alien.equals("0")){
                //내국인 경우
                Rtn = NC.setJumin(sJumin+SITEPW);

			} else if (alien.equals("5") || alien.equals("6") || alien.equals("7") || alien.equals("8")){
                //외국인 경우
				Rtn = NC.setJumin(sJumin+SITE_F_PW);
			}
			
			//정상처리인 경우
			if(Rtn.equals("0")){	
				if(alien.equals("1") || alien.equals("2") || alien.equals("3") || alien.equals("4")|| alien.equals("9")|| alien.equals("0")){
	                //내국인 경우
	                NC.setSiteCode(SITECODE);

	            } else if (alien.equals("5") || alien.equals("6") || alien.equals("7") || alien.equals("8")){
	                //외국인 경우
	                NC.setSiteCode(SITE_F_CODE);
	            }
				
				NC.setTimeOut(30000);
				Rtn = NC.getRtn().trim();
				
				if(Rtn.equals("1")) {
					RMsg = "실명인증 성공";
					status = "Y";
				}else if(Rtn.equals("2")) {
					RMsg = "실명인증 실패\n주민번호와 이름이 일치하지 않습니다.";
					Rtn = NC.getRtn().trim();
				}else if(Rtn.equals("3")) {
					RMsg = "실명인증 실패\n나이스평가정보에 해당정보가 없습니다.";
					Rtn = NC.getRtn().trim();
				}else if(Rtn.equals("5") || Rtn.equals("50")) {
					RMsg = "실명인증 실패\n정상적인 주민번호가 아닙니다.";
					Rtn = NC.getRtn().trim();
				}else {
					RMsg = "실명인증 실패\n오류코드 : " + NC.getRtn().trim();
					Rtn = NC.getRtn().trim();
				}
			} else {
				RMsg = "실명인증 실패\n오류코드 : " + NC.getRtn().trim();
				Rtn = NC.getRtn().trim();
			}
			
		}
		
 		// 계좌인증 로그 쌓기
 		HashMap<String,Object> paramObj = new HashMap<String,Object>();
 		paramObj.put("comId",		comId);
 		paramObj.put("userid",		userid); 	
 		paramObj.put("kind",		"NAME");
 		paramObj.put("certifyName",	"실명인증");
 		paramObj.put("status",		status);
 		paramObj.put("rstOrdNo",	"");
 		paramObj.put("rstCd",		Rtn);
 		paramObj.put("rstMsg",		RMsg);
 		paramObj.put("username",	username);
 		paramObj.put("birthday",	sJumin);
 		paramObj.put("cardNo",		"");	
 		paramObj.put("strBankCode",	"");
 		paramObj.put("workKind",	"E");
		
 		certifiedMapper.insertCertifyLog(paramObj);
		 
 		// P000: 정상응답일때 송신되는 코드
 		// E999: 시스템이상 		
 		retMap.put("result", Rtn);
 		retMap.put("message", RMsg);
    	 
		return retMap;
	}
	
	public String start(String niceUid, String svcPwd, String service, String strGbn, String strResId, String strNm, String strBankCode, String strAccountNo, String svcGbn, String strOrderNo,
			String svc_cls, String inq_rsn) {

		String result = "";

		BufferedReader in = null;
		PrintWriter out = null;

		try {
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket soc = (SSLSocket) factory.createSocket("secure.nuguya.com", 443);

			// 타임아웃 +++++++++++++++++++++++++++++++++++++++++++++++++++++
			soc.setSoTimeout(10 * 1000); // 타임아웃 10초
			soc.setSoLinger(true, 10);
			soc.setKeepAlive(true);
			// 타임아웃 +++++++++++++++++++++++++++++++++++++++++++++++++++++			
			
			out = new PrintWriter(soc.getOutputStream());		
			in = new BufferedReader(new InputStreamReader(soc.getInputStream(),"utf-8"), 8 * 1024);		
			result = rlnmCheck(out, in, niceUid, svcPwd, service, strGbn, strResId, strNm, strBankCode, strAccountNo, svcGbn, strOrderNo, svc_cls, inq_rsn);
			
		} catch (SocketException e) {
			logger.info(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}

		return result;

	}
	
	@SuppressWarnings("unused")
	public String rlnmCheck(PrintWriter out, BufferedReader in, String niceUid, String svcPwd, String service, String strGbn, String strResId, String strNm, String strBankCode, String strAccountNo,
			String svcGbn, String strOrderNo, String svc_cls, String inq_rsn) throws IOException {
		StringBuffer sbResult = new StringBuffer();

		String contents = "niceUid=" + niceUid + "&svcPwd=" + svcPwd + "&service=" + service + "&strGbn=" + strGbn + "&strResId=" + strResId + 
				                 "&strNm=" + java.net.URLEncoder.encode(strNm, "utf-8") +
				                 "&strBankCode=" + strBankCode + "&strAccountNo=" + strAccountNo + "&svcGbn=" + svcGbn + "&strOrderNo=" + strOrderNo + "&svc_cls=" + svc_cls + "&inq_rsn=" + inq_rsn + "&seq=0000001";

		logger.info("##############>>>>>>>> os : " + contents);
		
		// out.println("POST https://secure.nuguya.com/nuguya2/service/realname/sprealnameactconfirm.do HTTP/1.1");
		// //UTF-8 URL
		out.println("POST https://secure.nuguya.com/nuguya2/service/realname/sprealnameactconfirm.do HTTP/1.1");
		out.println("Host: secure.nuguya.com");
		out.println("Connection: Keep-Alive");
		//out.println("Content-Type: application/x-www-form-urlencoded; charset=\"utf-8\"");
		out.println("Content-Type: application/x-www-form-urlencoded;");
		out.println("Content-Length: " + contents.length());
		out.println();
		out.println(contents);
		out.flush();

		String line = null;
		int i = 0;
		boolean notYet = true;
		while ((line = in.readLine()) != null) {
			i++;
			if (notYet && line.indexOf("HTTP/1.") == -1) {
				continue;
			}
			if (notYet && line.indexOf("HTTP/1.") > -1) {
				notYet = false;
			}

			if (line.indexOf("HTTP/1.") > -1) {
				notYet = false;
			}
			if (line.startsWith("0")) {
				break;
			}
			if (line == null) {
				break;
			}

			if (i == 9)
				sbResult.append(line);
		}

		logger.info(sbResult.toString());
		return sbResult.toString();
	}
}
