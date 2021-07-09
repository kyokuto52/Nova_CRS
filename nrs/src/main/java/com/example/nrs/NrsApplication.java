package com.example.nrs;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@SpringBootApplication
@RestController
public class NrsApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(NrsApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(NrsApplication.class);
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
		List<Event> bj = sm.getLocalEvent("北京");
		Collections.reverse(bj);
		mav.addObject("bj", bj);
		List<Event> sh = sm.getLocalEvent("上海");
		Collections.reverse(sh);
		mav.addObject("sh", sh);

		return mav;
	}

	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public ModelAndView info(ModelAndView mav, @RequestParam("id") String id, HttpSession session) {

		// 取出活动信息
		List<Event> info = sm.getEvent(id);
		mav.addObject("info", info.get(0));
		mav.setViewName("info");
		return mav;
	}

	@RequestMapping(value = "/reserve", method = RequestMethod.GET)
	public ModelAndView reserve(ModelAndView mav, @RequestParam("id") String id, HttpSession session) {

		// 是否已经预约过
		List<Users> us = sm.getUser(session.getAttribute("username").toString());
		String[] eventid = us.get(0).getEvents().split(",");
		List<String> list = Arrays.asList(eventid);
		boolean result = list.contains(id);
		if (result) {
			mav.addObject("rs", session.getAttribute("username") + "已经预约过此活动！");
			mav.setViewName("success");
		} else {
			// 记录预约ID
			sm.addEvnet(id, session.getAttribute("username").toString());
			mav.addObject("rs", session.getAttribute("username") + "成功预约！");
			mav.setViewName("success");
		}
		return mav;
	}

	@RequestMapping("/login")
	public ModelAndView login() {
		return new ModelAndView("login");
	}

	@RequestMapping("/redirect")
	public String redirect() {
		return "/";
	}

	@RequestMapping("/logout")
	public ModelAndView logout(ModelAndView mav, HttpSession session) {
		String un = (String) session.getAttribute("username");
		session.removeAttribute("username");
		mav.addObject("rs", un + "成功登出！");
		mav.setViewName("success");
		return mav;
	}

	@RequestMapping("/reservelist")
	public ModelAndView reservelist(ModelAndView mav, HttpSession session) {
		List<Users> users = sm.getUser(session.getAttribute("username").toString());
		String[] events = users.get(0).getEvents().split(",");
		List<Event> reservedevent = new ArrayList<Event>();
		for (String id : events) {
			reservedevent.add((Event) sm.getEvent(id).get(0));
		}
		Collections.reverse(reservedevent);
		mav.addObject("re", reservedevent);
		mav.setViewName("reservelist");
		return mav;
	}

	@RequestMapping("/mypage")
	public ModelAndView mypage(ModelAndView mav, HttpSession session) {

		// Mybaits接口读取数据库
		List<Users> rs = sm.getUser(session.getAttribute("username").toString());

		String sex = null;
		switch (rs.get(0).getSex()) {
		case 0:
			sex = "性别：未填写";
			break;
		case 1:
			sex = "性别：男性";
			break;
		case 2:
			sex = "性别：女性";
			break;
		case 3:
			sex = "性别：其他";
			break;
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
	public ModelAndView register(ModelAndView mav, HttpSession session, @RequestParam("username") String username,
			@RequestParam("password") String password, @RequestParam("sex") int sex,
			@RequestParam("birthday") Date birthday, @RequestParam("phone") String phone,
			@RequestParam("email") String email) {
		// 判断是否重名
		List<Users> rs = sm.getUser(username);
		if (rs.isEmpty()) {
			sm.newUser(username, password, birthday, sex, phone, email);
			session.setAttribute("username", rs.get(0).getUserName());
			mav.addObject("rs", session.getAttribute("username") + "注册成功");
			mav.setViewName("success");
		} else {
			// 登录失败
			mav.addObject("rs", "该用户名已经注册。");
			mav.setViewName("success");
		}
		return mav;
	}

	@RequestMapping(value = "/logind", method = RequestMethod.POST)
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
				mav.setViewName("success");
			} else {
				// 登录失败
				mav.addObject("rs", "密码错误");
				mav.setViewName("success");
			}
		} else {
			// 登录失败
			mav.addObject("rs", "未发现用户");
			mav.setViewName("success");
		}
		return mav;
	}
}
