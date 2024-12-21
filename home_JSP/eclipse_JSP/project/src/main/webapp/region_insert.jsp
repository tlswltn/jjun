<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>지역 정보 작성</title>
<!-- ToastUI 에디터 CSS 및 JS -->
<link rel="stylesheet"
	href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
<script
	src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
<script
	src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=f05ff66bab3d2444d168e2e13e1ed414&libraries=services"></script>
<style>
/* 공통 스타일 */
body {
	font-family: Arial, sans-serif;
	margin: 0;
	padding: 0;
	background-color: #f4f4f4;
}

.container {
	max-width: 1200px;
	margin: 0 auto;
	padding: 20px;
	background-color: #fff;
	border-radius: 8px;
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.header {
	background-color: #81d4fa;
	padding: 20px;
	text-align: center;
	color: #333;
	font-size: 28px;
}

.section {
	margin-bottom: 30px;
}

.section-title {
	font-size: 20px;
	margin-bottom: 10px;
	border-bottom: 2px solid #ddd;
	padding-bottom: 5px;
}

input {
	width: 100%;
	padding: 10px;
	box-sizing: border-box;
	margin-bottom: 10px;
}

button {
	background-color: #007BFF;
	color: #fff;
	border: none;
	padding: 10px 15px;
	border-radius: 5px;
	cursor: pointer;
}

button:hover {
	background-color: #0056b3;
}

#map {
	width: 100%;
	height: 500px;
	margin-bottom: 20px;
}

.search-container {
	display: flex;
	margin-bottom: 10px;
}

.search-input {
	flex: 1;
	padding: 10px;
	font-size: 16px;
	border: 1px solid #ddd;
	border-radius: 5px 0 0 5px;
}

.search-button {
	background-color: #007BFF;
	color: white;
	border: none;
	padding: 10px 20px;
	cursor: pointer;
	border-radius: 0 5px 5px 0;
}

.search-button:hover {
	background-color: #0056b3;
}

.result-list {
	list-style: none;
	padding: 0;
	margin: 0;
}

.result-item {
	padding: 10px;
	border: 1px solid #ddd;
	border-radius: 5px;
	margin-bottom: 5px;
	cursor: pointer;
}

.result-item:hover {
	background-color: #f9f9f9;
}
</style>
</head>
<body>
	<div class="header">지역 정보 작성</div>

	<div class="container">
		<form action="./insertRegion.do" method="post"
			enctype="multipart/form-data">
			<div class="section">
				<h2 class="section-title">썸네일 이미지</h2>
				<img id="thumbnail-preview" src="" alt="이미지 미리보기"
					style="display: none;"> <input type="file" id="image"
					name="image" accept="image/*">
			</div>
			<div class="section">
				<h2 class="section-title">지역 이름</h2>
				<input type="text" name="title" placeholder="지역 이름을 입력하세요" required>
			</div>
			<div class="section">
				<h2 class="section-title">지역 설명</h2>
				<div id="description"></div>
				<input type="hidden" name="description">
			</div>

			<div class="section">
				<h2 class="section-title">지도 검색</h2>
				<div class="search-container">
					<input type="text" id="search-input" class="search-input"
						placeholder="검색어를 입력하세요">
					<button id="search-button" class="search-button" type="button">검색</button>
				</div>
				<div id="map"></div>
				<ul id="result-list" class="result-list"></ul>
				<label for="latitude">위도 (Latitude):</label> <input type="text"
					id="latitude" name="latitude" readonly> <label
					for="longitude">경도 (Longitude):</label> <input type="text"
					id="longitude" name="longitude" readonly>
			</div>

			<div class="section">
				<button type="submit">작성</button>
			</div>
		</form>
	</div>

	<script>
	window.onload = () => {
		// 마커를 클릭하면 장소명을 표시할 인포윈도우
		 const editor = new toastui.Editor({
        el: document.querySelector('#description'),
        height: '500px',
        initialEditType: 'wysiwyg',
        previewStyle: 'vertical',
    });
		

    // 폼 제출 시, editor 내용 Hidden Input으로 설정
    document.querySelector('form').onsubmit = () => {
        const descriptionContent = editor.getHTML();
        document.querySelector('input[name=description]').value = descriptionContent;
    };
	 };
		var infowindow = new kakao.maps.InfoWindow({ zIndex: 1 });

		// 지도 생성
		var mapContainer = document.getElementById('map'); // 지도를 표시할 div
		var mapOption = {
			center: new kakao.maps.LatLng(37.566826, 126.9786567), // 초기 지도 중심 좌표 (서울)
			level: 3 // 초기 확대 레벨
		};
		var map = new kakao.maps.Map(mapContainer, mapOption);

		// 장소 검색 객체 생성
		var ps = new kakao.maps.services.Places();

		// 검색 결과 리스트
		var resultList = document.getElementById('result-list');

		// 검색 버튼 클릭 이벤트
		document.getElementById('search-button').addEventListener('click', function() {
			var keyword = document.getElementById('search-input').value;

			if (!keyword.trim()) {
				alert('검색어를 입력하세요.');
				return;
			}

			// 키워드로 장소 검색
			ps.keywordSearch(keyword, placesSearchCB);
		});

		// 장소 검색 완료 시 호출되는 콜백 함수
		function placesSearchCB(data, status, pagination) {
			if (status === kakao.maps.services.Status.OK) {
				// 기존 검색 결과 삭제
				resultList.innerHTML = '';

				// 검색 결과 표시
				data.forEach(function(place) {
					displayMarker(place); // 지도에 마커 표시
					displayResultItem(place); // 결과 리스트 표시
				});
			} else if (status === kakao.maps.services.Status.ZERO_RESULT) {
				alert('검색 결과가 없습니다.');
			} else {
				alert('검색 중 오류가 발생했습니다.');
			}
		}

		// 지도에 마커를 표시하는 함수
		function displayMarker(place) {
			var marker = new kakao.maps.Marker({
				map: map,
				position: new kakao.maps.LatLng(place.y, place.x)
			});

			// 마커 클릭 이벤트
			kakao.maps.event.addListener(marker, 'click', function() {
				infowindow.setContent('<div style="padding:5px;font-size:12px;">' + place.place_name + '</div>');
				infowindow.open(map, marker);

				// 좌표 입력 필드 업데이트
				document.getElementById('latitude').value = place.y;
				document.getElementById('longitude').value = place.x;
			});
		}

		// 검색 결과 리스트에 항목 추가하는 함수
		function displayResultItem(place) {
			var item = document.createElement('li');
			item.className = 'result-item';
			item.innerText = place.place_name + ' (' + place.address_name + ')';
			item.addEventListener('click', function() {
				// 지도 이동 및 마커 표시
				var coords = new kakao.maps.LatLng(place.y, place.x);
				map.setCenter(coords);
				infowindow.setContent('<div style="padding:5px;font-size:12px;">' + place.place_name + '</div>');
				infowindow.open(map, new kakao.maps.Marker({ position: coords }));

				// 좌표 입력 필드 업데이트
				document.getElementById('latitude').value = place.y;
				document.getElementById('longitude').value = place.x;
			});

			resultList.appendChild(item);
		}
	</script>
</body>
</html>
