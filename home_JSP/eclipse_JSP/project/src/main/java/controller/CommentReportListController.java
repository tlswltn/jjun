package controller;

import java.io.IOException;
import java.util.List;

import dto.CommentReportDTO;
import dto.UserReportDTO;
import dto.UsersDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.ReportService;
import view.ModelAndView;

public class CommentReportListController implements Controller {

	@Override
	public ModelAndView execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 세션에서 사용자 정보 가져오기
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			System.out.println("[AdminReportListController] 비로그인 상태 - 로그인 페이지로 리다이렉트");
			response.sendRedirect("login.do"); // .do로 변경하여 DispatcherServlet을 거치도록
			return null;
		}

		UsersDTO user = (UsersDTO) session.getAttribute("user");
		if (!user.isAdmin()) { // 관리자 여부 확인
			System.out.println("[AdminReportListController] 비관리자 사용자 접근 시도 - 접근 거부");
			response.sendRedirect("accessDenied.jsp");
			return null;
		}

		// 요청 파라미터에서 처리 타입 확인 (승인 또는 거부)
		String action = request.getParameter("action"); // "approve" 또는 "reject"
		String reportNumberStr = request.getParameter("reportNumber");
		if (action != null && reportNumberStr != null) {
			try {
				int reportNumber = Integer.parseInt(reportNumberStr);
				String status = action.equals("approve") ? "APPROVED" : "REJECTED";

				// 관리자 ID 가져오기
				int adminId = user.getUserNumber(); // UsersDTO에 userNumber 사용

				// 신고 상태 업데이트
				boolean isUpdated = ReportService.getinstance().updateCommentReportStatus(reportNumber, status, adminId);
				System.out.println(reportNumber);
				System.out.println(status);
				System.out.println(adminId);
				if (isUpdated) {
					System.out.println("[AdminReportListController] 신고 처리 성공 - ReportNumber: " + reportNumber
							+ ", Status: " + status);
				} else {
					System.out.println("[AdminReportListController] 신고 처리 실패 - ReportNumber: " + reportNumber);
				}
			} catch (NumberFormatException e) {
				System.out.println("[AdminReportListController] 잘못된 reportNumber 입력");
			}
		}
		// 신고 목록 조회
		List<CommentReportDTO> list = ReportService.getinstance().selectCommentReports();
		if (list != null) {
			System.out.println("[AdminReportListController] 신고 목록 조회 성공 - 개수: " + list.size());
			for (CommentReportDTO report : list) {
				System.out.println(report); // DTO의 toString 메서드로 상세 로그 출력
			}
			request.setAttribute("list", list);
		} else {
			System.out.println("[AdminReportListController] 신고 목록 조회 실패");
		}

		ModelAndView view = new ModelAndView();
		view.setPath("./adminCommentReportList.jsp");
		view.setRedirect(false);
		return view;
	}

}
