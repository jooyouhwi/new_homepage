package com.wow.api.controller;

import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wow.api.common.EncryptUtil;
import com.wow.api.common.HttpClientUtil;
import com.wow.api.common.StringUtil;
import com.wow.api.dao.PgMapper;
import com.wow.api.model.PgModel;

import net.sf.json.JSONObject;

@RestController
public class settlebankPayment {

	@Autowired
	private PgMapper pgMapper;
	
	//구인증/비인증 API URL
	final String check = "real";
	final String serverURLTest	= "https://tbgw.settlebank.co.kr";
	final String serverURL		= "https://gw.settlebank.co.kr";
	
	// 헥토파이낸셜 PG 통신접속타임아웃
	final int connTimeout = 5000;
	
	// 헥토파이낸셜 PG통신 수신타임아웃
	final int readTimeout = 20000;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 노티를 성공적으로 수신한 경우 처리할 로직을 작성하여 주세요.
	boolean notiSuccess(List<String> noti){
		/* TODO : 관련 로직 추가 */
	    return true;
	}

	/** 입금대기시 처리할 로직을 작성하여 주세요. */
	boolean notiWaitingPay(List<String> noti){
		/* TODO : 관련 로직 추가 */
		return true;
	}  

	/** 노티 수신중 해시 체크 에러가 생긴 경우 처리할 로직을 작성하여 주세요. */
	boolean notiHashError(List<String> noti){
		/* TODO : 관련 로직 추가 */
		return false;
	}
	
	public HashMap<String,Object> payment(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//--------------------------------------------------------------------------------------------------------------//
		logger.info("** payment Start **");
		//--------------------------------------------------------------------------------------------------------------//
		// 만료된 페이지 설정
		request.setCharacterEncoding("utf-8");
		response.setHeader("cache-control", "no-cache");
		response.setHeader("pragma", "no-cache"); 
		response.setHeader("expire", "0");
		//--------------------------------------------------------------------------------------------------------------//
		// 사용 변수 선언
		Map<String,String> REQ_HEADER	= new LinkedHashMap<String,String>();						// 요청 파라미터(헤더)
		Map<String,String> REQ_BODY		= new LinkedHashMap<String,String>();						// 요청 파라미터(바디)
		Map<String,String> RES_HEADER	= new LinkedHashMap<String,String>();						// 응답 파라미터(헤더)
		Map<String,String> RES_BODY		= new LinkedHashMap<String,String>();						// 응답 파라미터(바디)
		Map<String,Object> reqParam		= new HashMap<String,Object>();								// 요청 파라미터(전문)
		Map<String,String> respParam	= new HashMap<String,String>();								// 응답 파라미터(전문)
		HashMap<String,Object> param	= new HashMap<String,Object>();
		PgModel pgConfigParam			= null;

		// 필수 파라미터 목록
		String[] requiredParam = {"kind","type","comId","mchtTrdNo","trdAmt","pmtprdNm","mchtCustId","mchtCustNm","cardNo","vldDtYear","vldDtMon","cardPwd","idntNo","instmtMon"};
		// 필수 파라미터 체크 인덱스
		int idx = 0;
		
		// AES256 암호화 필요 파라미터
		String[] ENCRYPT_PARAMS = {"cardNo","vldDtMon","vldDtYear","idntNo","cardPwd","trdAmt","taxAmt","vatAmt","taxFreeAmt","svcAmt"};
		// AES256 복호화 필요 파라미터
		String[] DECRYPT_PARAMS = {"trdAmt"};
		
		String myDomain = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		
		// PG 결제 정보
		String pgCardMid = "", pgCardKey = "", pgCardAesKey = "", requestUrl = "", notiUrl = "", mchtParam = "";
		//--------------------------------------------------------------------------------------------------------------//
		// 반환용 변수 선언
		HashMap<String,Object> retMap = new HashMap<String,Object>();
		//--------------------------------------------------------------------------------------------------------------//
		// 필수 파라미터 체크
		try {
			for(int i = 0; i < requiredParam.length; i++) {
				idx = i;
				request.getParameter(requiredParam[i]).toString();
			}
		} catch(Exception e) {
			logger.error(e.toString());
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		"필수 파라미터 누락(" + requiredParam[idx] + ")");
			retMap.put("status",		"E");
			return retMap;
		}
		// 파라미터 세팅
		Enumeration obj = request.getParameterNames();
		while( obj.hasMoreElements() ) {
			String key = obj.nextElement().toString();
			String val = request.getParameter(key);
			param.put(key,val);
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 중복결제 체크
		PgModel duplChk = pgMapper.getSettleCardDuplChk(param);
		if(duplChk.duplChk.equals("Y")) {
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		"중복결제 시도입니다.");
			retMap.put("status",		"E");
			
			param.put("remark",			"중복결제 시도입니다.");
			param.put("status",			"E");
			pgMapper.insertSettleCardLog(param);
			return retMap;
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 결제 정보 세팅
		try {
			pgConfigParam = pgMapper.getWowConfigPGInfo(param);
		} catch(Exception e) {
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		e.getMessage());
			retMap.put("status",		"E");
			
			param.put("remark",			e.getMessage());
			param.put("status",			"E");
			pgMapper.insertSettleCardLog(param);
			return retMap;
		}
		if(pgConfigParam.pgCardMid == null || pgConfigParam.pgCardMid.equals("") || pgConfigParam.pgCardKey == null || pgConfigParam.pgCardKey.equals("") || pgConfigParam.pgCardAesKey == null || pgConfigParam.pgCardAesKey.equals("")) {
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		"PG 결제 정보가 없습니다.");
			retMap.put("status",		"E");
			
