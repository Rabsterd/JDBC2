package edu.kh.jdbc.board.model.dao;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import static edu.kh.jdbc.common.JDBCTemplate.*;
import edu.kh.jdbc.board.model.dto.Board;

public class BoardDAO {
	
	// JDBC 객체 참조 변수
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	private Properties prop;
	
	public BoardDAO() {
		try {
			prop = new Properties();
			
			prop.loadFromXML(new FileInputStream("board-sql.xml"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 게시글 목록 조회 SQL 수행
	 * @param conn
	 * @return
	 */
	public List<Board> selectAllBoard(Connection conn) throws Exception {
		
		// 결과저장용 변수 선언 / 객체 생성
		List<Board> boardList = new ArrayList<Board>();
		
		try {
			
			String sql = prop.getProperty("selectAllBoard");
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				
				int boardNo = rs.getInt("BOARD_NO");
				String boardTitle = rs.getString("BOARD_TITLE");
				String boardName = rs.getString("MEMBER_NM");
				int readCount = rs.getInt("READ_COUNT");
				String createDate = rs.getString("CREATE_DT");
				int commentCount = rs.getInt("COMMENT_COUNT");
				
				Board board = new Board();
				
				board.setBoardNo(boardNo);
				board.setBoardTitle(boardTitle);
				board.setMemberName(createDate);
				board.setReadCount(readCount);
				board.setCreateDate(createDate);
				board.setCommentCount(commentCount);
				
				boardList.add(board); // list에 추가
			}
			
		} finally {
			// JDBC 객체 자원 반환
			close(rs);
			close(stmt);
			
		}
		
		return boardList;
	}

	/** 게시글 장세 조회 SQL 수행
	 * @param conn
	 * @param input
	 * @return
	 */
	public Board selectBoard(Connection conn, int input) throws Exception {
		
		Board board = null;
		
		try {
			String sql = prop.getProperty("selectBoard");
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, input);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				int boardNo = rs.getInt("BOARD_NO");
				String boardTitle = rs.getString("BOARD_TITLE");
				String memberName = rs.getString("MEMBER_NM");
				int reaCount = rs.getInt("READ_COUNT");
				String createDate = rs.getString("CREATE_DT");
				String boardContent = rs.getString("BOARD_CONTENT");
				int memberNo = rs.getInt("MEMBER_NO");
				
				board = new Board();
				
				board.setBoardNo(boardNo);
				board.setBoardTitle(boardTitle);
				board.setBoardContent(boardContent);
				board.setMemberName(memberName);
				board.setReadCount(reaCount);
				board.setCreateDate(createDate);
				board.setMemberNo(memberNo);
				
			}
			
		} finally {
			close(rs);
			close(pstmt);
		}

		return board;
	}

	/** 조회수 증가 SQL 수행
	 * @param conn
	 * @param input
	 * @return
	 */
	public int updateReadCount(Connection conn, int input) throws Exception {
		
		int result = 0;
		
		try {
			String sql = prop.getProperty("updateReadCount");
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, input);
			
			result = pstmt.executeUpdate();
			
			
		} finally {
			close(pstmt);
		}
				
		return result;
	}

	/** 게시글 수정 SQL 수행
	 * @param conn
	 * @param boardTitle
	 * @param boardContent
	 * @param boardNo
	 * @return
	 */
	public int updateBoard(Connection conn, String boardTitle, String boardContent, int boardNo) throws Exception {

		int result = 0;
		
		try {
			String sql = prop.getProperty("updateBoard");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, boardTitle);
			pstmt.setString(2, boardContent);
			pstmt.setInt(3, boardNo);
			
			result = pstmt.executeUpdate();
			
		} finally {
			close(pstmt);
		}
		
		return result;
	}

	/** 게시글 삭제 SQL 수행
	 * @param conn
	 * @param boardNo
	 * @return
	 */
	public int deleteBoard(Connection conn, int boardNo) throws Exception {
		int result = 0;
		
		try {
			String sql = prop.getProperty("deleteBoard");
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, boardNo);
			
			result = pstmt.executeUpdate();
			
		} finally {
			close(pstmt);
		}
		
		
		return result;
	}

	/** 다음 게시글 번호 조회 SQL 수행
	 * @param conn
	 * @return
	 */
	public int nextBoardNo(Connection conn) throws Exception {

		int boardNo = 0;
		
		try {
			String sql = prop.getProperty("nextBoardNo");
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				boardNo = rs.getInt(1); //4
			}
			
		} finally {
			close(rs);
			close(stmt);
		}
		
		return boardNo;
	}

	/** 게시글 삽입 SQL 수행
	 * @param conn
	 * @param boardTitle
	 * @param boardContent
	 * @param memberNo
	 * @param boardNo
	 * @return
	 */
	public int insertBoard(Connection conn, String boardTitle, String boardContent, int memberNo, int boardNo) throws Exception {
		
		int result = 0;
		
		try {
			String sql = prop.getProperty("insertBoard");
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, boardNo);
			pstmt.setString(2, boardTitle);
			pstmt.setString(3, boardContent);
			pstmt.setInt(4, memberNo);
			
			result = pstmt.executeUpdate();
			
			
		} finally {
			close(pstmt);
		}
		
		return result;
	}


	/** 게시글 검색 DAO
	 * @param conn
	 * @param condition
	 * @param query
	 * @return boardList
	 * @throws Exception
	 */
	public List<Board> searchBoard(Connection conn, int condition, String query) throws Exception{
		
		List<Board> boardList = new ArrayList<>();
		
		try {
			String sql = prop.getProperty("searchBoard1")
					   + prop.getProperty("searchBoard2_" + condition)
					   + prop.getProperty("searchBoard3");
					
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, query);
			
			// 3번(제목+내용)은 ?가 2개 존재하기 때문에 추가 세팅 구문 작성
			if(condition == 3)   pstmt.setString(2, query);
			
			rs = pstmt.executeQuery();
			
			// ResultSet에 저장된 값을 List 옮겨 담기
			while(rs.next()) {
				
				int boardNo = rs.getInt("BOARD_NO");
				String boardTitle = rs.getString("BOARD_TITLE");
				String memberName = rs.getString("MEMBER_NM");
				int readCount = rs.getInt("READ_COUNT");
				String createDate = rs.getString("CREATE_DT");
				int commentCount = rs.getInt("COMMENT_COUNT");
				
				Board board = new Board();
				board.setBoardNo(boardNo);
				board.setBoardTitle(boardTitle);
				board.setMemberName(memberName);
				board.setReadCount(readCount);
				board.setCreateDate(createDate);
				board.setCommentCount(commentCount);
				
				boardList.add(board);
			}
			
			
		}finally {
			close(rs);
			close(pstmt);
		}
		
		return boardList;
	}
	


}
