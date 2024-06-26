// 현재 페이지 번호를 저장하는 전역 변수
var currentPageNumber = 1;

$(document).ready(function () {
	$('.filter-id').click(function () {
		// 모든 필터의 색상을 초기화
		$('.filter-id').removeClass('selected-flag');

		// 클릭된 요소에 클래스 추가
		$(this).addClass('selected-flag');

		// 클릭된 요소의 id 추출
		var selectedId = $(this).attr('id');
		console.log(selectedId);

		// id를 이용한 추가 작업을 여기에 작성
	});
});

// 페이지네이션 생성 함수
function createPagination(currentPage, totalPages) {

	var paginationHtml = '<div class="pagination">';
	var startPage, endPage;
	var maxPage = 5;

	// 현재 페이지가 1부터 10 사이인 경우
	if (currentPage <= maxPage) {
		startPage = 1;
		endPage = maxPage;
	} else {
		// 현재 페이지를 기준으로 앞뒤로 5페이지씩 표시
		var startOffset = (currentPage % maxPage) === 0 ? maxPage : currentPage % maxPage;
		startPage = currentPage - startOffset + 1;
		endPage = startPage + maxPage - 1;

		// 총 페이지 수를 넘지 않도록 조정
		if (endPage > totalPages) {
			endPage = totalPages;
			startPage = totalPages - maxPage + 1;
		}
	}

	// '처음' 페이지로 이동하는 버튼
	if (startPage > 1) {
		paginationHtml += '<button class="page-item page-link" data-page="1">처음</button>';
	}

	// '이전 10페이지' 버튼 생성
	if (startPage > maxPage) {
		paginationHtml += '<button class="page-item page-link" data-page="' + (startPage - maxPage) + '">이전 5페이지</button>';
	}

	// 페이지 번호 버튼 생성
	for (var i = startPage; i <= endPage; i++) {
		paginationHtml += '<button class="page-item page-link' + (i === currentPage ? ' active' : '') + '" data-page="' + i + '">' + i + '</button>';
	}

	// '다음 10페이지' 버튼 생성
	if (endPage < totalPages) {
		paginationHtml += '<button class="page-item page-link" data-page="' + (endPage + 1) + '">다음 5페이지</button>';
	}

	// '마지막' 페이지로 이동하는 버튼
	if (endPage < totalPages) {
		paginationHtml += '<button class="page-item page-link" data-page="' + totalPages + '">마지막</button>';
	}

	paginationHtml += '</div>';
	$('.pagination').html(paginationHtml);
	
  	// 페이지 번호 버튼 클릭 이벤트 리스너
    $('.page-link').on('click', function() {
        currentPageNumber = $(this).data('page'); // 클릭된 페이지 번호를 전역 변수에 저장합니다.
        sendSearchCriteria(); // 페이지 번호가 업데이트된 후 sendSearchCriteria 함수를 호출합니다.
    });
}

		


// #homeIcon 클릭 시 데이터를 가져오는 함수와 전체 게시물 수를 가져오는 함수를 호출합니다.
$('#home-icon').click(function () {
	sendSearchCriteria();
});






  // 전용 면적 관려 js


// 최소 면적 선택박스 변경 시 최대 면적 옵션 업데이트 함수
function updateMaxAreaOptions() {
    var minArea = parseInt(document.getElementById('min-area').value);
    var maxAreaSelect = document.getElementById('max-area');
    maxAreaSelect.innerHTML = ''; // 기존 옵션 제거

    // 최소 면적 선택 시 최대 면적 옵션 생성
    for (var i = minArea + 10; i <= 100; i += 10) {
        var option = document.createElement('option');
        option.value = i;
        option.textContent = i + '㎡';
        maxAreaSelect.appendChild(option);
    }
    
    // '최대' 옵션 추가
    var maxOption = document.createElement('option');
    maxOption.value = 'max'; // '최대' 옵션의 value 설정
    maxOption.textContent = '최대';
    maxAreaSelect.appendChild(maxOption);

    updateAreaDisplay(); // 선택된 면적대 표시 업데이트
}

// 선택된 면적대 표시 업데이트 함수
function updateAreaDisplay() {
    var minArea = document.getElementById('min-area').value;
    var maxArea = document.getElementById('max-area').value;
    var minAreaDisplay = minArea === '0' ? '0㎡' : minArea + '㎡';
    var maxAreaDisplay = maxArea === 'max' ? '최대' : maxArea + '㎡';
    
    document.getElementById('min-area-display').textContent = minAreaDisplay;
    document.getElementById('max-area-display').textContent = maxAreaDisplay;
}

// 면적대 확인 함수
function confirmAreaRange() {
    var minArea = document.getElementById('min-area').value;
    var maxArea = document.getElementById('max-area').value;

    // 화면에 표시
    document.getElementById('min-area-display').textContent = minArea + '㎡';
    document.getElementById('max-area-display').textContent = maxArea === 'max' ? '최대' : maxArea + '㎡';
    
    // TODO: 서버로 값 전송하는 로직 구현
	 sendSearchCriteria();
	 closeAreaPopup(); // 면적대를 확인한 후 팝업창을 닫습니다.
}


	// 선택된 조건에 따른 데이터 개수를 가져오는 AJAX 요청 함수
	function getFilteredEstateCount(district, neighborhood, PriceMin, PriceMax, ExclusiveSizeMin, ExclusiveSizeMax) {
	    return new Promise((resolve, reject) => {
	        $.ajax({
	            url: '/realestate/count/byCriteria',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({
				districtName: district,
				neighborhoodName: neighborhood,
				PriceMin: PriceMin,
				PriceMax: PriceMax,
				ExclusiveSizeMin: ExclusiveSizeMin,
				ExclusiveSizeMax:ExclusiveSizeMax
			}),
			dataType: 'json',
	            success: function (count) {
	                resolve(count);
	            },
	            error: function (error) {
	                console.error('Error fetching estate count by criteria:', error);
	                reject(error);
	            }
	        });
	    });
	}



function showRealEstate(response) {
    // response에서 부동산 데이터를 추출하여 HTML로 변환합니다.
    var realEstateHtml = response.map(function(estate) {
        return `
            <div class="building">
                <div class="building-header">
                    <div class="building-label">아파트</div>
                    <div class="building-name">${estate.realEstate.complexName}</div>
                </div>
                <div class="building-info">
                    <div>
                        <span>${estate.estateExtra.cityName}</span>
                        <span> / ${estate.estateExtra.districtName}</span>
                        <span> / ${estate.estateExtra.neighborhoodName}</span>
                    </div>
                    <div>
                        <span>설립:${estate.realEstate.constructionYear}</span>
                        <span> / 번지:${estate.realEstate.address}</span>
                        <span> / 도로명:${estate.realEstate.addressStreet}</span>
                    </div>
                </div>
                <div class="building-sale">
                    <div>
                        <div class="lately-title">최근 매매 실거래가</div>
                        <div class="lately-price">${estate.realEstateSale.salePrice}억</div>
                        <div class="lately-info">오늘, ${estate.realEstateSale.floor}, ${estate.realEstateSale.exclusiveArea}㎡</div>
                    </div>
                    <div class="sale-info">
                        <div>
                            <span class="sale-title">전체 1년 매매가</span>
                            <span class="sale-price">${estate.estateExtra.allPrice}</span>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }).join('');
    
    // 생성된 HTML을 district-list-container에 삽입합니다.
    $('.district-list-container').html(realEstateHtml);
}