			param.put("message",		"PG 결제 정보가 없습니다.");
			param.put("status",			"E");
			pgMapper.insertSettleCardLog(param);
			return retMap;
		} else {
			pgCardMid	= pgConfigParam.pgCardMid;
			pgCardKey	= pgConfigParam.pgCardKey;
			pgCardAesKey= pgConfigParam.pgCardAesKey;
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 결제 요청 헤더 세팅
		SimpleDateFormat day = new SimpleDateFormat ("yyyyMMdd");			// 거래일
		SimpleDateFormat time = new SimpleDateFormat ("HHmmss");			// 거래시간
		
		Date date = new Date();
		String trdDt = day.format(date);
		String trdTm = time.format(date);
		
		REQ_HEADER.put("mchtId",		pgCardMid);							// 상점아이디(헥토파이낸셜에서 발급하는 고유 상점아이디)
		REQ_HEADER.put("ver",			"0A17");							// 전문버전(1st[0] 고정 /2nd[A] 고정/ 3,4th:연동규격서버전. v1.9 => [19])
		REQ_HEADER.put("method",		"CA");								// 결제수단(카드[CA] 고정)
		REQ_HEADER.put("bizType",		"B0");								// 업무구분(승인[B0] 고정)
		REQ_HEADER.put("encCd",			"23");								// 암호화구분(AES-256-ECB[23] 고정)
		REQ_HEADER.put("mchtTrdNo",		((String)param.get("mchtTrdNo")));	// 상점주문번호(상점에서 생성하는 유니크한 주문번호)
		REQ_HEADER.put("trdDt",			trdDt);								// 요청일자(현재 전문을 요청하는 일자[yyyyMMdd])
		REQ_HEADER.put("trdTm",			trdTm);								// 요청시간(현재 전문을 요청하는 시간[HHmmss]
		REQ_HEADER.put("mobileYn",		"N");								// 모바일여부(모바일[Y] / PC[N])
		REQ_HEADER.put("osType",		"W");								// OS구분(Android[A]/ iOS[I] / Windows[W] / Mac[M] / others[E])
		
		logger.info("$$$### REQ_HEADER : " + REQ_HEADER + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 결제 요청 바디 세팅
		notiUrl = myDomain + "/settleNoti";
		mchtParam = "comId="+param.get("comId");
		REQ_BODY.put("pmtprdNm",		((String)param.get("pmtprdNm")));	// 상품명 - 결제상품명
		REQ_BODY.put("mchtCustNm",		((String)param.get("mchtCustNm")));	// 고객명 - 상점 고객명
		REQ_BODY.put("mchtCustId",		((String)param.get("mchtCustId")));	// 상점 고객아이디
		REQ_BODY.put("email",			((String)param.get("email")));		// 이메일 - 상점 고객 이메일주소
		REQ_BODY.put("cardNo",			((String)param.get("cardNo")));		// 카드번호
		REQ_BODY.put("vldDtMon",		((String)param.get("vldDtMon")));	// 유효기간(월) - 유효기간 MM
		REQ_BODY.put("vldDtYear",		((String)param.get("vldDtYear")));	// 유효기간(년) - 유효기간 YY
		REQ_BODY.put("idntNo",			((String)param.get("idntNo")));		// 식별번호 - 생년월일 6자리 또는 사업자 번호 10자리
		REQ_BODY.put("cardPwd",			((String)param.get("cardPwd")));	// 카드비밀번호 - 카드비밀번호 앞 2자리
		REQ_BODY.put("instmtMon",		((String)param.get("instmtMon")));	// 할부개월수 - 할부개월수 2자리
		REQ_BODY.put("crcCd",			"KRW");								// 통화구분([KRW] 고정)
		REQ_BODY.put("taxTypeCd",		"N");								// 세금유형 - 과세[N] / 면세[Y] / 복합과세[G]
		REQ_BODY.put("trdAmt",			((String)param.get("trdAmt")));		// 거래금액
		REQ_BODY.put("taxAmt",			"");								// 과세금액 - 거래금액 중 과세금액
		REQ_BODY.put("vatAmt",			"");								// 부가세금액 - 거래금액 중 부가세금액
		REQ_BODY.put("taxFreeAmt",		"");								// 비과세금액 - 거래금액 중 비과세금액
		REQ_BODY.put("svcAmt",			"");								// 봉사료 - 신용카드 봉사료
		REQ_BODY.put("notiUrl",			notiUrl);							// 결과처리 URL - 결제완료 후, 헥토파이낸셜에서 상점으로 전달하는 노티(결과통보)를 수신하는 Callback URL 작성
		REQ_BODY.put("mchtParam",		mchtParam);							// 상점예약필드 - 기타 주문 정보를 입력하는 상점 예약 필드
		REQ_BODY.put("keyRegYn",		"N");
		
		logger.info("$$$### REQ_BODY : " + REQ_BODY + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 응답 파라미터 헤더 세팅
		RES_HEADER.put("mchtId",		"");								// 상점아이디(헥토파이낸셜에서 발급하는 고유 상점아이디)
		RES_HEADER.put("ver",			"");								// 전문버전(1st[0] 고정 /2nd[A] 고정/ 3,4th:연동규격서버전. v1.9 => [19])
		RES_HEADER.put("method",		"");								// 결제수단([CA] 고정)
		RES_HEADER.put("bizType",		"");								// 업무구분([B0] 고정)
		RES_HEADER.put("encCd",			"");								// 암호화구분(AES-256-ECB[23] 고정)
		RES_HEADER.put("mchtTrdNo",		"");								// 상점주문번호(상점에서 생성하는 유니크한 주문번호)
		RES_HEADER.put("trdNo",			"");								// 헥토파이낸셜거래번호(헥토파이낸셜에서 발급한 고유한 거래번호)
		RES_HEADER.put("trdDt",			"");								// 요청일자(현재 전문을 요청하는 일자[yyyyMMdd])
		RES_HEADER.put("trdTm",			"");								// 요청시간(현재 전문을 요청하는 시간[HHmmss]
		RES_HEADER.put("outStatCd",		"");								// 거래상태(거래상태코드(성공/실패) - 0021 성공 / 0031 실패)
		RES_HEADER.put("outRsltCd",		"");								// 거절코드(거래상태가 "0031"일 경우, 상세코드 전달)
		RES_HEADER.put("outRsltMsg",	"");								// 결과메세지(결과 메세지 전달 - URL Encoding, UTF-8)
		
		logger.info("$$$### RES_HEADER : " + RES_HEADER + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 응답 파라미터 바디 세팅
		RES_BODY.put("pktHash",			"");
		RES_BODY.put("trdAmt",			"");
		RES_BODY.put("issrId",			"");
		RES_BODY.put("cardNm",			"");
		RES_BODY.put("ninstmtTypeCd",	"");
		RES_BODY.put("instmtMon",		"");
		RES_BODY.put("apprNo",			"");
		
		logger.info("$$$### RES_BODY : " + RES_BODY + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 해쉬필드 조합 및 암호화 필드 암호화 처리
		/** ===============================================================================================
		 *                          SHA256 해쉬 처리
		 *  조합필드 : 거래일자 + 거래시간 + 상점아이디 + 상점거래번호 + 거래금액 + 라이센스키
		 *  ===============================================================================================   */
		String hashPlain="";
		String hashCipher="";
		try {
		    hashPlain  = String.format("%s%s%s%s%s%s"
		               , REQ_HEADER.get("trdDt")
		               , REQ_HEADER.get("trdTm")
		               , REQ_HEADER.get("mchtId")
		               , REQ_HEADER.get("mchtTrdNo")
		               , REQ_BODY.get("trdAmt")
		               , pgCardKey);
		    
		    hashCipher = EncryptUtil.digestSHA256(hashPlain);
		} catch(Exception e) {
		    logger.error("["+REQ_HEADER.get("mchtTrdNo")+"][SHA256 HASHING] Hashing Fail! : " + e.toString());
		} finally {
		    logger.info("["+REQ_HEADER.get("mchtTrdNo")+"][SHA256 HASHING] Plain Text["+hashPlain+"] ---> Cipher Text["+hashCipher+"]");
		    REQ_BODY.put("pktHash", hashCipher); //해쉬 결과 값 세팅
		}
		
		param.put("status","0");
		param.putAll(REQ_HEADER);
		param.putAll(REQ_BODY);
		pgMapper.insertSettleCardLog(param);
		
		/** =======================================================================
		 *                          AES256 암호화 처리
		 *  =======================================================================  */
		try{
		    for(int i=0; i < ENCRYPT_PARAMS.length; i++){
		        String aesPlain = REQ_BODY.get(ENCRYPT_PARAMS[i]);
		        if( !("".equals(aesPlain))){
		            byte[] aesCipherRaw = EncryptUtil.aes256EncryptEcb(pgCardAesKey, aesPlain);
		            String aesCipher = EncryptUtil.encodeBase64(aesCipherRaw);
		            
		            REQ_BODY.put(ENCRYPT_PARAMS[i], aesCipher); //암호화 결과 값 세팅
		            logger.info("["+REQ_HEADER.get("mchtTrdNo")+"][AES256 Encrypt] "+ENCRYPT_PARAMS[i]+"["+aesPlain+"] ---> ["+aesCipher+"]");
		        }
		    }
		}catch(Exception e){
		    logger.error("[" + REQ_HEADER.get("mchtTrdNo") + "][AES256 Encrypt] AES256 Encrypt Fail! : " + e.toString());
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 전문 조합 및 전송
		/** ===============================================================================================
		 *                              API호출(가맹점->세틀) 및 응답 처리
		 *  ===============================================================================================   */
		// params, data 이름은 세틀로 전달되야 하는 값이니 변경하지 마십시오.
		reqParam.put("params",	REQ_HEADER);
		reqParam.put("data",	REQ_BODY);
		//requestUrl = serverURL + "/spay/APICardActionPay.do";
		requestUrl = serverURLTest + "/spay/APICardActionPay.do";
		
		try {
			HttpClientUtil httpClientUtil = new HttpClientUtil();
			String resData = httpClientUtil.sendApi(requestUrl, reqParam, connTimeout, readTimeout);
			
			// 응답 파라미터 파싱
			JSONObject resp       = JSONObject.fromObject(resData);
			JSONObject respHeader = resp.has("params")? resp.getJSONObject("params") : null; 
		    JSONObject respBody   = resp.has("data")? resp.getJSONObject("data") : null;
		    
		    logger.info("response body : " + respBody.toString() + "!!!");
		    
		    // 응답 파라미터 세팅(헤더)
		    if( respHeader != null ){
		        for (String key : RES_HEADER.keySet()) {
		            respParam.put(key, StringUtil.isNull( respHeader.has(key)? respHeader.getString(key) : ""));
		        }
		    }else{
		        for (String key : RES_HEADER.keySet()) {
		            respParam.put(key, "");
		        }
		    }
		    
		    // 응답 파라미터 세팅(바디)
		    if( respBody != null){
		        for (String key : RES_BODY.keySet()) {
		            respParam.put(key, StringUtil.isNull( respBody.has(key)? respBody.getString(key) : ""));
		        }
		    }else{
		        for (String key : RES_BODY.keySet()) {
		            respParam.put(key, "");
		        }
		    }
		} catch(Exception e) {
		    logger.error("["+REQ_HEADER.get("mchtTrdNo")+"][Response Parsing Error]" + e.toString());
		    retMap.put("rsltCd",		"9999");
		    retMap.put("message",		"[Response Parsing Error]" + e.toString());
		    retMap.put("status",		"E");
		    return retMap;
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 응답 전문 복호화 처리
		/** ======================================================================
		        AES256 복호화 처리
		======================================================================   */
		try{
			for(int i=0; i< DECRYPT_PARAMS.length; i++){
				if( respParam.containsKey(DECRYPT_PARAMS[i]) ){
					String aesCipher = (respParam.get(DECRYPT_PARAMS[i])).trim();
					logger.info("aesCipher : " + aesCipher + "!!!");
					if( !("".equals(aesCipher))){
						byte[] aesCipherRaw = EncryptUtil.decodeBase64(aesCipher);
						String aesPlain = new String(EncryptUtil.aes256DecryptEcb(pgCardAesKey, aesCipherRaw), "UTF-8");
			
						respParam.put(DECRYPT_PARAMS[i], aesPlain);//복호화된 데이터로 세팅
						logger.info("["+REQ_HEADER.get("mchtTrdNo")+"][AES256 Decrypt] "+DECRYPT_PARAMS[i]+"["+aesCipher+"] ---> ["+aesPlain+"]");
					}
				}
			}
		}catch(Exception e){
			logger.error("[" + REQ_HEADER.get("mchtTrdNo") + "][AES256 Decrypt] AES256 Decrypt Fail! : " + e.toString());
		}
		logger.info("$$$### respParam : " + respParam + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		hashPlain = String.format("%s%s%s%s%s%s%s"
				                 ,respParam.get("outStatCd")
				                 ,respParam.get("trdDt")
				                 ,respParam.get("trdTm")
				                 ,respParam.get("mchtId")
				                 ,respParam.get("mchtTrdNo")
				                 ,respParam.get("trdAmt")
				                 ,pgConfigParam.pgCardKey);
		try {
			hashCipher = EncryptUtil.digestSHA256(hashPlain);
		} catch(Exception e) {
			logger.error("["+param.get("mchtTrdNo")+"][SHA256 HASHING] Hasing Fail! : " + e.toString());
		} finally {
			logger.info("["+param.get("mchtTrdNo")+"][SHA256 HASING] Plain Text["+hashPlain+"] ---> Cipher Text["+hashCipher+"]");
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 결과에 따라 응답 값 처리
		if(respParam.get("outStatCd").equals("0021") && respParam.get("outRsltCd").trim().equals("0000")) {
			if (hashCipher.equals(respParam.get("pktHash"))) {
				logger.info("["+param.get("mchtTrdNo")+"][SHA256 Hash Check] hashCipher["+hashCipher+"] pktHash["+respParam.get("pktHash")+"] equals?[TRUE]");
				
			} else {
				logger.info("["+param.get("mchtTrdNo")+"][SHA256 Hash Check] hashCipher["+hashCipher+"] pktHash["+respParam.get("pktHash")+"] equals?[FALSE]");
				param.put("status",		"E");
				param.put("remark",		"해시 불일치");
				pgMapper.updateSettleCardResLog(param);
				
				retMap.put("rsltCd",	"9999");
				retMap.put("message",	"해시 불일치");
				retMap.put("status",	"E");
				return retMap;
			}
			
			retMap.put("rsltCd",	respParam.get("outRsltCd"));
			retMap.put("message",	respParam.get("apprNo"));
			retMap.put("status",	"S");
			param.put("status",		"1");
		}else {
			retMap.put("rsltCd",	respParam.get("outRsltCd"));
			retMap.put("message",	respParam.get("outRsltMsg"));
			retMap.put("status",	"F");
			param.put("status",		"2");
		}
		//--------------------------------------------------------------------------------------------------------------//
		logger.info("$$$### retMap : " + retMap + " ###$$$");
		logger.info("** payment End **");
		//--------------------------------------------------------------------------------------------------------------//
		if(retMap.isEmpty()) {
			retMap.put("rsltCd",	"9999");
			retMap.put("message",	"비정상적 접근입니다.");
			retMap.put("status",	"E");
			
			param.put("remark",		"비정상적 접근입니다.");
			param.put("status",		"E");
		}
		param.putAll(respParam);
		pgMapper.updateSettleCardResLog(param);
		return retMap;
	}
	
	public HashMap<String,Object> cancel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//--------------------------------------------------------------------------------------------------------------//
		logger.info("** cancel Start **");
		//--------------------------------------------------------------------------------------------------------------//
		// 만료된 페이지 설정
		request.setCharacterEncoding("utf-8");
		response.setHeader("cache-control", "no-cache");
		response.setHeader("pragma", "no-cache"); 
		response.setHeader("expire", "0");
		//--------------------------------------------------------------------------------------------------------------//
		// 사용 변수 선언
		Map<String,String> REQ_HEADER	= new LinkedHashMap<String,String>();						// 요청 파라미터(헤더)
		Map<String,String> REQ_BODY		= new LinkedHashMap<String,String>();						// 요청 파라미터(바디)
		Map<String,String> RES_HEADER	= new LinkedHashMap<String,String>();						// 응답 파라미터(헤더)
		Map<String,String> RES_BODY		= new LinkedHashMap<String,String>();						// 응답 파라미터(바디)
		Map<String,Object> reqParam		= new HashMap<String,Object>();								// 요청 파라미터(전문)
		Map<String,String> respParam	= new HashMap<String,String>();								// 응답 파라미터(전문)
		HashMap<String,Object> param	= new HashMap<String,Object>();
		PgModel pgConfigParam			= null;

		// 필수 파라미터 목록
		String[] requiredParam = {"kind","type","comId","mchtTrdNo","workUser"};
		// 필수 파라미터 체크 인덱스
		int idx = 0;
		
		// AES256 암호화 필요 파라미터
		String[] ENCRYPT_PARAMS = {"cnclAmt","taxAmt","vatAmt","taxFreeAmt","svcAmt"};
		// AES256 복호화 필요 파라미터
		String[] DECRYPT_PARAMS = {"cnclAmt","cardCnclAmt","pntCnclAmt","blcAmt"};
		
		String myDomain = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		
		// PG 결제 정보
		String pgCardMid = "", pgCardKey = "", pgCardAesKey = "", requestUrl = "";
		//--------------------------------------------------------------------------------------------------------------//
		// 반환용 변수 선언
		HashMap<String,Object> retMap = new HashMap<String,Object>();
		//--------------------------------------------------------------------------------------------------------------//
		// 필수 파라미터 체크
		try {
			for(int i = 0; i < requiredParam.length; i++) {
				idx = i;
				request.getParameter(requiredParam[i]).toString();
			}
		} catch(Exception e) {
			logger.error(e.toString());
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		"필수 파라미터 누락(" + requiredParam[idx] + ")");
			retMap.put("status",		"E");
			return retMap;
		}
		// 파라미터 세팅
		Enumeration obj = request.getParameterNames();
		while( obj.hasMoreElements() ) {
			String key = obj.nextElement().toString();
			String val = request.getParameter(key);
			param.put(key,val);
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 중복결제 체크
		PgModel duplChk = pgMapper.getSettleCancelDuplChk(param);
		if(duplChk.duplChk.equals("Y")) {
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		"중복취소 시도입니다.");
			retMap.put("status",		"E");
			
			param.put("remark",			"중복취소 시도입니다.");
			param.put("status",			"E");
			pgMapper.insertSettleCancelLog(param);
			return retMap;
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 결제 정보 세팅
		try {
			pgConfigParam = pgMapper.getWowConfigPGInfo(param);
		} catch(Exception e) {
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		e.getMessage());
			retMap.put("status",		"E");
			
			param.put("remark",			e.getMessage());
			param.put("status",			"E");
			pgMapper.insertSettleCancelLog(param);
			return retMap;
		}
		if(pgConfigParam.pgCardMid == null || pgConfigParam.pgCardMid.equals("") || pgConfigParam.pgCardKey == null || pgConfigParam.pgCardKey.equals("") || pgConfigParam.pgCardAesKey == null || pgConfigParam.pgCardAesKey.equals("")) {
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		"PG 결제 정보가 없습니다.");
			retMap.put("status",		"E");
			
			param.put("message",		"PG 결제 정보가 없습니다.");
			param.put("status",			"E");
			pgMapper.insertSettleCancelLog(param);
			return retMap;
		} else {
			pgCardMid	= pgConfigParam.pgCardMid;
			pgCardKey	= pgConfigParam.pgCardKey;
			pgCardAesKey= pgConfigParam.pgCardAesKey;
		}
		//--------------------------------------------------------------------------------------------------------------//
		HashMap<String,Object> cnclPrm = pgMapper.getSettleCancelInfo(param);
		// 결제 요청 헤더 세팅
		SimpleDateFormat day = new SimpleDateFormat ("yyyyMMdd");			// 거래일
		SimpleDateFormat time = new SimpleDateFormat ("HHmmss");			// 거래시간
		
		Date date = new Date();
		String trdDt = day.format(date);
		String trdTm = time.format(date);
		
		REQ_HEADER.put("mchtId",		pgCardMid);							// 상점아이디(헥토파이낸셜에서 발급하는 고유 상점아이디)
		REQ_HEADER.put("ver",			"0A19");							// 전문버전(1st[0] 고정 /2nd[A] 고정/ 3,4th:연동규격서버전. v1.9 => [19])
		REQ_HEADER.put("method",		"CA");								// 결제수단(카드[CA] 고정)
		REQ_HEADER.put("bizType",		"C0");								// 업무구분(취소[C0] 고정)
		REQ_HEADER.put("encCd",			"23");								// 암호화구분(AES-256-ECB[23] 고정)
		REQ_HEADER.put("mchtTrdNo",		((String)param.get("mchtTrdNo")));	// 상점주문번호(상점에서 생성하는 유니크한 주문번호)
		REQ_HEADER.put("trdDt",			trdDt);								// 요청일자(현재 전문을 요청하는 일자[yyyyMMdd])
		REQ_HEADER.put("trdTm",			trdTm);								// 요청시간(현재 전문을 요청하는 시간[HHmmss]
		REQ_HEADER.put("mobileYn",		"N");								// 모바일여부(모바일[Y] / PC[N])
		REQ_HEADER.put("osType",		"W");								// OS구분(Android[A]/ iOS[I] / Windows[W] / Mac[M] / others[E])
		
		logger.info("$$$### REQ_HEADER : " + REQ_HEADER + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 결제 요청 바디 세팅
		REQ_BODY.put("orgTrdNo",		"");
		REQ_BODY.put("crcCd",			"KRW");								// 통화구분([KRW] 고정)
		REQ_BODY.put("taxTypeCd",		"N");								// 세금유형 - 과세[N] / 면세[Y] / 복합과세[G]
		REQ_BODY.put("cnclAmt",			((String)cnclPrm.get("cnclAmt")));	// 거래금액
		REQ_BODY.put("taxAmt",			"");								// 과세금액 - 거래금액 중 과세금액
		REQ_BODY.put("vatAmt",			"");								// 부가세금액 - 거래금액 중 부가세금액
		REQ_BODY.put("taxFreeAmt",		"");								// 비과세금액 - 거래금액 중 비과세금액
		REQ_BODY.put("svcAmt",			"");								// 봉사료 - 신용카드 봉사료
		REQ_BODY.put("cnclRsn",			"");								// 취소사유
		
		logger.info("$$$### REQ_BODY : " + REQ_BODY + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 응답 파라미터 헤더 세팅
		RES_HEADER.put("mchtId",		"");								// 상점아이디(헥토파이낸셜에서 발급하는 고유 상점아이디)
		RES_HEADER.put("ver",			"");								// 전문버전(1st[0] 고정 /2nd[A] 고정/ 3,4th:연동규격서버전. v1.9 => [19])
		RES_HEADER.put("method",		"");								// 결제수단([CA] 고정)
		RES_HEADER.put("bizType",		"");								// 업무구분([B0] 고정)
		RES_HEADER.put("encCd",			"");								// 암호화구분(AES-256-ECB[23] 고정)
		RES_HEADER.put("mchtTrdNo",		"");								// 상점주문번호(상점에서 생성하는 유니크한 주문번호)
		RES_HEADER.put("trdNo",			"");								// 헥토파이낸셜거래번호(헥토파이낸셜에서 발급한 고유한 거래번호)
		RES_HEADER.put("trdDt",			"");								// 요청일자(현재 전문을 요청하는 일자[yyyyMMdd])
		RES_HEADER.put("trdTm",			"");								// 요청시간(현재 전문을 요청하는 시간[HHmmss]
		RES_HEADER.put("outStatCd",		"");								// 거래상태(거래상태코드(성공/실패) - 0021 성공 / 0031 실패)
		RES_HEADER.put("outRsltCd",		"");								// 거절코드(거래상태가 "0031"일 경우, 상세코드 전달)
		RES_HEADER.put("outRsltMsg",	"");								// 결과메세지(결과 메세지 전달 - URL Encoding, UTF-8)
		
		logger.info("$$$### RES_HEADER : " + RES_HEADER + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 응답 파라미터 바디 세팅
		RES_BODY.put("pktHash",			"");
		RES_BODY.put("orgTrdNo",		"");
		RES_BODY.put("cnclAmt",			"");
		RES_BODY.put("cardCnclAmt",		"");
		RES_BODY.put("pntcnclAmt",		"");
		RES_BODY.put("blcAmt",			"");
		
		logger.info("$$$### RES_BODY : " + RES_BODY + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 해쉬필드 조합 및 암호화 필드 암호화 처리
		/** ===============================================================================================
		 *                          SHA256 해쉬 처리
		 *  조합필드 : 거래일자 + 거래시간 + 상점아이디 + 상점거래번호 + 거래금액 + 라이센스키
		 *  ===============================================================================================   */
		String hashPlain="";
		String hashCipher="";
		try {
		    hashPlain  = String.format("%s%s%s%s%s%s"
		               , REQ_HEADER.get("trdDt")
		               , REQ_HEADER.get("trdTm")
		               , REQ_HEADER.get("mchtId")
		               , REQ_HEADER.get("mchtTrdNo")
		               , REQ_BODY.get("cnclAmt")
		               , pgCardKey);
		    
		    hashCipher = EncryptUtil.digestSHA256(hashPlain);
		} catch(Exception e) {
		    logger.error("["+REQ_HEADER.get("mchtTrdNo")+"][SHA256 HASHING] Hashing Fail! : " + e.toString());
		} finally {
		    logger.info("["+REQ_HEADER.get("mchtTrdNo")+"][SHA256 HASHING] Plain Text["+hashPlain+"] ---> Cipher Text["+hashCipher+"]");
		    REQ_BODY.put("pktHash", hashCipher); //해쉬 결과 값 세팅
		}
		
		param.put("status","0");
		param.putAll(REQ_HEADER);
		param.putAll(REQ_BODY);
		pgMapper.insertSettleCancelLog(param);
		
		/** =======================================================================
		 *                          AES256 암호화 처리
		 *  =======================================================================  */
		try{
		    for(int i=0; i < ENCRYPT_PARAMS.length; i++){
		        String aesPlain = REQ_BODY.get(ENCRYPT_PARAMS[i]);
		        if( !("".equals(aesPlain))){
		            byte[] aesCipherRaw = EncryptUtil.aes256EncryptEcb(pgCardAesKey, aesPlain);
		            String aesCipher = EncryptUtil.encodeBase64(aesCipherRaw);
		            
		            REQ_BODY.put(ENCRYPT_PARAMS[i], aesCipher); //암호화 결과 값 세팅
		            logger.info("["+REQ_HEADER.get("mchtTrdNo")+"][AES256 Encrypt] "+ENCRYPT_PARAMS[i]+"["+aesPlain+"] ---> ["+aesCipher+"]");
		        }
		    }
		}catch(Exception e){
		    logger.error("[" + REQ_HEADER.get("mchtTrdNo") + "][AES256 Encrypt] AES256 Encrypt Fail! : " + e.toString());
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 전문 조합 및 전송
		/** ===============================================================================================
		 *                              API호출(가맹점->세틀) 및 응답 처리
		 *  ===============================================================================================   */
		// params, data 이름은 세틀로 전달되야 하는 값이니 변경하지 마십시오.
		reqParam.put("params",	REQ_HEADER);
		reqParam.put("data",	REQ_BODY);
		//requestUrl = serverURL + "/spay/APICardActionPay.do";
		requestUrl = serverURLTest + "/spay/APICancel.do";
		
		try {
			HttpClientUtil httpClientUtil = new HttpClientUtil();
			String resData = httpClientUtil.sendApi(requestUrl, reqParam, connTimeout, readTimeout);
			
			// 응답 파라미터 파싱
			JSONObject resp       = JSONObject.fromObject(resData);
			JSONObject respHeader = resp.has("params")? resp.getJSONObject("params") : null; 
		    JSONObject respBody   = resp.has("data")? resp.getJSONObject("data") : null;
		    
		    logger.info("response body : " + respBody.toString() + "!!!");
		    
		    // 응답 파라미터 세팅(헤더)
		    if( respHeader != null ){
		        for (String key : RES_HEADER.keySet()) {
		            respParam.put(key, StringUtil.isNull( respHeader.has(key)? respHeader.getString(key) : ""));
		        }
		    }else{
		        for (String key : RES_HEADER.keySet()) {
		            respParam.put(key, "");
		        }
		    }
		    
		    // 응답 파라미터 세팅(바디)
		    if( respBody != null){
		        for (String key : RES_BODY.keySet()) {
		            respParam.put(key, StringUtil.isNull( respBody.has(key)? respBody.getString(key) : ""));
		        }
		    }else{
		        for (String key : RES_BODY.keySet()) {
		            respParam.put(key, "");
		        }
		    }
		} catch(Exception e) {
		    logger.error("["+REQ_HEADER.get("mchtTrdNo")+"][Response Parsing Error]" + e.toString());
		    retMap.put("rsltCd",		"9999");
		    retMap.put("message",		"[Response Parsing Error]" + e.toString());
		    retMap.put("status",		"E");
		    return retMap;
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 응답 전문 복호화 처리
		/** ======================================================================
		        AES256 복호화 처리
		======================================================================   */
		try{
			for(int i=0; i< DECRYPT_PARAMS.length; i++){
				if( respParam.containsKey(DECRYPT_PARAMS[i]) ){
					String aesCipher = (respParam.get(DECRYPT_PARAMS[i])).trim();
					logger.info("aesCipher : " + aesCipher + "!!!");
					if( !("".equals(aesCipher))){
						byte[] aesCipherRaw = EncryptUtil.decodeBase64(aesCipher);
						String aesPlain = new String(EncryptUtil.aes256DecryptEcb(pgCardAesKey, aesCipherRaw), "UTF-8");
			
						respParam.put(DECRYPT_PARAMS[i], aesPlain);//복호화된 데이터로 세팅
						logger.info("["+REQ_HEADER.get("mchtTrdNo")+"][AES256 Decrypt] "+DECRYPT_PARAMS[i]+"["+aesCipher+"] ---> ["+aesPlain+"]");
					}
				}
			}
		}catch(Exception e){
			logger.error("[" + REQ_HEADER.get("mchtTrdNo") + "][AES256 Decrypt] AES256 Decrypt Fail! : " + e.toString());
		}
		logger.info("$$$### respParam : " + respParam + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		hashPlain = String.format("%s%s%s%s%s%s%s"
				                 ,respParam.get("outStatCd")
				                 ,respParam.get("trdDt")
				                 ,respParam.get("trdTm")
				                 ,respParam.get("mchtId")
				                 ,respParam.get("mchtTrdNo")
				                 ,respParam.get("cnclAmt")
				                 ,pgConfigParam.pgCardKey);
		try {
			hashCipher = EncryptUtil.digestSHA256(hashPlain);
		} catch(Exception e) {
			logger.error("["+param.get("mchtTrdNo")+"][SHA256 HASHING] Hasing Fail! : " + e.toString());
		} finally {
			logger.info("["+param.get("mchtTrdNo")+"][SHA256 HASING] Plain Text["+hashPlain+"] ---> Cipher Text["+hashCipher+"]");
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 결과에 따라 응답 값 처리
		if(respParam.get("outStatCd").equals("0021") && respParam.get("outRsltCd").trim().equals("0000")) {
			if (hashCipher.equals(respParam.get("pktHash"))) {
				logger.info("["+param.get("mchtTrdNo")+"][SHA256 Hash Check] hashCipher["+hashCipher+"] pktHash["+respParam.get("pktHash")+"] equals?[TRUE]");
				
			} else {
				logger.info("["+param.get("mchtTrdNo")+"][SHA256 Hash Check] hashCipher["+hashCipher+"] pktHash["+respParam.get("pktHash")+"] equals?[FALSE]");
				param.put("status",		"E");
				param.put("remark",		"해시 불일치");
				pgMapper.updateSettleCancelResLog(param);
				
				retMap.put("rsltCd",	"9999");
				retMap.put("message",	"해시 불일치");
				retMap.put("status",	"E");
				return retMap;
			}
			
			retMap.put("rsltCd",	respParam.get("outRsltCd"));
			retMap.put("message",	respParam.get("outRsltMsg"));
			retMap.put("status",	"S");
			param.put("status",		"1");
		}else {
			retMap.put("rsltCd",	respParam.get("outRsltCd"));
			retMap.put("message",	respParam.get("outRsltMsg"));
			retMap.put("status",	"F");
			param.put("status",		"2");
		}
		//--------------------------------------------------------------------------------------------------------------//
		logger.info("$$$### retMap : " + retMap + " ###$$$");
		logger.info("** cancel End **");
		//--------------------------------------------------------------------------------------------------------------//
		if(retMap.isEmpty()) {
			retMap.put("rsltCd",	"9999");
			retMap.put("message",	"비정상적 접근입니다.");
			retMap.put("status",	"E");
			
			param.put("remark",		"비정상적 접근입니다.");
			param.put("status",		"E");
		}
		param.putAll(respParam);
		pgMapper.updateSettleCardResLog(param);
		return retMap;
	}
	
	public HashMap<String,Object> vaccNumber(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//--------------------------------------------------------------------------------------------------------------//
		logger.info("** vaccNumber Start **");
		//--------------------------------------------------------------------------------------------------------------//
		// 만료된 페이지 설정
		request.setCharacterEncoding("utf-8");
		response.setHeader("cache-control", "no-cache");
		response.setHeader("pragma", "no-cache"); 
		response.setHeader("expire", "0");
		//--------------------------------------------------------------------------------------------------------------//
		// 사용 변수 선언
		Map<String,String> REQ_HEADER	= new LinkedHashMap<String,String>();						// 요청 파라미터(헤더)
		Map<String,String> REQ_BODY		= new LinkedHashMap<String,String>();						// 요청 파라미터(바디)
		Map<String,String> RES_HEADER	= new LinkedHashMap<String,String>();						// 응답 파라미터(헤더)
		Map<String,String> RES_BODY		= new LinkedHashMap<String,String>();						// 응답 파라미터(바디)
		Map<String,Object> reqParam		= new HashMap<String,Object>();								// 요청 파라미터(전문)
		Map<String,String> respParam	= new HashMap<String,String>();								// 응답 파라미터(전문)
		HashMap<String,Object> param	= new HashMap<String,Object>();
		PgModel pgConfigParam			= null;

		// 필수 파라미터 목록
		String[] requiredParam = {"kind","type","comId","mchtTrdNo","bankCd","expireDate","prdtNm","trdAmt","mchtCustNm","mchtCustId","csrcIssReqYn","cashRcptPrposDivCd","csrcRegNoDivCd","csrcRegNo"};
		// 필수 파라미터 체크 인덱스
		int idx = 0;
		
		// AES256 암호화 필요 파라미터
		String[] ENCRYPT_PARAMS = {"vAcntNo","trdAmt","taxAmt","vatAmt","taxFreeAmt","escrPwd","rfdDpstrNm","csrcRegNo"};
		// AES256 복호화 필요 파라미터
		String[] DECRYPT_PARAMS = {"vAcntNo","trdAmt"};
		
		String myDomain = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		
		// PG 결제 정보
		String pgVbankMid = "", pgVbankKey = "", pgVbankAesKey = "", requestUrl = "", notiUrl = "", mchtParam = "";
		//--------------------------------------------------------------------------------------------------------------//
		// 반환용 변수 선언
		HashMap<String,Object> retMap = new HashMap<String,Object>();
		//--------------------------------------------------------------------------------------------------------------//
		// 필수 파라미터 체크
		try {
			for(int i = 0; i < requiredParam.length; i++) {
				idx = i;
				request.getParameter(requiredParam[i]).toString();
			}
		} catch(Exception e) {
			logger.error(e.toString());
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		"필수 파라미터 누락(" + requiredParam[idx] + ")");
			retMap.put("status",		"E");
			return retMap;
		}
		// 파라미터 세팅
		Enumeration obj = request.getParameterNames();
		while( obj.hasMoreElements() ) {
			String key = obj.nextElement().toString();
			String val = request.getParameter(key);
			param.put(key,val);
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 중복결제 체크
		PgModel duplChk = pgMapper.getSettleVbankDuplChk(param);
		if(duplChk.duplChk.equals("Y")) {
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		"중복결제 시도입니다.");
			retMap.put("status",		"E");
			
			param.put("remark",			"중복결제 시도입니다.");
			param.put("status",			"E");
			pgMapper.insertSettleVbankLog(param);
			return retMap;
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 결제 정보 세팅
		try {
			pgConfigParam = pgMapper.getWowConfigPGInfo(param);
		} catch(Exception e) {
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		e.getMessage());
			retMap.put("status",		"E");
			
			param.put("remark",			e.getMessage());
			param.put("status",			"E");
			pgMapper.insertSettleVbankLog(param);
			return retMap;
		}
		if(pgConfigParam.pgVbankMid == null || pgConfigParam.pgVbankMid.equals("") || pgConfigParam.pgVbankKey == null || pgConfigParam.pgVbankKey.equals("") || pgConfigParam.pgVbankAesKey == null || pgConfigParam.pgVbankAesKey.equals("")) {
			retMap.put("rsltCd",		"9999");
			retMap.put("message",		"PG 결제 정보가 없습니다.");
			retMap.put("status",		"E");
			
			param.put("message",		"PG 결제 정보가 없습니다.");
			param.put("status",			"E");
			pgMapper.insertSettleVbankLog(param);
			return retMap;
		} else {
			pgVbankMid		= pgConfigParam.pgVbankMid;
			pgVbankKey		= pgConfigParam.pgVbankKey;
			pgVbankAesKey	= pgConfigParam.pgVbankAesKey;
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 결제 요청 헤더 세팅
		SimpleDateFormat day = new SimpleDateFormat ("yyyyMMdd");																// 거래일
		SimpleDateFormat time = new SimpleDateFormat ("HHmmss");																// 거래시간
		
		Date date = new Date();
		String trdDt = day.format(date);
		String trdTm = time.format(date);
		
		REQ_HEADER.put("mchtId",		pgVbankMid);																			// 상점아이디(헥토파이낸셜에서 발급하는 고유 상점아이디)
		REQ_HEADER.put("ver",			"0A19");																				// 전문버전(1st[0] 고정 /2nd[A] 고정/ 3,4th:연동규격서버전. v1.9 => [19])
		REQ_HEADER.put("method",		"VA");																					// 결제수단(가상계좌[VA] 고정)
		REQ_HEADER.put("bizType",		"A0");																					// 업무구분(채번[A0] 고정)
		REQ_HEADER.put("encCd",			"23");																					// 암호화구분(AES-256-ECB[23] 고정)
		REQ_HEADER.put("mchtTrdNo",		((String)param.get("mchtTrdNo")));														// 상점주문번호(상점에서 생성하는 유니크한 주문번호)
		REQ_HEADER.put("trdDt",			trdDt);																					// 요청일자(현재 전문을 요청하는 일자[yyyyMMdd])
		REQ_HEADER.put("trdTm",			trdTm);																					// 요청시간(현재 전문을 요청하는 시간[HHmmss]
		REQ_HEADER.put("mobileYn",		"N");																					// 모바일여부(모바일[Y] / PC[N])
		REQ_HEADER.put("osType",		"W");																					// OS구분(Android[A]/ iOS[I] / Windows[W] / Mac[M] / others[E])
		
		logger.info("$$$### REQ_HEADER : " + REQ_HEADER + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 결제 요청 바디 세팅
		notiUrl = myDomain + "/settleNoti";
		mchtParam = "comId="+param.get("comId");
		REQ_BODY.put("bankCd",				((String)param.get("bankCd")));														// 가상계좌 은행코드
		REQ_BODY.put("acntType",			"1");																				// 계좌구분(1 : 기본(회전식), 2 : 고정식, 3 : 고정무제한)
		REQ_BODY.put("vAcntNo",				"");																				// 가상계좌번호(고정식일 경우에만 사용)
		REQ_BODY.put("expireDate",			((String)param.get("expireDate")));													// 입금만료일시(가상계좌 신청 후 입금이 되어야 하는 기한 일시 YYYYMMDDHHmmss / 공란 입력 시 거래일 기준 + 10일로 자동 세팅)
		REQ_BODY.put("prdtNm",				((String)param.get("prdtNm")));														// 상품명 - 결제상품명
		REQ_BODY.put("sellerNm",			pgConfigParam.comName);																// 판매자명
		REQ_BODY.put("ordNm",				((String)param.get("mchtCustNm")));													// 주문자명
		REQ_BODY.put("trdAmt",				((String)param.get("trdAmt")));														// 거래금액
		REQ_BODY.put("dpstrNm",				pgConfigParam.comName);																// 통장인자명 - 고객의 통장에 찍히는 통장인자명
		REQ_BODY.put("mchtCustNm",			((String)param.get("mchtCustNm")));													// 입금자명
		REQ_BODY.put("taxTypeCd",			"N");																				// 세금유형 - 과세[N] / 면세[Y] / 복합과세[G]
		REQ_BODY.put("taxAmt",				"");																				// 과세금액 - 거래금액 중 과세금액
		REQ_BODY.put("vatAmt",				"");																				// 부가세금액 - 거래금액 중 부가세금액
		REQ_BODY.put("taxFreeAmt",			"");																				// 비과세금액 - 거래금액 중 비과세금액
		REQ_BODY.put("escrAgrYn",			"N");																				// 에스크로 사용여부(Y : 동의, N : 비동의)
		REQ_BODY.put("escrPwd",				"");																				// 에스크로 비밀번호(에스크로 사용 시 필수)
		REQ_BODY.put("rfdDpstrNm",			"");																				// 에스크로 환불 시 예금주명(에스크로 사용 시 필수)
		REQ_BODY.put("csrcIssReqYn",		param.get("csrcIssReqYn") == "" ? "N" : ((String)param.get("csrcIssReqYn")));		// 발행여부 - 현금영수증 발행여부(Y : 발행, N : 미발행)
		REQ_BODY.put("cashRcptPrposDivCd",	((String)param.get("cashRcptPrposDivCd")));											// 용도구분 - 현금영수증 용도구분(현금영수증 사용 시 필수) / (0 : 소득증빙용, 1 : 지출증빙용)
		REQ_BODY.put("csrcRegNoDivCd",		((String)param.get("csrcRegNoDivCd")));												// 등록번호 구분코드 - 현금영수증 등록번호 구분코드(현금영수증 사용 시 필수) / (1 : 카드, 2 : 주민번호, 3 : 사업자번호, 4 : 휴대폰번호)
		REQ_BODY.put("csrcRegNo",			((String)param.get("csrcRegNo")));													// 고유식별정보 - 현금영수증 고유식별정보(현금영수증 사용 시 필수)
		REQ_BODY.put("email",				((String)param.get("email")));														// 이메일 - 상점 고객 이메일주소
		REQ_BODY.put("notiUrl",				notiUrl);																			// 결과처리 URL - 결제완료 후, 헥토파이낸셜에서 상점으로 전달하는 노티(결과통보)를 수신하는 Callback URL 작성
		REQ_BODY.put("mchtParam",			mchtParam);																			// 상점예약필드 - 기타 주문 정보를 입력하는 상점 예약 필드
		REQ_BODY.put("mchtCustId",			((String)param.get("mchtCustId")));													// 상점 고객아이디
		
		logger.info("$$$### REQ_BODY : " + REQ_BODY + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 응답 파라미터 헤더 세팅
		RES_HEADER.put("mchtId",		"");																					// 상점아이디(헥토파이낸셜에서 발급하는 고유 상점아이디)
		RES_HEADER.put("ver",			"");																					// 전문버전(1st[0] 고정 /2nd[A] 고정/ 3,4th:연동규격서버전. v1.9 => [19])
		RES_HEADER.put("method",		"");																					// 결제수단([CA] 고정)
		RES_HEADER.put("bizType",		"");																					// 업무구분([B0] 고정)
		RES_HEADER.put("encCd",			"");																					// 암호화구분(AES-256-ECB[23] 고정)
		RES_HEADER.put("mchtTrdNo",		"");																					// 상점주문번호(상점에서 생성하는 유니크한 주문번호)
		RES_HEADER.put("trdNo",			"");																					// 헥토파이낸셜거래번호(헥토파이낸셜에서 발급한 고유한 거래번호)
		RES_HEADER.put("trdDt",			"");																					// 요청일자(현재 전문을 요청하는 일자[yyyyMMdd])
		RES_HEADER.put("trdTm",			"");																					// 요청시간(현재 전문을 요청하는 시간[HHmmss]
		RES_HEADER.put("outStatCd",		"");																					// 거래상태(거래상태코드(성공/실패) - 0021 성공 / 0031 실패)
		RES_HEADER.put("outRsltCd",		"");																					// 거절코드(거래상태가 "0031"일 경우, 상세코드 전달)
		RES_HEADER.put("outRsltMsg",	"");																					// 결과메세지(결과 메세지 전달 - URL Encoding, UTF-8)
		
		logger.info("$$$### RES_HEADER : " + RES_HEADER + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 응답 파라미터 바디 세팅
		RES_BODY.put("pktHash",			"");
		RES_BODY.put("bankCd",			"");
		RES_BODY.put("vAcntNo",			"");
		RES_BODY.put("expireDate",		"");
		RES_BODY.put("trdAmt",			"");
		RES_BODY.put("acntType",		"");
		
		logger.info("$$$### RES_BODY : " + RES_BODY + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		// 해쉬필드 조합 및 암호화 필드 암호화 처리
		/** ===============================================================================================
		 *                          SHA256 해쉬 처리
		 *  조합필드 : 거래일자 + 거래시간 + 상점아이디 + 상점거래번호 + 거래금액 + 라이센스키
		 *  ===============================================================================================   */
		String hashPlain="";
		String hashCipher="";
		try {
		    hashPlain  = String.format("%s%s%s%s%s%s"
		               , REQ_HEADER.get("trdDt")
		               , REQ_HEADER.get("trdTm")
		               , REQ_HEADER.get("mchtId")
		               , REQ_HEADER.get("mchtTrdNo")
		               , REQ_BODY.get("trdAmt")
		               , pgVbankKey);
		    
		    hashCipher = EncryptUtil.digestSHA256(hashPlain);
		} catch(Exception e) {
		    logger.error("["+REQ_HEADER.get("mchtTrdNo")+"][SHA256 HASHING] Hashing Fail! : " + e.toString());
		} finally {
		    logger.info("["+REQ_HEADER.get("mchtTrdNo")+"][SHA256 HASHING] Plain Text["+hashPlain+"] ---> Cipher Text["+hashCipher+"]");
		    REQ_BODY.put("pktHash", hashCipher); //해쉬 결과 값 세팅
		}
		
		param.put("status","0");
		param.putAll(REQ_HEADER);
		param.putAll(REQ_BODY);
		pgMapper.insertSettleVbankLog(param);
		
		/** =======================================================================
		 *                          AES256 암호화 처리
		 *  =======================================================================  */
		try{
		    for(int i=0; i < ENCRYPT_PARAMS.length; i++){
		        String aesPlain = REQ_BODY.get(ENCRYPT_PARAMS[i]);
		        if( !("".equals(aesPlain))){
		            byte[] aesCipherRaw = EncryptUtil.aes256EncryptEcb(pgVbankAesKey, aesPlain);
		            String aesCipher = EncryptUtil.encodeBase64(aesCipherRaw);
		            
		            REQ_BODY.put(ENCRYPT_PARAMS[i], aesCipher); //암호화 결과 값 세팅
		            logger.info("["+REQ_HEADER.get("mchtTrdNo")+"][AES256 Encrypt] "+ENCRYPT_PARAMS[i]+"["+aesPlain+"] ---> ["+aesCipher+"]");
		        }
		    }
		}catch(Exception e){
		    logger.error("[" + REQ_HEADER.get("mchtTrdNo") + "][AES256 Encrypt] AES256 Encrypt Fail! : " + e.toString());
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 전문 조합 및 전송
		/** ===============================================================================================
		 *                              API호출(가맹점->세틀) 및 응답 처리
		 *  ===============================================================================================   */
		// params, data 이름은 세틀로 전달되야 하는 값이니 변경하지 마십시오.
		reqParam.put("params",	REQ_HEADER);
		reqParam.put("data",	REQ_BODY);
		//requestUrl = serverURL + "/spay/APIVBank.do";
		requestUrl = serverURLTest + "/spay/APIVBank.do";
		
		try {
			HttpClientUtil httpClientUtil = new HttpClientUtil();
			String resData = httpClientUtil.sendApi(requestUrl, reqParam, connTimeout, readTimeout);
			
			// 응답 파라미터 파싱
			JSONObject resp       = JSONObject.fromObject(resData);
			JSONObject respHeader = resp.has("params")? resp.getJSONObject("params") : null; 
		    JSONObject respBody   = resp.has("data")? resp.getJSONObject("data") : null;
		    
		    logger.info("response body : " + respBody.toString() + "!!!");
		    
		    // 응답 파라미터 세팅(헤더)
		    if( respHeader != null ){
		        for (String key : RES_HEADER.keySet()) {
		            respParam.put(key, StringUtil.isNull( respHeader.has(key)? respHeader.getString(key) : ""));
		        }
		    }else{
		        for (String key : RES_HEADER.keySet()) {
		            respParam.put(key, "");
		        }
		    }
		    
		    // 응답 파라미터 세팅(바디)
		    if( respBody != null){
		        for (String key : RES_BODY.keySet()) {
		            respParam.put(key, StringUtil.isNull( respBody.has(key)? respBody.getString(key) : ""));
		        }
		    }else{
		        for (String key : RES_BODY.keySet()) {
		            respParam.put(key, "");
		        }
		    }
		} catch(Exception e) {
		    logger.error("["+REQ_HEADER.get("mchtTrdNo")+"][Response Parsing Error]" + e.toString());
		    retMap.put("rsltCd",		"9999");
		    retMap.put("message",		"[Response Parsing Error]" + e.toString());
		    retMap.put("status",		"E");
		    
		    param.put("remark",			"[Response Parsing Error]" + e.toString());
		    param.put("status",			"E");
		    pgMapper.updateSettleVbankResLog(param);
		    return retMap;
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 응답 전문 복호화 처리
		/** ======================================================================
		        AES256 복호화 처리
		======================================================================   */
		try{
			for(int i=0; i< DECRYPT_PARAMS.length; i++){
				if( respParam.containsKey(DECRYPT_PARAMS[i]) ){
					String aesCipher = (respParam.get(DECRYPT_PARAMS[i])).trim();
					logger.info("aesCipher : " + aesCipher + "!!!");
					if( !("".equals(aesCipher))){
						byte[] aesCipherRaw = EncryptUtil.decodeBase64(aesCipher);
						String aesPlain = new String(EncryptUtil.aes256DecryptEcb(pgVbankAesKey, aesCipherRaw), "UTF-8");
			
						respParam.put(DECRYPT_PARAMS[i], aesPlain);//복호화된 데이터로 세팅
						logger.info("["+REQ_HEADER.get("mchtTrdNo")+"][AES256 Decrypt] "+DECRYPT_PARAMS[i]+"["+aesCipher+"] ---> ["+aesPlain+"]");
					}
				}
			}
		}catch(Exception e){
			logger.error("[" + REQ_HEADER.get("mchtTrdNo") + "][AES256 Decrypt] AES256 Decrypt Fail! : " + e.toString());
		}
		logger.info("$$$### respParam : " + respParam + " ###$$$");
		//--------------------------------------------------------------------------------------------------------------//
		hashPlain = String.format("%s%s%s%s%s%s%s"
				                 ,respParam.get("outStatCd")
				                 ,respParam.get("trdDt")
				                 ,respParam.get("trdTm")
				                 ,respParam.get("mchtId")
				                 ,respParam.get("mchtTrdNo")
				                 ,respParam.get("trdAmt")
				                 ,pgConfigParam.pgVbankKey);
		try {
			hashCipher = EncryptUtil.digestSHA256(hashPlain);
		} catch(Exception e) {
			logger.error("["+param.get("mchtTrdNo")+"][SHA256 HASHING] Hasing Fail! : " + e.toString());
		} finally {
			logger.info("["+param.get("mchtTrdNo")+"][SHA256 HASING] Plain Text["+hashPlain+"] ---> Cipher Text["+hashCipher+"]");
		}
		//--------------------------------------------------------------------------------------------------------------//
		// 결과에 따라 응답 값 처리
		if(respParam.get("outStatCd").equals("0021") && respParam.get("outRsltCd").trim().equals("0000")) {
			if (hashCipher.equals(respParam.get("pktHash"))) {
				logger.info("["+param.get("mchtTrdNo")+"][SHA256 Hash Check] hashCipher["+hashCipher+"] pktHash["+respParam.get("pktHash")+"] equals?[TRUE]");
				
			} else {
				logger.info("["+param.get("mchtTrdNo")+"][SHA256 Hash Check] hashCipher["+hashCipher+"] pktHash["+respParam.get("pktHash")+"] equals?[FALSE]");
				retMap.put("rsltCd",	"9999");
				retMap.put("message",	"해시 불일치");
				retMap.put("status",	"E");
				
				param.put("remark",		"해시 불일치");
				param.put("status",		"E");
				pgMapper.updateSettleVbankResLog(param);
				return retMap;
			}
			
			retMap.put("rsltCd",	respParam.get("outRsltCd"));
			retMap.put("message",	respParam.get("vAcntNo"));
			retMap.put("status",	"S");
			param.put("status",		"1");
		}else {
			retMap.put("rsltCd",	respParam.get("outRsltCd"));
			retMap.put("message",	respParam.get("outRsltMsg"));
			retMap.put("status",	"F");
			param.put("status",		"2");
		}
		//--------------------------------------------------------------------------------------------------------------//
		logger.info("$$$### retMap : " + retMap + " ###$$$");
		logger.info("** payment End **");
		//--------------------------------------------------------------------------------------------------------------//
		if(retMap.isEmpty()) {
			retMap.put("rsltCd",	"9999");
			retMap.put("message",	"비정상적 접근입니다.");
			retMap.put("status",	"E");
			
			param.put("remark",		"비정상적 접근입니다.");
			param.put("status",		"E");
		}
		param.putAll(respParam);
		pgMapper.updateSettleVbankResLog(param);
		return retMap;
	}
	
	@RequestMapping(value="/settleNoti")
	private void settleNoti(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//--------------------------------------------------------------------------------------------------------------//
		logger.info("** settleNoti Start **");
		//--------------------------------------------------------------------------------------------------------------//
		// 만료된 페이지 설정
		request.setCharacterEncoding("utf-8");
		response.setHeader("cache-control", "no-cache");
		response.setHeader("pragma", "no-cache"); 
		response.setHeader("expire", "0");
		
		PrintWriter out = response.getWriter();
		//--------------------------------------------------------------------------------------------------------------//
		HashMap<String,Object> param	= new HashMap<String,Object>();
		PgModel pgConfigParam			= null;
		String comId					= "";
		String pgKey					= "";
		String comVaccNotiUrl			= "";
		String hashPlain				= "";
		String hashCipher				= "";
		boolean resp					= false;
		//--------------------------------------------------------------------------------------------------------------//
		try {
			// 파라미터 세팅
			Enumeration obj = request.getParameterNames();
			while( obj.hasMoreElements() ) {
				String key = obj.nextElement().toString();
				String val = request.getParameter(key);
				param.put(key,val);
			}
			//--------------------------------------------------------------------------------------------------------------//
			// 응답 파라미터 List에 저장
			ArrayList<String> noti = new ArrayList<>();
			noti.add("거래상태:"+			param.get("outStatCd"));
			noti.add("거래번호:"+			param.get("trdNo"));
			noti.add("결제수단:"+			param.get("method"));
			noti.add("업무구분:"+			param.get("bizType"));
			noti.add("상점아이디:"+		param.get("mchtId"));
			noti.add("상점거래번호:"+		param.get("mchtTrdNo"));
			noti.add("주문자명:"+			param.get("mchtCustNm"));
			noti.add("상점한글명:"+		param.get("mchtName"));
			noti.add("상품명:"+			param.get("pmtprdNm"));
			noti.add("거래일시:"+			param.get("trdDtm"));
			noti.add("거래금액:"+			param.get("trdAmt"));
			noti.add("자동결제키:"+		param.get("billKey"));
			noti.add("자동결제키 유효기간:"+	param.get("billKeyExpireDt"));
			noti.add("은행코드:"+			param.get("bankCd"));
			noti.add("은행명:"+			param.get("bankNm"));
			noti.add("카드사코드:"+		param.get("cardCd"));
			noti.add("카드명:"+			param.get("cardNm"));
			noti.add("이통사코드:"+		param.get("telecomCd"));
			noti.add("이통사명:"+			param.get("telecomNm"));
			noti.add("가상계좌번호:"+		param.get("vAcntNo"));
			noti.add("가상계좌 입금만료일자:"+param.get("expireDt"));
			noti.add("통장인자명:"+		param.get("AcntPrintNm"));
			noti.add("입금자명:"+			param.get("dpstrNm"));
			noti.add("고객이메일:"+		param.get("email"));
			noti.add("상점고객아이디:"+		param.get("mchtCustId"));
			noti.add("카드번호:"+			param.get("cardNo"));
			noti.add("카드승인번호:"+		param.get("cardApprNo"));
			noti.add("할부개월수:"+		param.get("instmtMon"));
			noti.add("할부타입:"+			param.get("instmtType"));
			noti.add("휴대폰번호(암호화):"+	param.get("phoneNoEnc"));
			noti.add("원거래번호:"+		param.get("orgTrdNo"));
			noti.add("원거래일자:"+		param.get("orgTrdDt"));
			noti.add("복합결제 거래번호:"+	param.get("mixTrdNo"));
			noti.add("복합결제 금액:"+		param.get("mixTrdAmt"));
			noti.add("실결제금액:"+		param.get("payAmt"));
			noti.add("현금영수증 승인번호:"+	param.get("csrcIssNo"));
			noti.add("취소거래타입:"+		param.get("cnclType"));
			noti.add("기타주문정보:"+		param.get("mchtParam"));
			noti.add("해쉬값:"+			param.get("pktHash")); //서버에서 전달된 해쉬 값
			
			logger.info("!!!!noti:"+noti);
			//--------------------------------------------------------------------------------------------------------------//
			logger.info("$$$### Response Param : " + param + " ###$$$");
			comId = param.get("mchtParam").toString().split("=")[1];
			param.put("comId",comId);
			pgConfigParam = pgMapper.getWowConfigPGInfo(param);
			if(param.get("method").equals("CA")) {
				pgKey = pgConfigParam.pgCardKey;
			} else if(param.get("method").equals("VA")) {
				pgKey = pgConfigParam.pgVbankKey;
				comVaccNotiUrl = pgConfigParam.vaccNotiUrl;
			}
			hashPlain = String.format("%s%s%s%s%s%s", param.get("outStatCd"), param.get("trdDtm"), param.get("mchtId"), param.get("mchtTrdNo"), param.get("trdAmt"), pgKey);
			try {
				hashCipher = EncryptUtil.digestSHA256(hashPlain);
			} catch(Exception e) {
				logger.error("["+param.get("mchtTrdNo")+"][SHA256 HASHING] Hasing Fail! : " + e.toString());
			} finally {
				logger.info("["+param.get("mchtTrdNo")+"][SHA256 HASING] Plain Text["+hashPlain+"] ---> Cipher Text["+hashCipher+"]");
			}
			//--------------------------------------------------------------------------------------------------------------//
			if (hashCipher.equals(param.get("pktHash"))) {
				resp = true;
				logger.info("["+param.get("mchtTrdNo")+"][SHA256 Hash Check] hashCipher["+hashCipher+"] pktHash["+param.get("pktHash")+"] equals?[TRUE]");
				logger.info("$$$### NOTI PARAM : " + param + " ###$$$");
				if("0021".equals(param.get("outStatCd"))) {
					resp = true;
					if(param.get("method").equals("VA")) {
						resp = sendNoti(param,comVaccNotiUrl);
					}
				} else if("0051".equals(param.get("outStatCd"))) {
					resp = true;
					pgMapper.insertSettleVbankNotiLog(param);
				} else {
					throw new Exception("결제 승인 실패 : " + param.get("outStatCd"));
				}
			} else {
				throw new Exception("해시 불일치 오류");
			}
		} catch(Exception e) {
			resp = false;
			logger.error(e.getMessage());
			param.put("remark",e.getMessage());
		} finally {
			if(param.get("method").equals("CA")) {
				pgMapper.updateSettleCardNotiLog(param);
			}else {
			}
			if(resp){
			    out.println("OK");
			    logger.info("["+ param.get("mchtTrdNo") + "][Result] OK");
			}else{
			    out.println("FAIL");
			    logger.info("["+ param.get("mchtTrdNo") + "][Result] FAIL");
			}
		}
		//--------------------------------------------------------------------------------------------------------------//
	}
	
	private boolean sendNoti(HashMap<String,Object> param, String comVaccNotiUrl) throws Exception {
		logger.info("$$$### sendNoti Start ###$$$");
		
		boolean resp = false;
		
		StringBuffer sb = new StringBuffer();
		sb.append("?moneyNo="+param.get("mchtTrdNo"));
		sb.append("&amt="+param.get("trdAmt"));
		
		String urlParams = sb.toString();
		
		try {
			URL url = new URL(((comVaccNotiUrl+"/vaccNoti.do")+urlParams));
			
			HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setUseCaches(false);
			
			resp = (con.getResponseCode() == 200);
		} catch(Exception e) {
			logger.error(e.toString());
		} finally {
			logger.info("$$$### resp : " + resp + "... ###$$$");
		}
		
		logger.info("$$$### sendNoti End ###$$$");
		return resp;
	}
}