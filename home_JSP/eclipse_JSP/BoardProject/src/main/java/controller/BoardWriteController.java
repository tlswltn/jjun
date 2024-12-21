package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dto.BoardDTO;
import dto.BoardFileDTO;
import dto.BoardMemberDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import service.BoardService;
import view.ModelAndView;

public class BoardWriteController implements Controller {

	@Override
	public ModelAndView execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//title, content 파라미터 읽어옴
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		
		//작성자 ID --> 세션 있는 BoardMemberDTO에서 읽어옴
		String id = ((BoardMemberDTO)request.getSession().getAttribute("user")).getId();
//		HttpSession session = request.getSession();
//		BoardMemberDTO dto = (BoardMemberDTO) session.getAttribute("user");
//		id = dto.getId();
		
		//파일 업로드 처리
		File root = new File("c:\\FileUpLoad");
		//해당 경로가 있는지 체크, 없으면 해당 경로 생성
		if(!root.exists()) {
			System.out.println("파일 업로드할 폴더 및 경로 생성");
			root.mkdirs();
		}		
		
		Iterator<Part> it = request.getParts().iterator();
		List<BoardFileDTO> fileList = new ArrayList<BoardFileDTO>();
		//업로드할 파일 정보를 읽는 부분
		while(it.hasNext()) {
			Part part = it.next();
			if(part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty()) {
				//파일 쓰기
				part.write(root.getAbsolutePath()+"\\"+part.getSubmittedFileName());
				//BoardFileDTO 만들어서 리스트 추가
				//게시글번호, 파일 경로 가지고서 BoardFileDTO 생성
				//리스트에다가 추가
				BoardFileDTO fdto = new BoardFileDTO();
				fdto.setFpath(root.getAbsolutePath()+"\\"+part.getSubmittedFileName());
				fileList.add(fdto);
			}
		}
		
		//게시글 등록 
		BoardDTO dto = new BoardDTO();
		dto.setId(id);	dto.setTitle(title);	dto.setContent(content);
		int count = BoardService.getInstance().insertBoard(dto,fileList);
		System.out.println("게시글 등록 결과 : " + count);
		//main.jsp로 이동(./boardMain.do)
		ModelAndView view = new ModelAndView();
		view.setPath("./boardMain.do");
		view.setRedirect(true);
		
		return view;
	}

}