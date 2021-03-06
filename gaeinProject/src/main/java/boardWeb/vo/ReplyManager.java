package boardWeb.vo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import boardWeb.util.DBManager;

public class ReplyManager {
	
	String sql;
	ResultSet rs = null;
	Connection conn = null;
	PreparedStatement psmt = null;
	public Reply reply = new Reply();
	
	public ReplyManager(int lidx, int bidx, int midx, String rcontent) {	// 댓글 작성
		try {
			conn = DBManager.getConnection();
			
			// 댓글을 등록하는 과정
			sql = "INSERT INTO ASSABOARDREPLY(ridx, lidx, bidx, midx, rcontent) VALUES(b_ridx_seq.nextval,?,?,?,?)";
			psmt = conn.prepareStatement(sql);
			psmt.setInt(1, lidx);
			psmt.setInt(2, bidx);
			psmt.setInt(3, midx);
			psmt.setString(4, rcontent);
			psmt.executeUpdate();
			
			// 댓글 등록시 포인트 1점
			sql = "UPDATE assamember SET point = point + 1 WHERE midx = " + midx;
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			
			// DB에 추가한 RIDX를 불러오는 과정
			psmt = null;
			sql = "SELECT max(ridx) AS ridx FROM ASSABOARDREPLY";
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			if(rs.next()){
				reply.setRidx(rs.getInt("ridx"));
			}
			rs = null;
			
			// 올린 댓글을 조회하는 과정
			sql = "SELECT a.midx, rcontent, TO_CHAR(rdate, 'YYYY-MM-DD HH24:MI:SS') as rdate, nickname, position FROM assaboardreply a, assamember b WHERE a.midx = b.midx AND ridx = " + reply.getRidx();
			psmt = conn.prepareStatement(sql);
			rs = psmt.executeQuery();
			if(rs.next()){
				
				reply.setMidx(rs.getInt("midx"));
				reply.setRdate(rs.getString("rdate"));
				reply.setRcontent(rs.getString("rcontent"));
				reply.setNickname(rs.getString("nickname"));
				reply.setPosition(rs.getString("position"));
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBManager.close(conn, psmt, rs);
		}
	}
	
	public ReplyManager(int ridx, String rcontent) {	// 댓글 수정
		try {
			
			conn = DBManager.getConnection();
			sql = "UPDATE ASSABOARDREPLY SET rcontent=?,MODIFYYN='Y' WHERE ridx = ?" ;
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, rcontent);
			psmt.setInt(2, ridx);
			psmt.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBManager.close(conn, psmt, rs);
		}
	}
	
	public ReplyManager(int ridx) {		// 댓글 삭제
		try {
			conn = DBManager.getConnection();
			sql = "UPDATE ASSABOARDREPLY SET delyn = 'Y' WHERE ridx = " + ridx;
			psmt = conn.prepareStatement(sql);
			psmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DBManager.close(conn, psmt);
		}
	}
}
