package edu.kh.jdbc.model.service;

import java.sql.Connection;
import java.sql.SQLException;

// import static 구문
// -> static이 붙은 필드, 메서드를 호출할 때
//		클래스명을 생략할 수 있게 해주는 구문
import static edu.kh.jdbc.common.JDBCTemplate.*;

import edu.kh.jdbc.model.dao.TestDAO;
import edu.kh.jdbc.model.vo.TestVO;

public class TestService {
	// Service : 비즈니스 로직(데이터 가공, 트랜잭션 제어) 처리
	// -> 실제 프로그램이 제공하는 기능을 모아놓은 클래스
	
	// 하나의 Service 메서드에서 n개의 DAO 메서드를 호출하여
	// 이를 하나의 트랜잭션 단위로 취급하여
	// 한번에 commit, rollback을 수행할 수 있다
	private TestDAO dao = new TestDAO();

	public int insert(TestVO vo1) throws SQLException{
		
		// 커넥션 생성
		Connection conn = getConnection();
		
		
		// DAO 메서드 호출하여 수행 후 결과 반환받기
		// -> Service에서 생성한 Connection 객체를 반드시 같이 전달
		int result = dao.insert(conn, vo1);
		
		// 트랜잭션 제어
		if(result > 0) commit(conn);
		else 		   rollback(conn);
		
		// 커넥션 반환
		close(conn);
		
		// 결과 반환
		return result;
		
	}
	
	
	// 3행 삽입 서비스
	public int insert(TestVO vo1, TestVO vo2, TestVO vo3) {
		
		// 1. Connection 생성(무조건 1번)
		Connection conn = getConnection();
		
		int result = 0; // insert 3회 모두 성공 시 1, 아니면 0
		
		try {
			int res1 = dao.insert(conn, vo1);
			int res2 = dao.insert(conn, vo2);
			int res3 = dao.insert(conn, vo3);
			
			if(res1 + res2 + res3 == 3) { // 모두 insert 성공한 경우
				
				commit(conn);
				result = 1;
			} else {
				rollback(conn);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result; // insert 3회 결과 반환
	}

}
