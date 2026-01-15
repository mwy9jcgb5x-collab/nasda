/**
 * 나의 영감 저장소 - Main JavaScript
 * 브런치/핀터레스트 감성 디자인
 */

// ========================================
// ✅ (추가) 헤더에서 호출하는 함수명 브릿지
//    header가 openSearchModal()/closeSearchModal()을 호출해도 동작하도록 보장
// ========================================
function openSearchModal() {
    openSearchModalLegacy();
}
function closeSearchModal() {
    closeSearchModalLegacy();
}

// ========================================
// 1. 전역 변수
// ========================================
let currentCategory = '전체';
let searchQuery = '';
let isLoading = false;

// ========================================
// 2. 모바일 검색 모달
// ========================================
function openSearchModalLegacy() {
    const modal = document.getElementById('searchModal');
    if (modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';

        const input = modal.querySelector('.search-input-modal');
        if (input) {
            setTimeout(() => input.focus(), 100);
        }
    }
}

function closeSearchModalLegacy() {
    const modal = document.getElementById('searchModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = '';
    }
}

// 모달 외부 클릭 시 닫기
document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('searchModal');
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeSearchModal();
            }
        });
    }

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeSearchModal();
        }
    });
});

// ========================================
// 3. Masonry Grid 레이아웃 최적화
// ========================================
function initMasonryLayout() {
    const grid = document.getElementById('masonryGrid');
    if (!grid) return;

    // 이미지 로드 완료 후 레이아웃 재계산
    const images = grid.querySelectorAll('img');
    let loadedImages = 0;

    images.forEach(img => {
        if (img.complete) {
            loadedImages++;
        } else {
            img.addEventListener('load', function() {
                loadedImages++;
                if (loadedImages === images.length) {
                    optimizeLayout();
                }
            });

            img.addEventListener('error', function() {
                // 이미지 로드 실패 시 placeholder
                this.src = 'https://via.placeholder.com/400x600?text=No+Image';
                loadedImages++;
                if (loadedImages === images.length) {
                    optimizeLayout();
                }
            });
        }
    });

    if (loadedImages === images.length) {
        optimizeLayout();
    }
}

function optimizeLayout() {
    const grid = document.getElementById('masonry-grid');
    if (!grid) return;

    // CSS Grid는 자동으로 레이아웃을 처리하므로
    // 추가적인 높이 조정이 필요한 경우에만 사용
    console.log('Layout optimized');
}

// ========================================
// 4. 무한 스크롤 (선택사항)
// ========================================
let currentPage = 1;
let hasMore = true;

function initInfiniteScroll() {
    window.addEventListener('scroll', function() {
        // 페이지 하단에 가까워지면 다음 페이지 로드
        const scrollPosition = window.innerHeight + window.scrollY;
        const threshold = document.body.offsetHeight - 500;

        if (scrollPosition >= threshold && !isLoading && hasMore) {
            loadMorePosts();
        }
    });
}

function loadMorePosts() {
    if (isLoading) return;

    isLoading = true;
    showLoadingSpinner();

    // Axios 또는 fetch를 사용하여 서버에서 데이터 가져오기
    // 예시:
    fetch(`/api/posts?page=${currentPage + 1}&category=${currentCategory}`)
        .then(response => response.json())
        .then(data => {
            if (data.posts && data.posts.length > 0) {
                appendPosts(data.posts);
                currentPage++;
            } else {
                hasMore = false;
            }
        })
        .catch(error => {
            console.error('Error loading posts:', error);
        })
        .finally(() => {
            isLoading = false;
            hideLoadingSpinner();
        });
}

function appendPosts(posts) {
    const grid = document.getElementById('masonryGrid');
    if (!grid) return;

    posts.forEach(post => {
        const postElement = createPostElement(post);
        grid.appendChild(postElement);
    });

    // 레이아웃 재계산
    initMasonryLayout();
}

function createPostElement(post) {
    const div = document.createElement('div');
    div.className = 'masonry-item';

    const imageHtml = post.images && post.images.length > 0
        ? `<div class="post-image">
               <img src="${post.images[0]}" alt="${post.title}">
           </div>`
        : '';

    div.innerHTML = `
    <a href="/posts/${post.id}" class="post-card">
            ${imageHtml}
            <div class="post-info">
                <h3 class="post-title">${post.title}</h3>
                <p class="post-description">${post.description || ''}</p>
                <div class="post-meta">
                    <span class="post-category">${post.category}</span>
                    <span class="post-author">${post.author?.username || 'Anonymous'}</span>
                </div>
            </div>
        </a>
    `;

    return div;
}

function showLoadingSpinner() {
    let spinner = document.getElementById('loadingSpinner');
    if (!spinner) {
        spinner = document.createElement('div');
        spinner.id = 'loadingSpinner';
        spinner.className = 'loading';
        spinner.innerHTML = '<div class="spinner"></div>';
        document.querySelector('.main-content').appendChild(spinner);
    }
    spinner.style.display = 'flex';
}

function hideLoadingSpinner() {
    const spinner = document.getElementById('loadingSpinner');
    if (spinner) {
        spinner.style.display = 'none';
    }
}

// ========================================
// 5. 카테고리 필터링
// ========================================
function filterByCategory(category) {
    currentCategory = category;
    currentPage = 1;
    hasMore = true;

    // 서버에서 필터링된 데이터 가져오기
    window.location.href = `/?category=${encodeURIComponent(category)}`;
}

