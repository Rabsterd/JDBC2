package edu.kh.jdbc.member.model.dao;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import static edu.kh.jdbc.common.JDBCTemplate.*;

import edu.kh.jdbc.member.model.dto.Member;

public class MemberDAO {
	
	// JDBC 객체 참조 변수
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	// xml에 작성된 SQL을 읽어와 저장할 객체 참조변수
	private Properties prop;
	
	// 기본생성자로 객체 생성 시 XML 읽어오기
	public MemberDAO() {
		try {
			prop = new Properties();
			
			prop.loadFromXML(new FileInputStream("member-sql.xml"));
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	/** 회원 목록 조회 SQL 수행
	 * @param conn
	 * @return
	 */
	public List<Member> selectMemberList(Connection conn) throws Exception {
		
		// 결과 저장용 변수 선언
		List<Member> memberList = new ArrayList<Member>();
		
		try {
			
			String sql = prop.getProperty("selectMemberList");
			
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				String memberId = rs.getString("MEMBER_ID");
				String memberName = rs.getString("MEMBER_NM");
				String memberGender = rs.getString("성별");
				
				Member member = new Member();
				member.setMemberId(memberId);
				member.setMemberName(memberName);
				member.setMemberGender(memberGender);
				
				memberList.add(member);
				
			}
			
		} finally {
			close(rs);
			close(stmt);
		}
		
		return memberList;
	}


	/** 내 정보 수정 DAO
	 * @param conn
	 * @param memberName
	 * @param memberGender
	 * @param memberNo
	 * @return
	 */
	public int updateMember(Connection conn, String memberName, String memberGender, int memberNo) 
							throws Exception {
		
		int result = 0;
		
		try {
			String sql = prop.getProperty("updateMember");
			
			System.out.println(sql);
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, memberName);
			pstmt.setString(2, memberGender);
			pstmt.setInt(3, memberNo);
			
			result = pstmt.executeUpdate();
			
			System.out.println(result);
			
		} finally {
			close(pstmt);
		}

		
		return result;
	}

}
