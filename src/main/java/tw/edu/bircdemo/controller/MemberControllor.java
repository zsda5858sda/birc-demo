package tw.edu.bircdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
	@PostMapping(path="", produces = "application/json; charset=UTF-8")
	public String create(@RequestBody MemberBean memberBean) throws JsonProcessingException {
		/*
		*
		*
		*
		*
		*
		*
		*
		*
		*/
		memberBean = memberService.createAndReturn(memberBean);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode result = mapper.createObjectNode();
		result.put("result", true);
		result.put("errorCode", 200);
		result.put("message", "新增成功");
		ObjectNode dataNode = result.putObject("data");
		dataNode.put("id",memberBean.getId());
		dataNode.put("firstName",memberBean.getFirstName());
		dataNode.put("lastName",memberBean.getLastName());
		dataNode.put("gender",memberBean.getGender());
		dataNode.put("email",memberBean.getEmail());		
		return mapper.writeValueAsString(result);
	}
}
