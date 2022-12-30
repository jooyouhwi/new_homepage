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
		model.addAttribute("menu", "About");
		return "homepage/about";	
	}
	
	@RequestMapping("/service")
	public String service(Model model) {
		System.out.println("service"); 
		model.addAttribute("menu", "Service");
		return "homepage/service";	
	}
	
	@RequestMapping("/contact")
	public String contact(Model model) {
		System.out.println("contact"); 
		model.addAttribute("menu", "Contact");
		return "homepage/contact";	
	}
	
	@RequestMapping("/blog")
	public String blog(Model model) {
		System.out.println("blog"); 
		model.addAttribute("menu", "Blog");
		return "homepage/blog";	
	}
	
	@RequestMapping("/blog_detail")
	public String blog_detail(Model model) {
		System.out.println("blog"); 
		model.addAttribute("menu", "Blog Detail");
		return "homepage/blog_detail";	
	}
	
	
	
}