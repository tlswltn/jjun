<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>게시글 작성</title>
    <!-- 에디터 추가 -->
    <link rel="stylesheet" type="text/css" href="css/board_write_view.css">
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" /> 
    <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
    
    
    <script>
    	window.onload = () => {
    		const editor = new toastui.Editor({ <!-- 에디터 연결 -->
    			  el: document.querySelector('#content'),
    			  height: '500px',
    			  initialEditType: 'wysiwyg',
    			  previewStyle: 'vertical'
   			});		
    		
    		document.querySelector('form').onsubmit = (e) => {
    			//e.preventDefault();
    			console.log(editor.getHTML());
    			console.log(editor.getMarkdown());
    			document.querySelector('input[name=content]').value = editor.getHTML(); 
    		}
    	}
    </script>
</head>
<body>
    <div class="container">
        <h1>게시글 수정</h1>
        <form action="./boardUpdate.do" method="post">
        	<input type="hidden" name="bno" value="${board.bno }"> <!-- 기존 게시물 번호도 그대로 가져오기 위해 hidden처리 후 가져오기 -->
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" id="title" name="title" value="${board.title }" required>
            </div>
            <div class="form-group">
                <label for="content">내용</label>
                <div id="content">${board.content }</div> <!-- 각 위치 전 데이터 다시 그대로 가져오기 ${board.content } value="${board.title } -->
                <input type="hidden" name="content">
            </div>
            <div class="form-actions">
                <button type="submit" class="btn submit-btn">수정</button>
                <a href="javascript:history.back();" class="btn cancel-btn">취소</a> <!-- href="javascript:history.back();" : 뒤로가기 처리 -->
            </div>
        </form>
    </div>
</body>
</html>