package com.stevelee.steve_user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestTestController {
	
	@Autowired
	private SqlSession sqlSession;
	
	@RequestMapping("/useralllist")
	public List  useralllist(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// jsonString Object의 리스트 형식으로 리턴
		System.out.println("steve_user request list로 리턴시 맵리스트 형식으로 리턴 [{}]");
		
		List<Map<String, Object>> userlist = sqlSession.selectList("usermapper.useralllist");
		
		System.out.println("userlist : " + userlist.toString());
		
		return userlist;
	}
	
	@RequestMapping("/useralllistjson")
	public JSONObject  useralllistjson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// json으로 리턴되나, userList라는 키 안에 전부 각 json형식이 들어가서 포함
		System.out.println("steve_user useralllistjson // json으로 리턴된다. ");
		
		List<Map<String, Object>> userlist = sqlSession.selectList("usermapper.useralllist");
		
		System.out.println("userlist : " + userlist.toString());
		
		JSONObject obj = new JSONObject();
		
		obj.put("userList", userlist);
		
		
		
		return obj;
	}
	
	
	// ex url) http://localhost:8080/userselectoneGet?mail_id=abc@gmail.com
	@RequestMapping("/userselectoneGet")
	public String userselectoneGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// get방식으로 받고, String 형식으로 리턴한다
		String id = request.getParameter("mail_id");
		
		System.out.println("id : " + id);
		
		Map<String, Object> userone = sqlSession.selectOne("usermapper.userselectoneGet", id);
		
		return userone.toString();
	}
	
	// http://localhost:8080//userselectonegetjson?mail_id=abc@gmail.com
	@RequestMapping("/userselectonegetjson")
	public JSONObject userselectonegetjson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// get방식으로 데이터를 받고 JSONObject로 리턴한다.
		String id = request.getParameter("mail_id");
		
		Map<String, Object> pw = sqlSession.selectOne("usermapper.userselecttoonegetjson", id);
		
		JSONObject obj = new JSONObject();
		obj.put("pw", pw);
		
		return obj;
	}
	
	@RequestMapping("/userselectonePost")
	public String userselectonePost(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> posttext) throws Exception {
		// post방식은 requestbody에서 받아온다 postman 에서 json 형식이 map이기 떄문에 받는것도 map으로 보낸다.
		
		Map<String, Object> userone = sqlSession.selectOne("usermapper.userselectonePost", posttext);
		
		System.out.println("userone : " + userone);
		
		return userone.toString();
	}
	
	// http://localhost:8080/userchk?mail_id=abc@gmail.com&pw=abc
	@RequestMapping("/userchk")
	public Map userchk(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// get 방식으로 받을시, login 체크로직
		
		// return 값으로 넘겨줄 map 변수 만들기
		Map<String, Object> result = new HashMap<String, Object>();
		// userchk 시 사용될 dto만들기
		userDto dto = new userDto();
		
		// request에서 오는 값을 변수에 저장
		String id = request.getParameter("mail_id");
		String pw = request.getParameter("pw");
		
		// dto에 변수 저장
		dto.setMail_id(id);
		dto.setPw(pw);
		
		// 쿼리 실행시 exception 발생을 대처하기 위해 try-catch문 생성
		try {
			// 변수 null 체크 순서대로 안하면 안됨 자바는 컴파일을 위에서 아래로 하기 떄문에
			// 변수 둘다 없는지, 둘 중 하나가 있는지, 확인
			if (dto.getMail_id().equals("") && dto.getPw().equals("")) {
				result.put("result - id, pw 둘다 입력하지 않았음", 13);
				return result;
			} 
			
			if ("".equals(dto.getMail_id())) {
				result.put("result - id 입력해주라", 11);
				return result;
			} else if  (dto.getPw().equals("")) {
				result.put("result -  pw 입력해 주라", 12);	
				return result;
			}
			
			// 1. id 가 있는지 체크 있다면, id가 존재하는지 체크
			if (dto.getMail_id() != null) {
				// 1-1. 쿼리 호출하여 값 을 불러온다.
				Map<Object, Object> idCheck = sqlSession.selectOne("usermapper.idCheck", dto.getMail_id()); // ID
				// 2. id가 없다면, result에 값을 넣어주고 return
				if (idCheck == null) {
					result.put("result- id가 없다", 0);
				}
				// 3. id가 있다면, pw도 체크 
				if(!idCheck.isEmpty()) { // id
					
					// 3-1. id가 있다면 request의 pw값과, idCheck 쿼리에서 가져온 pw값을 비교하자.
					// 3-2. pw가 맞지 않는다면 result에 해당 값을 설정해서 return 
					if(!idCheck.get("pw").equals(pw)) { // id Ok pw Not Ok
						
						result.put("result - id는 맞으나, pw가 안맞는다.", 1);
					} else if (idCheck.get("pw").equals(pw)) {
						
						// 4. 둘다 맞다면 둘다 체크하고  result에 다 맞다. 라는 값을 입력해주고 return
						Map<Object, Object> loginCheck = sqlSession.selectOne("usermapper.idpwSelect", dto); // loginOk
						if(!loginCheck.isEmpty()) { // loginOk
							result.put("result - id, pw 다맞음", 2);
						}
					}
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
		return result;
	}
	
	/**
	 * post 방식으로 받은 데이터 기준으로 받아서, 입력값 출력
	 * @param requeset Postman parameter 
	  		  {
				"mail_id" : "abc@gmail.com",
				"pw" : "abc"
			  }
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/userchkPost")
	public String userchkPost(HttpServletRequest request, HttpServletResponse response, @RequestBody userDto param) throws Exception {
		// Post 데이터 받을시 체크 login 체크 로직
		
		Map<String, Object> selectList = null;
		String result = "";
		
		if ("".equals(param.getMail_id())) {
			result = "11 - id 미입력";
		} else if ("".equals(param.getPw())) {
			result = "12 - pw 미입력";
		} else {
			int countResult = sqlSession.selectOne("usermapper.getUserCount", param);

			if (countResult == 0) {
				result = "0 - id 없음";
			} else {
				selectList = sqlSession.selectOne("usermapper.getUser", param);
				
				String mail_id = (String) selectList.get("mail_id");
				String pw = (String) selectList.get("pw");
				
				if (param.getMail_id().equals(mail_id) && param.getPw().equals(pw)) {
					result = "2 - id, pw 모두 맞음";
				} else if (param.getMail_id().equals(mail_id)) {
					result = "1 - id 있고 pw 틀림";
				}
			}
		}

		return result;
	}
}
