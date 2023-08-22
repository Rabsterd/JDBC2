package edu.kh.jdbc.board.view;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import edu.kh.jdbc.board.model.dto.Board;
import edu.kh.jdbc.board.model.service.BoardService;
import edu.kh.jdbc.common.Session;


public class BoardView {
	private Scanner sc = new Scanner(System.in);
	private BoardService boardService = new BoardService();
	
	public void boardMenu() {
		
		int input = 0;
		
		do {
			try {
				System.out.println("\n===== 게시판 기능 =====\n");
				System.out.println("1. 게시글 목록 조회");
				System.out.println("2. 게시글 상세 조회(+ 댓글 기능)");
				
				System.out.println("3. 게시글 작성");
				// 제목, 내용(StringBuffer 이용) 입력
				// -> 게시글 삽입 서비스(제목, 내용, 로그인 회원 번호) 호출
				
				System.out.println("4. 게시글 검색");
				System.out.println("9. 메인 메뉴로 돌아가기");
				System.out.println("0. 프로그램 종료");
				
				System.out.print("\n메뉴 선택 : ");
				input = sc.nextInt();
				sc.nextLine(); 
				
				System.out.println();
				
				switch(input) {
				case 1: selectAllBoard();  break; // 게시글 목록 조회
				case 2: selectBoard(); break; // 게시글 상세 조회
				
				case 3: insertBoard(); break; // 게시글 등록(삽입)
				
				case 4: searchBoard(); break; // 게시글 검색
				
				case 9: 
					System.out.println("\n===== 메인 메뉴로 돌아갑니다 =====\n");
					break;
				
				case 0: 
					System.out.println("\n=== 프로그램 종료 ===\n"); 
					System.exit(0);
				default: System.out.println("\n*** 메뉴 번호만 입력 해주세요 ***\n");  
				}
				
				System.out.println();
				
			}catch (InputMismatchException e) {
				System.out.println("\n*** 입력 형식이 올바르지 않습니다***\n");
				sc.nextLine(); // 입력버퍼에 잘못된 문자열 제거
				input = -1; // while문 종료 방지
			}
			
		}while(input != 9);
		
	}

	/** 게시글 등록
	 * 
	 */
	private void insertBoard() {
		System.out.println("\n=== 게시글 등록 ===\n");
		
		System.out.print("제목 입력 : ");
		String boardTitle = sc.nextLine();
		
		StringBuffer sb = new StringBuffer();
		
		System.out.println("<!wq 입력 시 종료>");
		// !wq 특정 단어가 입력될 때까지 무한히 입력
		
		while(true) {
			String str = sc.nextLine();
			
			
			if(str.equals("!wq")) break;
			
			sb.append(str);
			sb.append("\n");
		}
		
		try {
			// 게시글 삽입 서비스 호출
			int result = boardService.insertBoard(boardTitle, sb.toString(), 
													Session.loginMember.getMemberNo());
			
			if(result > 0) { // 성공 시
				System.out.println("\n== 등록되었습니다 ==\n");
				
				// 현재 등록된 게시글 상세 조회 서비스 호출
				// -> 게시글 번호, 로그인 회원번호
				Board board = boardService.selectBoard(result, Session.loginMember.getMemberNo());
				
				System.out.println("--------------------------------------------------------");
				System.out.printf("글번호 : %d \n제목 : %s\n", board.getBoardNo(), board.getBoardTitle());
				System.out.printf("작성자 : %s | 작성일 : %s  \n조회수 : %d\n", 
						board.getMemberName(), board.getCreateDate(), board.getReadCount());
				System.out.println("--------------------------------------------------------\n");
				System.out.println(board.getBoardContent());
				System.out.println("\n--------------------------------------------------------");
				
			} else {
				System.out.println("\n== 등록 실패 ==\n");
			}
			
		} catch(Exception e) {
			System.out.println("\n=== 게시글 등록 중 예외 발생 ===\n");
			e.printStackTrace();
		}
		
	}

	/** 게시글 상세 조회
	 * 
	 */
	private void selectBoard() {
		System.out.println("\n=== 게시글 상세 조회===\n");
		// 게시글 번호 입력
		// 1) 번호가 일치하는 게시글이 있으면 조회
		//		-> 조회수 증가(단, 자신이 작성한 게시글일 경우 조회수 증가 X)
		//		-> 자신이 작성한 게시글일 경우 수정/ 삭제 기능 노출
		//
		
		// 2) 번호가 일치하는 게시글이 없으면
		//	-> 해당 게시글이 존재하지 않습니다
		
		System.out.print("게시글 번호 입력 : ");
		int input = sc.nextInt();
		sc.nextLine();
		
		try {
			// 게시글 상세 조회 서비스 호출
			Board board = boardService.selectBoard(input, Session.loginMember.getMemberNo());
												// 입력받은 번호, 로그인한 회원 번호(조회수 증가에 사용)
			
			if(board == null) {
				System.out.println("\n== 해당 게시글이 존재하지 않습니다 ==\n");
				return;
			}
			
			System.out.println("--------------------------------------------------------");
			System.out.printf("글번호 : %d \n제목 : %s\n", board.getBoardNo(), board.getBoardTitle());
			System.out.printf("작성자 : %s | 작성일 : %s  \n조회수 : %d\n", 
					board.getMemberName(), board.getCreateDate(), board.getReadCount());
			System.out.println("--------------------------------------------------------\n");
			System.out.println(board.getBoardContent());
			System.out.println("\n--------------------------------------------------------");
			
			// 로그인한 회원이 작성한 게시글일 경우 
			// 수정 / 삭제 기능 노출
			if( Session.loginMember.getMemberNo() == board.getMemberNo() ) {
				while(true) {
					System.out.println("1) 수정");
					System.out.println("2) 삭제");
					System.out.println("0) 게시판 메뉴로 돌아가기");
					
					System.out.print("선택 >> ");
					
					input = sc.nextInt();
					sc.nextLine();
					
					// 기능 수행 후 게시판 메뉴로 돌아가기
					switch(input) {
					case 1 : updateBoard(board.getBoardNo()); return;
							// 게시글 번호를 매개변수로 전달
					
					case 2 : deleteBoard(board.getBoardNo()); return;
							// 게시글 번호를 매개변수로 전달
					
					case 0 : return;
					default : System.out.println("\n*** 잘못 입력 하셨습니다 ***\n");
					}
				}


			}
			
		} catch(Exception e) {
			System.out.println("\n=== 게시글 상세 조회 중 예외 발생 ===\n");
			e.printStackTrace();
		}
		
	}

