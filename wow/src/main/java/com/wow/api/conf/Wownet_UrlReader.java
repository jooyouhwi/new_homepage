package com.wow.api.conf;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.InputSource;

import com.wow.api.dao.CompanyMapper;
import com.wow.api.dao.MaccoMapper;
import com.wow.api.model.CompanyModel;

import net.sf.json.JSONObject;

public class Wownet_UrlReader {
    //protected Log log = LogFactory.getLog(this.getClass());
    
	
	@Autowired
	private MaccoMapper maccoMapper;
	
    public HashMap<String,Object> UrlReaderHttp(HashMap<String,Object> param) {
        
        HashMap<String,Object> rtnMap = new HashMap<String,Object>();
        //StringBuffer sbuf = new StringBuffer();
        URL obj = null;
        HttpsURLConnection con = null;
        BufferedReader in = null;
        DataOutputStream wr = null; 
        
        
        
        //EgovMap userAccount = (EgovMap) request.getSession().getAttribute("userInfo");
        String workUser = (String)param.get("workUser");
        String ordTime  = (String)param.get("ordTime");
        String procKind = param.get("procKind") == null ? "" : "" + param.get("procKind");
        
        try {
        	//실사용  
            String url       = "https://real.macco.or.kr/deduct/deductPass.action";
            String urlCan    = "https://real.macco.or.kr/deduct/deductCancel.action";
            String urlNew    = "https://realt2.macco.or.kr/deduct/deductPass.action";
            String urlCanNew = "https://realt2.macco.or.kr/deduct/deductCancel.action";
            
            //테스트
        	//String url       = "https://realtest.macco.or.kr/deduct/deductPass.action";
            //String urlCan    = "https://realtest.macco.or.kr/deduct/deductCancel.action";
            
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs,
						String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs,
						String authType) {
				}
			} };
	    	
	    	SSLContext sc = SSLContext.getInstance("TLSv1.2");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
            if(procKind.equals("I")){
            	obj = new URL(url);	
            }else{
            	obj = new URL(urlCan);
            }
            
            con = (HttpsURLConnection) obj.openConnection();
     
            //add reuqest header
            con.setRequestMethod("POST");
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setRequestProperty("accept-charset", "UTF-8");
            con.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=utf-8");

            //==============================================================================
            //  직판 조합 전송 파라미터 구성한다.
            //==============================================================================
            /*
            -- Sample --
            prm.append("orderid=35570448");
            prm.append("&shopid=5500");
            prm.append("&ctype=W");
            prm.append("&totalmoney=100");
            prm.append("&seller_type=1");
            prm.append("&name=테스트");
            prm.append("&userid=111111");  // 주민등록번호(앞 7자리 사용)
            prm.append("&mem_id=12345");
            prm.append("&merc_code=");
            prm.append("&returntype=xml");
            */
            // Get Parameter ---------------------------------------------------
            
            HashMap<String,Object> urlparm = new HashMap<String,Object>();
			urlparm.put("orderid", param.get("orderid"));
			urlparm.put("ctrCd", param.get("ctrCd"));
			urlparm.put("comId", param.get("comId"));
            
			HashMap<String,Object> deliparam = new HashMap<String,Object>();
            System.out.println("### orderid :   " + urlparm.get("orderid"));
            System.out.println("### ctrCd   :   " + urlparm.get("ctrCd"));
            System.out.println("### comId   :   " + urlparm.get("comId"));
            //deliparam = (EgovMap) commonService.select("wowOrder.chkOrdDeli", urlparm);
            System.out.println("### totalmoney :   " + deliparam.get("totalmoney"));
            
            String comId       = param.get("comId") == null ? "" : "" + param.get("comId");
            String orderid     = param.get("orderid") == null ? "" : "" + param.get("orderid");
            String shopid      = param.get("shopid") == null ? "" : "" + param.get("shopid");
            String ctype       = param.get("ctype") == null ? "" : "" + param.get("ctype");
            String totalmoney  = deliparam.get("totalmoney") == null ? "" : "" + deliparam.get("totalmoney");
            String seller_type = param.get("seller_type") == null ? "" : "" + param.get("seller_type");
            String name        = param.get("name") == null ? "" : "" + param.get("name");
            String userid      = param.get("userid") == null ? "" : "" + param.get("userid");
            String mem_id      = param.get("mem_id") == null ? "" : "" + param.get("mem_id");
            String returntype  = param.get("returntype") == null ? "" : "" + param.get("returntype");
            String merc_code   = param.get("jp_code") == null ? "" : "" + param.get("jp_code");
            //String merc_code = param.get("merc_code") == null ? "" : "" + param.get("merc_code");
            String pay_method  = param.get("jp_pay_type") == null ? "" : "" + param.get("jp_pay_type");
            String ordKind     = param.get("ordKind") == null ? "" : "" + param.get("ordKind");
            String no          = param.get("no") == null ? "" : "" + param.get("no");
            String oriNo       = param.get("oriNo") == null ? "" : "" + param.get("oriNo");
            
            
            System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            System.out.println("공제신고 파라메터 확인");
            System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            System.out.println("comId:"+comId);
            System.out.println("orderid:"+orderid);
            System.out.println("shopid:"+shopid);
            System.out.println("ctype:"+ctype);
            System.out.println("totalmoney:"+totalmoney);
            System.out.println("seller_type:"+seller_type);
            System.out.println("name:"+name);
            System.out.println("userid:"+userid);
            System.out.println("mem_id:"+mem_id);
            System.out.println("returntype:"+returntype);
            System.out.println("merc_code:"+merc_code);
            System.out.println("pay_method:"+pay_method);
            System.out.println("ordKind:"+ordKind);
            System.out.println("no:"+no);
            System.out.println("oriNo:"+oriNo);
            System.out.println("procKind:"+procKind);
            System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");

            // 기본 문자열 UTF-8
            // 한글 문자열일 경우, UTF-8로 강제 인코딩 하여 전송한다.
            // (아래 라인 빠질 경우, 한글 문자열 깨짐)
            name = java.net.URLEncoder.encode(name,"UTF-8");
            
            StringBuffer prm = new StringBuffer();
            
            // Set Parameter  --------------------------------------------------
            
            //--------------------------------------------------------------------------
            // 주문번호
            //--------------------------------------------------------------------------
            String vOrderid = "";
            
            if(procKind.equals("I")){
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	System.out.println("주문시 주문번호");
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	
            	vOrderid = param.get("orderid") == null ? "" : "" + param.get("orderid");
            //--------------------------------------------------------------------------
            // 취소파라미터 구성
            //--------------------------------------------------------------------------
            }else if(procKind.equals("U")){
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	System.out.println("취소시 주문번호");
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	
            	vOrderid = param.get("orderid") == null ? "" : "" + param.get("orderid");
        	//--------------------------------------------------------------------------
            // 교환파라미터 구성
            //--------------------------------------------------------------------------
            }else if(procKind.equals("B")){
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	System.out.println("교환반품시 주문번호");
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	
            	vOrderid = param.get("bpOrdNo") == null ? "" : "" + param.get("bpOrdNo");
            }   
            
            System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        	System.out.println("공제신고 주문번호 : "+vOrderid);
        	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            
            //--------------------------------------------------------------------------
            // 파라미터 구성 : 공통
            //--------------------------------------------------------------------------            
            //prm.append("orderid=" + orderid); // 주문번호 : 주문번호는 고유한 번호여야 함
            prm.append("orderid=" + vOrderid); // 주문번호 : 주문번호는 고유한 번호여야 함
            prm.append("&shopid=" + shopid); // 회원ID : 숫자 4자리 형태 (5500:테스트)
            prm.append("&ctype=" + ctype); // 구분 : (W:웹)
            prm.append("&totalmoney=" + totalmoney); // 구매금액 : 1회공제한도금액(1원~3천만원)
            prm.append("&returntype=" + returntype); // 리턴 데이터 방식 : (xml : XML방식, json:JSON방식)
            
            //--------------------------------------------------------------------------
            // 주문파라미터 구성
            //--------------------------------------------------------------------------
            if(procKind.equals("I")){
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	System.out.println("주문");
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	prm.append("&userid=" + userid);  // 주민등록번호(앞 7자리 사용)
                prm.append("&name=" + name); // 구매자명 : Character set 은 UTF-8 사용요망
                prm.append("&mem_id=" + mem_id); // 회원ID : 판매원(소비자) 고유번호
            	prm.append("&seller_type=" + seller_type); // 구매자 구분 : (1:판매원, 2:일반)
                prm.append("&merc_code="  + merc_code); // 중계코드 : 중계특례회원사인 경우에만 데이터 전송해야 함.
                prm.append("&pay_method=" + pay_method); // 중계코드 : 중계특례회원사인 경우에만 데이터 전송해야 함.
            //--------------------------------------------------------------------------
            // 취소파라미터 구성
            //--------------------------------------------------------------------------
            }else if(procKind.equals("U")){
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	System.out.println("취소");
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	prm.append("&guaranteecode=" + no); // 중계코드 : 중계특례회원사인 경우에만 데이터 전송해야 함.
        	//--------------------------------------------------------------------------
            // 교환파라미터 구성
            //--------------------------------------------------------------------------
            }else if(procKind.equals("B")){
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	System.out.println("교환반품");
            	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
            	prm.append("&guaranteecode=" + oriNo); // 중계코드 : 중계특례회원사인 경우에만 데이터 전송해야 함.
            }
            
            String urlParameters = prm.toString(); // 파라메터
     
            //==================================================================
            // 직접판매공제 조합에 파라미터 전송.
            //==================================================================
            con.setDoOutput(true);
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
     
            //==================================================================
            // Response : 전문 수신 결과를 저장.
            //==================================================================
            HashMap<String,Object> res = new HashMap<String,Object>(); // 
            
            int responseCode = con.getResponseCode();
            
            // 결과 전문을 읽어온다. ===========================================
            if (responseCode == 200) {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
         
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                
                //print result
                System.out.println(response.toString());
                
                /* 
                 * 결과 전문 파싱 처리 -----------------------------------------
                 *
                 * xml 로 받아온 결과를 JDOM 라이브러리를 이용하여 값을 추출한다.
                 *
                 * 결과 예)
                 * <?xml version="1.0" encoding="UTF-8" ?> 
                 * <result>
                 *     <package>
                 *         <orderID>M000002516</orderID> 
                 *         <guaranteeResult>Y</guaranteeResult> 
                 *         <memID>00001330</memID> 
                 *         <GuaranteeCode>64713031</GuaranteeCode> 
                 *         <mallID>5500</mallID> 
                 *     </package>
                 * </result>
                 */
                InputSource input = new InputSource(new StringReader( response.toString() )); // 리던값을 InputSource 에 넣는다.
                input.setEncoding("UTF-8");

                // JDOM 라이브러리
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(input); // xml파일을 부른다.
                
                Element xmlRoot = doc.getRootElement(); // xml파일의 첫번째 태그를 부른다.
                
                List<Element> elements = xmlRoot.getChildren();  // 첫번째 List 형태로 불러온다.
                
                for (Element elemt : elements) { // 첫번째 List 개수 만큼 루프를 실행(result 로드 안의 로드에 접근한다.)
                    List<Element> elements2 = elemt.getChildren(); // 두번째 List 형태로 불러온다.
                    
                    for (Element elemt2 : elements2) { // 두번째 List 개수 만큼 루프를 실행(package 로드 안의 로드에 접근한다.)
                        /*
                         * package 로드 안에 있는 로드를 읽어온다.
                         *   - orderID, guaranteeResult, memID, GuaranteeCode, mallID
                         */
                        System.out.println("$$$$$$$$$$$$$$$$ " + elemt2.getName() + " = " + elemt2.getText());
                        res.put(elemt2.getName(), elemt2.getText());
                    }
                }
                
                System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★res CHK:"+res);
                
                rtnMap.put("guaranteeResult", res.get("guaranteeResult")); // 결과 : 발급성공여부
                
                if (res.get("errorCode") != null) {
                    rtnMap.put("errorCode", res.get("errorCode")); // 결과 : 에러코드(에러 발생시에만 있다)
                }else{
                	rtnMap.put("errorCode", "0000"); // 결과 : 에러코드(에러 발생시에만 있다)
                }
                
                if (res.get("cancelResult") != null) {
                    rtnMap.put("cancelResult", res.get("cancelResult")); // 결과 : 에러코드(에러 발생시에만 있다)
                }
                
                // 공제번호 : guaranteeResult 값이 Y, D 일때만 공제번호출력
                if (res.get("GuaranteeCode") != null) {
                	rtnMap.put("guaranteeCode", res.get("GuaranteeCode")); // 결과 : 공제번호(성공)
                } else if (res.get("guaranteeCode") != null) {
                	rtnMap.put("guaranteeCode", res.get("guaranteeCode")); // 결과 : 공제번호(성공)
                } else {
                    res.put("guaranteeCode", "");
                    res.put("GuaranteeCode", "");
                    rtnMap.put("guaranteeCode", ""); // 결과 : 공제번호(실패시 없다)
                }
                
                /** 반품취소 파라메터 재정의 Start **/
                /* ** 반품(B), 취소(U)의 경우. 파라메터가 다름. 파라메터를 재정의 한다.** */
                // orderID       주문번호
                // errorCode     에러코드
                // cancelResult  취소결과 (Y/N)
                // mallID        가맹점번호
                // guaranteeCode 공제번호
                
                if (procKind.equals("I")) {
                	rtnMap.put("errorCode", res.get("ErrorCode"));
                }
                
                if (procKind.equals("U") || procKind.equals("B")) {
                	res.put("ErrorCode", res.get("errorCode"));
                	res.put("guaranteeResult", res.get("cancelResult"));
                	res.put("GuaranteeCode", res.get("guaranteeCode"));
                	
                	rtnMap.put("guaranteeCode", res.get("guaranteeCode"));
                	rtnMap.put("guaranteeResult", res.get("cancelResult"));
                	rtnMap.put("errorCode", res.get("errorCode"));
                }
                
                // rtnMap에 담아 리턴한다.
                /** 반품취소 파라메터 재정의 End **/
                
                HashMap<String,Object> paramObj = new HashMap<String,Object>();
                paramObj.put("comId", param.get("comId"));         // Ord_NO : 주문번호
                paramObj.put("ORD_NO", param.get("orderid"));         // Ord_NO : 주문번호
                //paramObj.put("GUBUN", "0");                         // Gubun : ‘0’       -- 주문등록 신고 구분자값. (취소시 ‘1’)
                paramObj.put("GUBUN", ordKind);                       // Gubun : ‘0’       -- 주문등록 신고 구분자값. (취소시 ‘1’)
                paramObj.put("LICENSE_NO", res.get("GuaranteeCode")); // License_NO : 공제번호 ? 정상 신고 완료시 발번된 공제번호, 실패시에는 공백
                paramObj.put("SEND_YN", res.get("guaranteeResult"));  // Send_YN : ‘N’         -- 결과 수신 값. guaranteeResult
                paramObj.put("ERR_CD", res.get("ErrorCode"));         // Err_CD : ‘’        -- 방화벽 차단일땐 공백, 에러코드 받았으면 에러코드 넣어 준다.

                paramObj.put("AMT", param.get("totalmoney"));         // Amt : 가격정보
                paramObj.put("SELLER_CD", "0");                       // Seller_CD : ‘0’
                paramObj.put("USER_NAME", param.get("name"));         // UserName : 회원명
                paramObj.put("JUMIN_NO", param.get("userid"));        // Jumin_NO : 앞 7자리
                paramObj.put("USER_ID", param.get("mem_id"));         // Userid : 회원번호
                paramObj.put("WORK_USER", workUser);                  // Work_User : 작업자 아이디
                paramObj.put("ORD_TIME", ordTime);                    // Ord_Time : 주문일시 (YYMMDD-HH24MISS)
                
                // 저장 (로그데이터 DB 저장) =======================================
                //commonService.insert("wowOrder.insertOrdGuild", paramObj);
                
                maccoMapper.insertOrdGuild(paramObj);
                
                
                if (res.get("guaranteeResult") != null && "Y".equals(res.get("guaranteeResult"))) {
                    // 성공  -> API 경우 테이블 직접적으로 UPDATE 하지 않는다.
                	//maccoMapper.updateOrdGuildSuccess(paramObj);  
                	
                    //rtnMap.put("rtnMessage", "주문과 공제번호 발번 모두 정상적으로 되었습니다.");
                } else {
                	// 실패  -> API 경우 테이블 직접적으로 UPDATE 하지 않는다.
                	//maccoMapper.updateOrdGuildFail(paramObj);
                    rtnMap.put("rtnMessage", "주문은 정상적으로 되었으나 공제번호 발번은 실패 되었습니다.");
                }
                
            } else {
                // 200 이 아닌경우
                
                /*EgovMap paramObj = new EgovMap();
                paramObj.put("ORD_NO", param.get("orderid"));  // Ord_NO : 주문번호
                paramObj.put("GUBUN", "0");                    // Gubun : ‘0’       -- 주문등록 신고 구분자값. (취소시 ‘1’)
                paramObj.put("LICENSE_NO", "");                // License_NO : 공제번호 ? 정상 신고 완료시 발번된 공제번호, 실패시에는 공백
                paramObj.put("SEND_YN", "N");                  // Send_YN : ‘N’         -- 직판에 데이터 보내지 못했으면 ‘N’, 에러코드라도 받았으면 ‘Y’
                paramObj.put("ERR_CD", "");                    // Err_CD : ‘’        -- 방화벽 차단일땐 공백, 에러코드 받았으면 에러코드 넣어 준다.
                paramObj.put("AMT", param.get("totalmoney"));  // Amt : 가격정보
                paramObj.put("SELLER_CD", "0");                // Seller_CD : ‘0’
                paramObj.put("USER_NAME", param.get("name"));  // UserName : 회원명
                paramObj.put("JUMIN_NO", param.get("userid")); // Jumin_NO : 앞 7자리
                paramObj.put("USER_ID", param.get("mem_id"));  // Userid : 회원번호
                paramObj.put("WORK_USER", workUser);           // Work_User : 작업자 아이디
                paramObj.put("ORD_TIME", ordTime);             // Ord_Time : 주문일시 (YYMMDD-HH24MISS)
                
                // 저장
                commonService.insert("order.insertOrdGuild", paramObj);*/
                
                throw new Exception();
            }
            in.close();
             
        } catch (Exception e) {
        	System.out.println("$$$$ Error!" + e.toString());
            //==================================================================
            // 방화벽 차단(결과 값을 전혀 받지 못하는 경우에 대한 로그 기록
            //==================================================================
            HashMap<String,Object> paramObj = new HashMap<String,Object>();
            paramObj.put("ORD_NO", param.get("orderid"));  // Ord_NO : 주문번호
            paramObj.put("GUBUN", "0");                    // Gubun : ‘0’       -- 주문등록 신고 구분자값. (취소시 ‘1’)
            paramObj.put("LICENSE_NO", "");                // License_NO : 공제번호 ? 정상 신고 완료시 발번된 공제번호, 실패시에는 공백
            paramObj.put("SEND_YN", "N");                  // Send_YN : ‘N’         -- 직판에 데이터 보내지 못했으면 ‘N’, 에러코드라도 받았으면 ‘Y’
            paramObj.put("ERR_CD", "");                    // Err_CD : ‘’        -- 방화벽 차단일땐 공백, 에러코드 받았으면 에러코드 넣어 준다.
            paramObj.put("AMT", param.get("totalmoney"));  // Amt : 가격정보
            paramObj.put("SELLER_CD", "0");                // Seller_CD : ‘0’
            paramObj.put("USER_NAME", param.get("name"));  // UserName : 회원명
            paramObj.put("JUMIN_NO", param.get("userid")); // Jumin_NO : 앞 7자리
            paramObj.put("USER_ID", param.get("mem_id"));  // Userid : 회원번호
            paramObj.put("WORK_USER", workUser);           // Work_User : 작업자 아이디
            paramObj.put("ORD_TIME", ordTime);             // Ord_Time : 주문일시 (YYMMDD-HH24MISS)
            
            // 저장 (로그데이터 DB 저장) =======================================
             maccoMapper.insertOrdGuild(paramObj);
            
            rtnMap.put("rtnMessage", "주문은 정상적으로 되었으나 공제번호 발번은 실패 되었습니다.");
            
        } finally {
            if (in != null) { try { in.close(); } catch(Exception e) {} }
        }
        
        System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
    	System.out.println("공제신고 rtnMap 확인:"+rtnMap);
    	System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");        
        return rtnMap;
    }
}
