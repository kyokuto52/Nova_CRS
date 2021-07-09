package com.example.nrs;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface Sqlmapper {
	
	//Users
	@Select("select * from users")
	List<Users> getAllUsers();
	
	@Select("select * from users where username = #{username}")
	List<Users> getUser(String username);
	
	@Insert("INSERT users values (0, #{un} ,#{pw}, #{sex}, #{dt}, 0, #{email}, #{phone}, null , null)")
	void newUser(String un, String pw, Date dt, int sex, String phone, String email);
	
	@Update("UPDATE users SET events = CONCAT(events,#{eventid},',') WHERE userName = #{userid}")
	void addEvnet(String eventid, String userid);
	
	//Events
	@Select("select * from events")
	List<Event> getAllEvent();
	
	@Select("select * from events where location = #{loc}")
	List<Event> getLocalEvent(String loc);
	
	@Select("select * from events where id = #{id}")
	List<Event> getEvent(String id);
	
	@Insert("INSERT events values (#{id}, #{title}, #{dt}, #{local}, #{point}, #{prof})")
	void newEvnet(int id, String title, Date dt, String local, int point, String prof);
	
	
}