	/** 게시글 삭제
	 * @param boardNo
	 */
	private void deleteBoard(int boardNo) {
		System.out.println("\n=== 게시글 삭제 ===\n");
		
		System.out.print("정말 삭제하시겠습니까(Y/N) : ");
		char check = sc.next().toUpperCase().charAt(0);
	while(true) {	
			if(check == 'N') {
				System.out.println("삭제 취소");
				return;
			}
			
			if(check != 'Y') {
				System.out.println("잘못 입력하셨습니다");
				continue;
			}
			
			break; // check == 'Y' 인 경우
		}
	try {
		int result = boardService.deleteBoard(boardNo);
		
		if(result > 0) {
			System.out.println("\n== 게시글이 삭제되었습니다 ==\n");
		} else {
			System.out.println("\n== 삭제 실패 ==\n");
		}
	} catch(Exception e) {
		System.out.println("\n=== 게시글 삭제 중 예외 발생===\n");
		e.printStackTrace();
	}
		
	}
	
	

	/** 게시글 수정
	 * @param boardNo
	 */
	private void updateBoard(int boardNo) {
		System.out.println("\n=== 게시글 수정 ===\n");
		
		System.out.print("수정할 제목 : ");
		String boardTitle = sc.nextLine();
		
		StringBuffer sb = new StringBuffer();
		
		System.out.println("<!wq 입력 시 종료>");
		// !wq 특정 단어가 입력될 때까지 무한히 입력
		
		while(true) {
			String str = sc.nextLine();
			
			
			if(str.equals("!wq")) break;
			
			sb.append(str);
			sb.append("\n");
		}
		
		try {
			// 게시글 수정 서비스 호출
			int result = boardService.updateBoard(boardTitle, sb.toString(), boardNo);
			
			if(result > 0) {
				System.out.println("\n== 게시글이 수정되었습니다 ==\n");
			} else {
				System.out.println("\n== 수정 실패 ==\n");
			}
		}catch(Exception e) {
			System.out.println("\n=== 게시글 수정 중 예외 발생===\n");
			e.printStackTrace();
		}
	}

	/** 게시글 목록 조회
	 * 
	 */
	private void selectAllBoard() {
		System.out.println("\n=== 게시글 목록 조회===\n");
		
		try {
			// 게시글 목록 조회 서비스 호출
			List<Board> boardList = boardService.selectAllBoard();
			
			// 게시글이 없는 경우
			if(boardList.isEmpty()) {
				System.out.println("\n=== 게시글이 존재하지 않습니다 ===\n");
				return;
			}
			
			for(Board b : boardList) {
				// 3 | 샘플 제목[2] | 유저일 | 2023-03-24 | 0
				System.out.printf("%d | %s[%d] | %s | %s | %d \n",
						b.getBoardNo(),
						b.getBoardTitle(),
						b.getCommentCount(),
						b.getMemberName(),
						b.getCreateDate(),
						b.getReadCount()
						);
			}
			
		} catch(Exception e) {
			System.out.println("\n=== 게시글 목록 조회 중 예외 발생 ===\n");
			e.printStackTrace();
		}
	}
	
	/**
	 * 게시글 검색
	 */
	private void searchBoard() {
		try {
			System.out.println("\n[게시글 검색]\n");
			
			System.out.println("1) 제목");
			System.out.println("2) 내용");
			System.out.println("3) 제목 + 내용");
			System.out.println("4) 작성자");
			System.out.print("검색 조건 선택 : ");
			
			int condition = sc.nextInt();
			sc.nextLine();
			
			if(condition >= 1 && condition <= 4) { // 정상 입력
				
				System.out.print("검색어 입력 : ");
				String query = sc.nextLine();
				
				// 검색 서비스 호출 후 결과 반환 받기
				List<Board> boardList = boardService.searchBoard(condition, query);
						
				if(boardList.isEmpty()) { // 조회 결과가 없을 경우
					System.out.println("\n[검색 결과가 없습니다.]\n");
					
				}else {
					
					for(Board b : boardList) {
						// 3 | 샘플 제목3[4] | 유저삼 | 3시간 전 | 10
						System.out.printf("%d | %s[%d] | %s | %s | %d\n",
								b.getBoardNo(), 
								b.getBoardTitle(), 
								b.getCommentCount(),
								b.getMemberName(),
								b.getCreateDate(),
								b.getReadCount());
					}
				}
				
				
				
			} else { // 비정상 입력
				System.out.println("\n[1~4번 사이의 숫자를 입력해주세요]\n");
			}
			
			
		}catch (Exception e) {
			System.out.println("\n<<게시글 검색 중 예외 발생>>\n");
			e.printStackTrace();
		}
	}
	


}


