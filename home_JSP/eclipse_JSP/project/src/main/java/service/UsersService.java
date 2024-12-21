package service;

import config.DBManager;
import dto.UsersDTO;
import mapper.UsersMapper;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.mindrot.jbcrypt.BCrypt;

/**
 * UsersService 클래스는 사용자 관련 비즈니스 로직을 처리하는 서비스 계층입니다.
 */
public class UsersService {

	// 싱글톤 패턴: 인스턴스 저장 변수
	private static UsersService instance = new UsersService();

	// UsersMapper 객체 (MyBatis 매퍼 인터페이스)
	private UsersMapper mapper;

	/**
	 * 기본 생성자: DBManager를 통해 UsersMapper를 초기화합니다.
	 */
	private UsersService() {
		System.out.println("[UsersService] 생성자 호출 -> UsersMapper 초기화");
		mapper = DBManager.getInstance().getSession().getMapper(UsersMapper.class);
		System.out.println("[UsersService] UsersMapper 객체 생성 완료");
	}

	/**
	 * UsersService의 싱글톤 인스턴스를 반환합니다.
	 * 
	 * @return UsersService 인스턴스
	 */
	public static UsersService getInstance() {
		System.out.println("[UsersService] getInstance() 호출 -> 싱글톤 인스턴스 반환");
		return instance;
	}

