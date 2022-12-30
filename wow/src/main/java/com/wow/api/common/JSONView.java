package com.wow.api.common;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;
import org.springframework.web.servlet.view.AbstractView;



public class JSONView  extends AbstractView{
	//protected Log log = LogFactory.getLog(this.getClass());
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/plain;charset=UTF-8");
			
		String json = JSONValue.toJSONString(model);
		//log.debug("###:" + json);
		
		PrintWriter out = response.getWriter();						
      
		out.write(json);
		out.flush();
		out.close();
	}

}
