package com.example.nrs;

import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@SpringBootApplication
@RestController
public class NrsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NrsApplication.class, args);
	}

	@Autowired
	Sqlmapper sm;

	// Mapping
	@RequestMapping("/")
	public ModelAndView entrance(ModelAndView mav, HttpSession session) {
		// 判断是否已经登录
		if (session.getAttribute("username") == null) {
			mav.setViewName("index");
		} else {
			mav.setViewName("index-user");
			mav.addObject("rs", session.getAttribute("username") + "你好！");
		}
		// 取出活动信息
		List <Event> le = sm.getAllEvent();
		mav.addObject("le", le);
		
		return mav;
	}

	@RequestMapping("/login")
	public ModelAndView login() {
		return new ModelAndView("login");
	}
	
	@RequestMapping("/reslist")
	public ModelAndView reslist() {
		return new ModelAndView("reslist");
	}
	
	@RequestMapping("/mypage")
	public ModelAndView mypage(ModelAndView mav, HttpSession session) {

		// Mybaits接口读取数据库
		List<Users> rs = sm.getUser(session.getAttribute("username").toString());	
		
		String sex = null;
		switch(rs.get(0).getSex()) {
		case 0: sex = "性别：未填写"; break;
		case 1: sex = "性别：男性"; break;
		case 2: sex = "性别：女性"; break;
		case 3: sex = "性别：其他"; break;
		}
		
		mav.addObject("id", "用户ID:" + rs.get(0).getId() + "　");
		mav.addObject("un", rs.get(0).getUserName());
		mav.addObject("sex", sex);
		mav.addObject("prof", rs.get(0).getProfile());
		mav.setViewName("mypage");
		return mav;
	}
	
	@RequestMapping("/register")
	public ModelAndView register() {
		return new ModelAndView("register");
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView register(ModelAndView mav, HttpSession session,
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("sex") int sex,
			@RequestParam("birthday") Date birthday,
			@RequestParam("phone") String phone,
			@RequestParam("email") String email
			) {
		//判断是否重名
		List<Users> rs = sm.getUser(username);
		if (rs.isEmpty()) {
			sm.newUser(username, password, birthday, sex, phone, email);
			session.setAttribute("username", rs.get(0).getUserName());
			mav.addObject("rs", session.getAttribute("username") + "注册成功");
			mav.setViewName("success");
		}else {
			// 登录失败
			mav.addObject("rs", "该用户名已经注册。");
			mav.setViewName("fail");
		}		
		return mav;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(ModelAndView mav, @RequestParam("username") String username,
			@RequestParam("password") String password, HttpSession session) {

		// Mybaits接口读取数据库
		List<Users> rs = sm.getUser(username);

		// 验证密码
		if (!rs.isEmpty()) {
			if (rs.get(0).getPassword().equals(password)) {
				// 登录成功
				// 记住用户名
				session.setAttribute("username", rs.get(0).getUserName());
				// 指向成功页面
				mav.addObject("rs", session.getAttribute("username") + "成功登录！");
				mav.setViewName("index-user");
			}else {
				// 登录失败
				mav.addObject("rs", "密码错误");
				mav.setViewName("fail");
			}
		}else {
			// 登录失败
			mav.addObject("rs", "未发现用户");
			mav.setViewName("fail");
		}

		return mav;
	}

}