// ========================================
// 6. 검색 기능
// ========================================
function handleSearch(event, formElement) {
    event.preventDefault();

    const keyword = formElement.querySelector('input[name="keyword"]').value.trim();
    const type = formElement.querySelector('select[name="type"]').value;

    if (!keyword) {
        alert('검색어를 입력하세요.');
        return;
    }

    // 검색 실행
    window.location.href = `/search?keyword=${encodeURIComponent(keyword)}&type=${encodeURIComponent(type)}`;
}

// ========================================
// 7. 이미지 지연 로딩 (Lazy Loading)
// ========================================
function initLazyLoading() {
    const images = document.querySelectorAll('img[data-src]');

    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.removeAttribute('data-src');
                    imageObserver.unobserve(img);
                }
            });
        });

        images.forEach(img => imageObserver.observe(img));
    } else {
        // Fallback for browsers that don't support IntersectionObserver
        images.forEach(img => {
            img.src = img.dataset.src;
            img.removeAttribute('data-src');
        });
    }
}

// ========================================
// 8. 카테고리 스크롤 (모바일)
// ========================================
function initCategoryScroll() {
    const container = document.querySelector('.category-container');
    if (!container) return;

    // 활성 카테고리로 자동 스크롤
    const activeItem = container.querySelector('.category-item.active');
    if (activeItem) {
        const containerWidth = container.offsetWidth;
        const itemOffset = activeItem.offsetLeft;
        const itemWidth = activeItem.offsetWidth;

        // 활성 아이템을 중앙에 배치
        container.scrollLeft = itemOffset - (containerWidth / 2) + (itemWidth / 2);
    }
}

// ========================================
// 9. Smooth Scroll to Top
// ========================================
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// 로고 클릭 시 맨 위로 스크롤
document.addEventListener('DOMContentLoaded', function() {
    const logo = document.querySelector('.logo');
    if (logo) {
        logo.addEventListener('click', scrollToTop);
    }
});

// ========================================
// 10. 폼 검증
// ========================================
function validateSearchForm(form) {
    const keyword = form.querySelector('input[name="keyword"]').value.trim();

    if (!keyword) {
        alert('검색어를 입력하세요.');
        return false;
    }

    if (keyword.length < 2) {
        alert('검색어는 2글자 이상 입력하세요.');
        return false;
    }

    return true;
}

// ========================================
// 11. 초기화
// ========================================
document.addEventListener('DOMContentLoaded', function() {
    console.log('나의 영감 저장소 - 페이지 로드 완료');

    // Masonry 레이아웃 초기화
    initMasonryLayout();

    // Lazy Loading 초기화
    initLazyLoading();

    // 카테고리 스크롤 초기화
    initCategoryScroll();

    // 무한 스크롤 초기화 (선택사항)
    // initInfiniteScroll();

    // 검색 폼 이벤트 리스너
    // const searchForms = document.querySforms);
    const searchForms = document.querySelectorAll('form');
    searchForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!validateSearchForm(form)) {
                e.preventDefault();
            }
        });
    });

    // 이미지 로드 에러 처리
    const images = document.querySelectorAll('.post-image img');
    images.forEach(img => {
        img.addEventListener('error', function() {
            this.src = 'https://via.placeholder.com/400x600?text=No+Image';
            this.alt = 'Image not found';
        });
    });

    // 뒤로가기/앞으로가기 시 스크롤 위치 복원
    if ('scrollRestoration' in history) {
        history.scrollRestoration = 'manual';
    }

    // 페이지 언로드 시 스크롤 위치 저장
    window.addEventListener('beforeunload', function() {
        sessionStorage.setItem('scrollPosition', window.scrollY);
    });

    // 페이지 로드 시 스크롤 위치 복원
    const savedScrollPosition = sessionStorage.getItem('scrollPosition');
    if (savedScrollPosition) {
        window.scrollTo(0, parseInt(savedScrollPosition));
        sessionStorage.removeItem('scrollPosition');
    }
});

// ========================================
// 12. 윈도우 리사이즈 처리
// ========================================
let resizeTimeout;
window.addEventListener('resize', function() {
    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(function() {
        // 리사이즈 완료 후 레이아웃 재계산
        initMasonryLayout();
    }, 250);
});

// ========================================
// 13. 성능 최적화
// ========================================
// Debounce 함수
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    }
}

// Throttle 함수
function throttle(func, limit) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// ========================================
// 14. 유틸리티 함수
// ========================================
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;

    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 7) {
        return date.toLocaleDateString('ko-KR');
    } else if (days > 0) {
        return `${days}일 전`;
    } else if (hours > 0) {
        return `${hours}시간 전`;
    } else if (minutes > 0) {
        return `${minutes}분 전`;
    } else {
        return '방금 전';
    }
}

function truncateText(text, maxLength) {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

// ========================================
// 15. 접근성 개선
// ========================================
// 키보드 네비게이션
document.addEventListener('keydown', function(e) {
    // Tab 키로 포커스 이동 시 outline 표시
    if (e.key === 'Tab') {
        document.body.classList.add('keyboard-navigation');
    }
});

document.addEventListener('mousedown', function() {
    document.body.classList.remove('keyboard-navigation');
});

// ========================================
// 16. 에러 처리
// ========================================
window.addEventListener('error', function(e) {
    console.error('JavaScript Error:', e.error);
    // 에러 로깅 서비스로 전송 (선택사항)
});

window.addEventListener('unhandledrejection', function(e) {
    console.error('Unhandled Promise Rejection:', e.reason);
    // 에러 로깅 서비스로 전송 (선택사항)
});

// ========================================
// 17. Export (모듈 사용 시)
// ========================================
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        openSearchModalLegacy,
        closeSearchModalLegacy,
        filterByCategory,
        initMasonryLayout,
        scrollToTop
    };
}
