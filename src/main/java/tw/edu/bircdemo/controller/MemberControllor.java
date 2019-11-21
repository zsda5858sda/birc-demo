package tw.edu.bircdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.edu.bircdemo.bean.MemberBean;
import tw.edu.bircdemo.service.MemberService;

@Controller
@RequestMapping(path = "/member")
public class MemberControllor {
	private MemberService memberService;
	
	@Autowired
	public MemberControllor(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@ResponseBody
	@GetMapping(path = "/search")
	public String search() {
		System.out.println("get request");
		return "{\"result\": true}";
	}
	@ResponseBody
	@PostMapping(path="")
	public String create(@RequestBody MemberBean memberBean) {
		memberService.createAndReturn(memberBean);
		return "{\"result\": true}";
	}
}
