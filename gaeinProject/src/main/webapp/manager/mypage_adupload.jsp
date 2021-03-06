<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@	page import="java.io.*"%>
<%@	page import="java.util.*"%>
<%@ page import="java.sql.*" %>
<%@ page import="boardWeb.vo.*" %>
<%@ page import="boardWeb.util.*" %>
<%@ page import="com.oreilly.servlet.*"%>
<%@	page import="com.oreilly.servlet.multipart.*"%>
<%	
	Member loginUser = (Member)session.getAttribute("loginUser");
	String directory = request.getRealPath("/upload/");	
	//String directory = "C:\\Users\\MYCOM\\git\\MyProject1\\gaeinProject\\src\\main\\webapp\\ad";
	int maxSize = 1024 * 1024 * 100;	
	String encoding = "UTF-8";			
	MultipartRequest multipartRequest = new MultipartRequest(request, directory, maxSize, encoding, new DefaultFileRenamePolicy());
	
	String fileName = multipartRequest.getOriginalFileName("adimage");
	String fileRealName = multipartRequest.getFilesystemName("adimage");
	
	int point = Integer.parseInt(multipartRequest.getParameter("point"));
	String links = multipartRequest.getParameter("link");
	
	String sql = "";
	Connection conn = null;
	PreparedStatement psmt = null;
	ResultSet rs = null;
	
	try{
		
		conn = DBManager.getConnection();
		
		sql = "INSERT INTO assaad(midx, point, links, filerealname) ";
		sql +="VALUES (?,?,?,?)";
		psmt = conn.prepareStatement(sql);
		psmt.setInt(1, loginUser.getMidx());
		psmt.setInt(2, point);
		psmt.setString(3, links);
		psmt.setString(4, fileRealName);
		psmt.executeUpdate();
		
		sql = "UPDATE assamember SET point = point - " + point +  " WHERE midx = " + loginUser.getMidx();
		psmt = conn.prepareStatement(sql);
		psmt.executeUpdate();
		
		sql = "SELECT * FROM assamember WHERE midx = " + loginUser.getMidx();
		psmt = conn.prepareStatement(sql);
		rs = psmt.executeQuery();
		if(rs.next()){
			Member member = new Member();
			
			member.setMidx(rs.getInt("midx"));
			member.setPoint(rs.getInt("point"));
			member.setNickname(rs.getString("nickname"));
			member.setPosition(rs.getString("position"));
			
			session.setAttribute("loginUser", member);
		}
		
		response.sendRedirect(request.getContextPath() + "/manager/mypage.jsp");
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		DBManager.close(conn, psmt, rs);
	}
	
%>