	// --------------------------------------------------------------------------------------------------
	// 로그인 처리
	public UsersDTO login(String loginId, String password) {
		System.out.println("[UsersService] login() 호출 -> loginId: " + loginId);

		try {
			// 1. loginId로 사용자 정보 조회
			UsersDTO tempUser = mapper.findUserByLoginId(loginId);

			if (tempUser == null) {
				System.out.println("[UsersService] 사용자 정보 조회 실패 -> 사용자 없음");
				return null;
			}

			// 2. 비밀번호 검증
			System.out.println("[UsersService] 조회된 사용자 비밀번호 검증 시작");
			if (BCrypt.checkpw(password, tempUser.getPassword())) {
				System.out.println("[UsersService] BCrypt.checkpw 비밀번호 검증 성공");

				// 3. userNumber로 사용자 정보 재조회 (최신 상태 가져오기)
				System.out.println("[UsersService] 사용자 정보 재조회 시작 -> userNumber: " + tempUser.getUserNumber());
				System.out.println("[UsersService] 사용자 정보 재조회 userNumber 외 : " + tempUser.getUserName() + " / "
						+ tempUser.getNickName() + " / " + tempUser.getUserEmail());
				UsersDTO user = mapper.findUserByUserNumber(tempUser.getUserNumber());

				if (user != null) {
					System.out.println("[UsersService] 최종 사용자 정보 조회 성공 -> " + "userNumber: " + user.getUserNumber()
							+ ", loginId: " + user.getLoginId() + ", nickName: " + user.getNickName() + ", userEmail: "
							+ user.getUserEmail());
					return user;
				} else {
					System.out.println("[UsersService] 최종 사용자 정보 조회 실패");
					return null;
				}
			} else {
				System.out.println("[UsersService] 비밀번호 검증 실패");
				return null;
			}
		} catch (Exception e) {
			System.out.println("[UsersService] 로그인 처리 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	// --------------------------------------------------------------------------------------------------
	// 회원가입 처리
	public String hashPassword(String password) {
		System.out.println("[UsersService] hashPassword() 호출 -> 비밀번호 해싱 시작");
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	public int insertMember(UsersDTO dto) {
		System.out.println("[UsersService] insertMember() 호출 -> 회원가입 시작");
		return mapper.insertMember(dto);
	}

	// --------------------------------------------------------------------------------------------------
	// 중복 확인 (아이디, 이메일, 닉네임)
	public boolean isLoginIdExists(String loginId) {
		System.out.println("[UsersService] isLoginIdExists() 호출 -> 아이디 중복 확인");
		return mapper.selectLoginIdCount(loginId) > 0;
	}

	public boolean isEmailExists(String email) {
		System.out.println("[UsersService] isEmailExists() 호출 -> 이메일 중복 확인");
		return mapper.selectEmailCount(email) > 0;
	}

	public boolean isNickNameExists(String nickName) {
		System.out.println("[UsersService] isNickNameExists() 호출 -> 닉네임 중복 확인");
		return mapper.selectNickNameCount(nickName) > 0;
	}

	// --------------------------------------------------------------------------------------------------
	// 사용자 번호로 사용자 정보 조회
	public UsersDTO findUserByUserNumber(int userNumber) {
		System.out.println("[UsersService] findUserByUserNumber() 호출 -> 사용자 번호: " + userNumber);

		try {
			UsersDTO user = mapper.findUserByUserNumber(userNumber);
			if (user != null) {
				System.out.println("[UsersService] 사용자 조회 성공 -> " + "이름: " + user.getUserName() + ", 닉네임: "
						+ user.getNickName() + ", 이메일: " + user.getUserEmail());
				return user;
			} else {
				System.out.println("[UsersService] 사용자 조회 실패 -> 데이터 없음");
				return null;
			}
		} catch (Exception e) {
			System.out.println("[UsersService] 사용자 정보 조회 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	// --------------------------------------------------------------------------------------------------
	// 사용자 정보 업데이트 메서드
	public boolean updateUser(UsersDTO user) {
		System.out.println("[UsersService] updateUser() 호출 -> 사용자 정보 수정 시작");

		try {
			int result = mapper.updateUser(user);
			if (result > 0) {
				System.out.println("[UsersService] 사용자 정보 업데이트 성공");
				return true;
			} else {
				System.out.println("[UsersService] 사용자 정보 업데이트 실패");
				return false;
			}
		} catch (Exception e) {
			System.out.println("[UsersService] 사용자 정보 업데이트 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	// -------------------------------------------------------------------------------------------------
	// 닉네임 변경?
	public boolean updateNickName(int userNumber, String newNickName) {
		System.out.println("[UsersService] updateNickName() 호출");

		try {
			if (isNickNameExists(newNickName)) {
				System.out.println("[UsersService] 닉네임 중복 -> 업데이트 불가");
				return false;
			}

			UsersDTO user = mapper.findUserByUserNumber(userNumber);
			Timestamp lastUpdate = user.getUpdateTime();

			// 1주일 제한 체크
			if (lastUpdate != null && System.currentTimeMillis() - lastUpdate.getTime() < 7 * 24 * 60 * 60 * 1000) {
				System.out.println("[UsersService] 닉네임 변경 제한 -> 1주일 미경과");
				return false;
			}

			user.setNickName(newNickName);
			user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			return mapper.updateUser(user) > 0;
		} catch (Exception e) {
			System.out.println("[UsersService] 닉네임 변경 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	// -------------------------------------------------------------------------------------------------
	// 이메일 변경?
	public boolean updateEmail(int userNumber, String newEmail) {
		System.out.println("[UsersService] updateEmail() 호출");

		try {
			if (isEmailExists(newEmail)) {
				System.out.println("[UsersService] 이메일 중복 -> 업데이트 불가");
				return false;
			}

			UsersDTO user = mapper.findUserByUserNumber(userNumber);
			user.setUserEmail(newEmail);
			return mapper.updateUser(user) > 0;
		} catch (Exception e) {
			System.out.println("[UsersService] 이메일 변경 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	// -------------------------------------------------------------------------------------------------
	// 비밀번호 변경
	public boolean updatePassword(int userNumber, String currentPassword, String newPassword) {
	    System.out.println("[UsersService] updatePassword() 호출");

	    try {
	        UsersDTO user = mapper.findUserByUserNumber(userNumber);

	        // 현재 비밀번호 검증
	        if (!BCrypt.checkpw(currentPassword, user.getPassword())) {
	            System.out.println("[UsersService] 현재 비밀번호 불일치");
	            return false;
	        }

	        // 비밀번호 업데이트
	        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
	        user.setPwUpdateTime(new Timestamp(System.currentTimeMillis()));
	        return mapper.updateUser(user) > 0;
	    } catch (Exception e) {
	        System.out.println("[UsersService] 비밀번호 변경 중 예외 발생: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}



	// -----------------------------------------------------------------------------------------------------
	public boolean checkCurrentPassword(int userNumber, String currentPassword) {
		System.out.println("[UsersService] checkCurrentPassword() 호출");

		UsersDTO user = mapper.findUserByUserNumber(userNumber);
		if (user != null && BCrypt.checkpw(currentPassword, user.getPassword())) {
			System.out.println("[UsersService] 현재 비밀번호 일치");
			return true;
		}
		System.out.println("[UsersService] 현재 비밀번호 불일치");
		return false;
	}

	// -----------------------------------------------------------------------------------------------------
	// 사용자 이름과 이메일로 아이디 찾기
	// 새작업 12월 15일
	public String findLoginId(String userName, String userEmail) {
		System.out.println("[UsersService] findLoginId 호출 -> userName: " + userName + ", userEmail: " + userEmail);
		try {
			return mapper.findLoginIdByUserNameAndEmail(userName, userEmail);
		} catch (Exception e) {
			System.out.println("[UsersService] findLoginId 예외 발생: " + e.getMessage());
			return null;
		}
	}

	// 사용자 이름과 이메일로 비밀번호 찾기
	// 새작업 12월 16일
	public UsersDTO verifyUserForPasswordReset(String userName, String loginId, String userEmail) {
		System.out.println("[UsersService] 비밀번호 초기화 사용자 검증 시작");
		System.out.println("입력된 정보 -> 이름: " + userName + ", 아이디: " + loginId + ", 이메일: " + userEmail);

		// 파라미터 구성
		Map<String, String> params = new HashMap<>();
		params.put("userName", userName);
		params.put("loginId", loginId);
		params.put("userEmail", userEmail);

		// 사용자 확인
		UsersDTO user = null;
		try {
			user = mapper.findUserForPasswordReset(params);
			if (user != null) {
				System.out.println("[UsersService] 사용자 확인 성공 -> 로그인 아이디: " + user.getLoginId());
			} else {
				System.out.println("[UsersService] 사용자 확인 실패 -> 데이터베이스에서 찾을 수 없음");
			}
		} catch (Exception e) {
			System.out.println("[UsersService] 사용자 검증 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
		}

		return user;
	}

	public boolean resetPassword(int userNumber, String newPassword) {
		System.out.println("[UsersService] 비밀번호 초기화 시작 -> 사용자 번호: " + userNumber);

		// 비밀번호 해싱 처리
		String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		System.out.println("[UsersService] 해싱된 비밀번호: " + hashedPassword);

		// 업데이트를 위한 파라미터 맵 구성
		Map<String, Object> params = new HashMap<>();
		params.put("userNumber", userNumber); // 사용자 번호
		params.put("password", hashedPassword); // 해싱된 비밀번호

		try {
			int result = mapper.updatePassword(params);
			if (result > 0) {
				System.out.println("[UsersService] 비밀번호 초기화 성공");
				return true;
			} else {
				System.out.println("[UsersService] 비밀번호 초기화 실패 -> 업데이트된 행 없음");
				return false;
			}
		} catch (Exception e) {
			System.out.println("[UsersService] 비밀번호 초기화 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	// -----------------------------------------------------------------------------------------------------

	/**
	 * 모든 사용자 정보를 조회합니다.
	 * 
	 * @return 사용자 목록 (List 형태로 반환)
	 */
	public List<UsersDTO> selectAllUsers() {
		try (SqlSession session = DBManager.getInstance().getSession()) {
			UsersMapper mapper = session.getMapper(UsersMapper.class);
			// 관리자를 제외한 사용자 조회
			List<UsersDTO> users = mapper.selectAllUsersExcludeAdmin();
			System.out.println("모든 사용자 조회 성공. 사용자 수: " + users.size());
			return users;
		} catch (Exception e) {
			System.out.println("selectAllUsers 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	// -----------------------------------------------------------------------------------------------------
	
	/**
	 * 사용자와 연관된 데이터 삭제 후 사용자 삭제
	 * 
	 * @param userNumber 사용자 번호
	 * @return 삭제 성공 여부
	 */
	public boolean deleteUserAndRelatedData(int userNumber) {
		SqlSession session = null;
		try {
			session = DBManager.getInstance().getSession(); // autoCommit=false로 설정된 세션
			UsersMapper mapper = session.getMapper(UsersMapper.class);

			// 댓글 삭제
			int deletedComments = mapper.deleteCommentsByUserNumber(userNumber);
			System.out.println("삭제된 댓글 수: " + deletedComments);

			// 게시물 삭제
			int deletedBoards = mapper.deleteBoardsByUserNumber(userNumber);
			System.out.println("삭제된 게시물 수: " + deletedBoards);

			// 사용자 삭제
			int deletedUser = mapper.deleteUserByUserNumber(userNumber);
			System.out.println("삭제된 사용자 수: " + deletedUser);

			// 모든 작업이 성공하면 커밋
			session.commit();
			return deletedUser > 0;
		} catch (Exception e) {
			if (session != null) {
				session.rollback(); // 예외 발생 시 롤백
				System.out.println("deleteUserAndRelatedData 중 예외 발생: " + e.getMessage() + ", 롤백 수행");
			}
			System.out.println("deleteUserAndRelatedData 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			if (session != null) {
				session.close(); // 세션 닫기
				System.out.println("SqlSession 닫기");
			}
		}
	}
	// -----------------------------------------------------------------------------------------------------
	
	/**
	 * 사용자 삭제
	 * 
	 * @param userNumber 사용자 번호
	 * @return 삭제된 행의 수
	 */
	public int deleteUser(int userNumber) {
		System.out.println("deleteUser() 호출, userNumber: " + userNumber);
		try (SqlSession session = DBManager.getInstance().getSession()) {
			UsersMapper mapper = session.getMapper(UsersMapper.class);
			int result = mapper.deleteUserByUserNumber(userNumber);
			session.commit();
			System.out.println("사용자 삭제 성공 - 삭제된 행 수: " + result);
			return result;
		} catch (Exception e) {
			System.out.println("deleteUser 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}
	// -----------------------------------------------------------------------------------------------------
	
	/**
	 * 사용자 번호로 특정 사용자 정보 조회
	 * 
	 * @param userNumber 사용자 번호
	 * @return 사용자 정보 (UsersDTO)
	 */
	public UsersDTO selectUserByNumber(int userNumber) {
		System.out.println("selectUserByNumber() 호출 -> userNumber: " + userNumber);
		try (SqlSession session = DBManager.getInstance().getSession()) {
			UsersMapper mapper = session.getMapper(UsersMapper.class);
			UsersDTO user = mapper.selectUserByNumber(userNumber);
			if (user != null) {
				System.out.println("사용자 조회 성공 - 사용자: " + user.getNickName());
			} else {
				System.out.println("사용자 조회 실패 - 존재하지 않는 userNumber: " + userNumber);
			}
			return user;
		} catch (Exception e) {
			System.out.println("selectUserByNumber 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	// 프로필 작업부분---------------------------------------------------------------------------

    /**
     * 사용자 번호를 기준으로 프로필 이미지 경로를 조회합니다.
     * 
     * @param userNumber 사용자 번호
     * @return 프로필 이미지 파일명
     */
	public String getProfileImage(int userNumber) {
	    System.out.println("[UsersService] getProfileImage() 호출 -> 사용자 번호: " + userNumber);
	    try {
	        String imageName = mapper.getProfileImage(userNumber);
	        System.out.println("[UsersService] DB에서 가져온 이미지 이름: " + imageName);
	        return imageName;
	    } catch (Exception e) {
	        System.out.println("[UsersService] 프로필 이미지 조회 중 예외 발생: " + e.getMessage());
	        return null;
	    }
	}


    /**
     * 사용자 프로필 이미지를 업데이트합니다.
     * 
     * @param userNumber 사용자 번호
     * @param fileName   새 이미지 파일명
     */
    public void updateProfileImage(int userNumber, String fileName) {
        System.out.println("[UsersService] updateProfileImage() 호출 -> 사용자 번호: " + userNumber + ", 파일명: " + fileName);
        try {
            mapper.updateProfileImage(userNumber, fileName);
            System.out.println("[UsersService] 프로필 이미지 업데이트 성공");
        } catch (Exception e) {
            System.out.println("[UsersService] 프로필 이미지 업데이트 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 사용자 프로필 이미지를 기본 이미지로 변경합니다.
     * 
     * @param userNumber 사용자 번호
     */
    public void deleteProfileImage(int userNumber) {
        System.out.println("[UsersService] deleteProfileImage() 호출 -> 사용자 번호: " + userNumber);
        try {
            mapper.updateProfileImage(userNumber, "Default_IMG.png");
            System.out.println("[UsersService] 프로필 이미지 기본 이미지로 변경 성공");
        } catch (Exception e) {
            System.out.println("[UsersService] 프로필 이미지 기본 이미지 변경 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
