package com.wow.api.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/")
public class homeMainController {
	
	@RequestMapping("/")
	public String layout() {
		System.out.println("index");
		return "index";	
	} 
	
	@RequestMapping("/home")
	public String home(Model model) {
		System.out.println("home");
		model.addAttribute("menu", "Home");
		return "index";	
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		System.out.println("about");
		// 기본 정보 조회 
		model.addAttribute("menu", "About");
		return "homepage/about";	
	}
	
	@RequestMapping("/service")
	public String service(Model model) {
		System.out.println("service");
		// 기본 정보 조회 
		model.addAttribute("menu", "Service");
		return "homepage/service";	
	}
}