<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>게시글 작성</title>
    <link rel="stylesheet" type="text/css" href="css/board_write_view.css">
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
    <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
    <script>
    	window.onload = () => {
    		const editor = new toastui.Editor({
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
    		
    		//파일 드래그 처리
    		let file_area = document.querySelector('.file_drop_area');
		let file = document.querySelector('#file');
		
		file_area.ondrop = (e) => {
			e.preventDefault();
			const data = e.dataTransfer;
			console.log(data);
			console.log(data.files);
			//파일 태그에 드래그한 파일 정보를 연결
			file.files = data.files;
			let file_list_view = document.querySelector('.file_list_view');
			for(let i=0;i<data.files.length;i++){
				file_list_view.innerHTML += `\${data.files[i].name}<br>`;
			}

			e.target.classList.remove('file_area_active');

		}
		
		file_area.ondragover = (e) => {
			e.preventDefault();
		}
		
		file_area.ondragenter = (e) => {
			e.target.classList.add('file_area_active');
			e.preventDefault();
		}
		
		file_area.ondragleave = (e) => {
			e.target.classList.remove('file_area_active');
			e.preventDefault();
		}
    	}
    </script>
    <style>
    	body{
    		height: 1000px;
    	}
		.file_drop_area{
			width:200px;
			height: 200px;
			border : 2px dashed #e9e9e9;
			background-image: url('img/file.png');
			background-repeat: no-repeat;
			background-position: center;
		}
		#file{
			display:none;
		}
		.file_area_active{
			box-shadow: 0px 0px 3px 5px red;
		}
	</style>
</head>
<body>
    <div class="container">
        <h1>게시글 작성</h1>
        <form action="./boardWrite.do" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" id="title" name="title" required>
            </div>
            <div class="form-group">
                <label for="content">내용</label>
                <div id="content"></div>
                <input type="hidden" name="content">
            </div>
            <div class="file_drop_area"></div>
			<input type="file" name="file" id="file">
			<div class="file_list_view"></div>
            <div class="form-actions">
                <button type="submit" class="btn submit-btn">등록</button>
                <a href="./boardMain.do" class="btn cancel-btn">취소</a>
            </div>
        </form>
    </div>
</body>
</html>