package com.wow.api.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import com.wow.api.conf.Wownet_UrlReader;
import com.wow.api.dao.MaccoMapper;

import net.sf.json.JSONObject;

@Controller
public class maccoReportController {

	@Autowired
	private MaccoMapper maccoMapper;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	public  HashMap<String,Object>  maccoReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
		HashMap<String, Object> modelMap = new HashMap<String, Object>();
		
        request.setCharacterEncoding("utf-8");
        String orderId  = request.getParameter("ordNo");
        String procKind = request.getParameter("procKind");
        String comId    = request.getParameter("comId");
		try {
			
		    logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 공제번호 발급 시작 @@@@@@@@@@@@@@@@@@@@@@@@@");
		    HashMap<String,Object> urlparm = new HashMap<String,Object>();
		    urlparm.put("ordno", orderId);
		    urlparm.put("procKind", procKind);
		    urlparm.put("comId", comId);
		    

		   // HashMap<String,Object> rstUrlParm = commonService.select("wowOrder.selectUrlParameter_wownet", urlparm);
		    
		    HashMap<String,Object> rstUrlParm =urlparm;
		    		
            HashMap<String,Object> req = new HashMap<String,Object>();
            req.put("comId"      , rstUrlParm.get("comId"));        // 회사ID
            req.put("orderid"    , rstUrlParm.get("orderid"));      // 주문번호
            req.put("shopid"     , rstUrlParm.get("shopid"));       // Guild_CD
            req.put("ctype"      , rstUrlParm.get("ctype"));        // w 고정 
            req.put("totalmoney" , rstUrlParm.get("totalmoney"));   // 신고 금액 
            req.put("seller_type", rstUrlParm.get("sellerType"));   // 등급 회원 1 / 소비자 2 
            req.put("name"       , rstUrlParm.get("name"));         // 이름  
            req.put("userid"     , rstUrlParm.get("userid"));       // 생년월일 6자리  + 성별 번호 (M : 1 / F: 2)
            req.put("mem_id"     , rstUrlParm.get("memId"));        // 회원번호
            req.put("merc_code"  , rstUrlParm.get("mercCode"));     // '' 공백 
            req.put("returntype" , rstUrlParm.get("returntype"));   // xml 고정 
            req.put("jp_code"    , rstUrlParm.get("jpCode"));       // '' 공백 
            req.put("jp_pay_type", rstUrlParm.get("jpPayType"));    //신용카드 : CD  / 체크카드 : CC  / 현금(무통장입금 포함) : CH / 복합결제 : MI / 기타결제 : EC (UFJP_PAY_METHOD 함수 참조)
            req.put("workUser"   , rstUrlParm.get("workUser"));     // 작업자 
            req.put("ordTime"    , rstUrlParm.get("ordTime"));      // 작업시간 - 221026-142342
            req.put("ordKind"    , rstUrlParm.get("ordKind"));      // 주문상태  A , X 
            req.put("no"         , rstUrlParm.get("no"));           // 공제 번호   (취소요청 요망) 와 동일하면 공백 처리 존재 할경우는 그대로 공제번호 노출 
            req.put("oriNo"      , rstUrlParm.get("oriNo"));        // 공제 번호 
            req.put("procKind"   , rstUrlParm.get("procKind"));     // 1 고정 
            req.put("bpOrdNo"    , rstUrlParm.get("bpOrdNo"));      // 원 주문번호  ,  원주문 번호가 없을시 해당 주문번호   
            req.put("ctrCd"      , rstUrlParm.get("ctrCd"));        // 국가 코드 KR 만 사용 
            
            // 받은 파라메터 로그 저장. 
            
            logger.info("req:"+req);
            
            //maccoMapper.insertLogMacco(req);
            
            
            Wownet_UrlReader Wownet_UrlReader = new Wownet_UrlReader();
            
            HashMap<String,Object> rstUrl = Wownet_UrlReader.UrlReaderHttp(req);
            
            JSONObject resultJson = new JSONObject();
            Map<String, Object> uData = new LinkedHashMap<String, Object>();
            
            
		    //에러코드 확인
		    //https://m.macco.or.kr/it/errorcodeweb/list.action
            
            logger.info("errorCode:"+rstUrl.get("errorCode"));
            logger.info("guaranteeCode:"+rstUrl.get("guaranteeCode"));
            logger.info("guaranteeResult:"+rstUrl.get("guaranteeResult"));
            logger.info("cancelResult:"+rstUrl.get("cancelResult"));
            
            modelMap.put("errorCode", rstUrl.get("errorCode"));  // 결과 : 에러코드(에러 발생시에만 있다)
            modelMap.put("guaranteeCode", rstUrl.get("guaranteeCode"));  // 결과 : 공제번호(실패시 없다)
            modelMap.put("guaranteeResult", rstUrl.get("guaranteeResult"));  // 결과 : 발급성공여부
            modelMap.put("cancelResult", rstUrl.get("cancelResult"));  // 결과 : 공제번호(실패시 없다)
            
            uData = getJsonStringFromMap(modelMap);
            resultJson.put("data", uData);
            logger.info("resultJson : "+resultJson);
            
            /*
            PrintWriter out = response.getWriter();
            out.print(resultJson);
  	  	    out.flush();
  	  	    out.close();
  	  	    */
            
            //json 으로 변경 작업 진행해야함.
            
            /*if(rstUrlParm.get("procKind").equals("I")){
            	out.println(rstUrl.get("guaranteeCode")+"|"+rstUrl.get("guaranteeResult")+"|"+rstUrl.get("errorCode"));
            }else if(rstUrlParm.get("procKind").equals("U")){
            	out.println(rstUrl.get("guaranteeCode")+"|"+rstUrl.get("guaranteeResult")+"|"+rstUrl.get("errorCode"));
            }else{
            	out.println(rstUrl.get("guaranteeCode")+"|"+rstUrl.get("guaranteeResult")+"|"+rstUrl.get("errorCode"));
            }*/
            
            logger.info("wowreturn?: " + rstUrl.get("guaranteeCode")+"|"+rstUrl.get("guaranteeResult")+"|"+rstUrl.get("errorCode"));
            
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 공제번호 발급 끝 @@@@@@@@@@@@@@@@@@@@@@@@@");
		} catch(Exception ee1) {
			logger.info("$$$$ Error! : " + ee1.toString());
		    
		    
            JSONObject resultJson = new JSONObject();
            Map<String, Object> uData = new LinkedHashMap<String, Object>();
            
		    //에러코드 확인
		    //https://m.macco.or.kr/it/errorcodeweb/list.action
            
            modelMap.put("errorCode", "0000");     // 결과 : 에러코드(에러 발생시에만 있다)
            modelMap.put("guaranteeCode", "");     // 결과 : 공제번호(실패시 없다)
            modelMap.put("guaranteeResult", "N");  // 결과 : 발급성공여부
            modelMap.put("cancelResult", "");      // 결과 : 공제번호(실패시 없다)
            
            uData = getJsonStringFromMap(modelMap);
            resultJson.put("data", uData);
            logger.info("resultJson : "+resultJson);
            /*
            PrintWriter out = response.getWriter();
            out.print(resultJson);
  	  	    out.flush();
  	  	    out.close();		  
  	  	    */
		}
        
		return modelMap;
    }



/**
	 * Map을 jsonString으로 변환한다.
	 *
	 * @param map Map<String, Object>.
	 * @return String.
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getJsonStringFromMap( Map<String, Object> map ) {

		JSONObject json = new JSONObject();
		for( Map.Entry<String, Object> entry : map.entrySet() ) {
			String key = entry.getKey();
			Object value = entry.getValue();
			json.put(key, value);
		}
		
		return json;
	}

}
