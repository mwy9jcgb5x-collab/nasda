/**
 * sticker.js - 생성, 조회, 수정, 삭제(DB 동기화) 완벽 복구 버전
 */
(function() {
    let categories = [];
    let stickersInPalette = [];
    let stickers = [];
    let isDecorating = false;
    let selectedSticker = null;

    // --- [1] 핵심 렌더링: 드래그 이동 & 조작 버튼 & 개별 삭제 ---
    function renderStickers() {
        document.querySelectorAll('.sticker-layer').forEach(layer => layer.innerHTML = '');

        stickers.forEach((s) => {
            const targetLayer = document.querySelector(`.sticker-layer[data-image-id="${s.postImageId}"]`);
            if (!targetLayer) return;

            const isSelected = selectedSticker === s;
            const el = document.createElement('div');

            // ✅ z-index를 대폭 높여 클릭 우선순위와 가시성 확보
            el.className = `sticker-item absolute transform -translate-x-1/2 -translate-y-1/2 cursor-move ${isSelected ? 'z-[10000]' : 'z-10'}`;
            el.style.left = s.x + '%';
            el.style.top = s.y + '%';

            // ✅ 모든 이모지 0.43 사이즈 동일 고정 적용
            const flipX = s.isFlipped ? -1 : 1;
            const currentScale = 0.43;
            el.style.transform = `translate(-50%, -50%) scale(${currentScale * flipX}, ${currentScale}) rotate(${s.rotation || 0}deg)`;

            el.innerHTML = `
                <img src="${s.imgUrl}" class="w-24 h-24 object-contain pointer-events-none bg-transparent" 
                     style="background: transparent !important; ${isSelected ? 'filter: drop-shadow(0 0 10px #fbcfe8); border: 2.5px dashed #fbcfe8; border-radius: 12px;' : ''}">
                ${isDecorating && isSelected ? `
                    <div class="btn-single-remove absolute -top-5 -right-5 bg-red-600 text-white rounded-full w-8 h-8 flex items-center justify-center text-sm cursor-pointer shadow-xl border-2 border-white z-[10001]">×</div>
                    
                    <div class="sticker-control-panel absolute -bottom-16 left-1/2 -translate-x-1/2 flex gap-1.5 bg-white/95 p-2 rounded-full shadow-2xl border border-pink-200 z-[10001] pointer-events-auto" style="min-width: 190px;">
                        <button type="button" class="c-btn op-up">➕</button>
                        <button type="button" class="c-btn op-down">➖</button>
                        <button type="button" class="c-btn op-rotate">🔄</button>
                        <button type="button" class="c-btn op-flip">↔️</button>
                        <button type="button" class="c-btn op-reset">🧹</button>
                    </div>
                ` : ''}
            `;

            // ✅ 버튼 이벤트 직접 연결 (ReferenceError 방지)
            if (isSelected && isDecorating) {
                el.querySelector('.op-up').onclick = (e) => { e.stopPropagation(); updateAction('scale', 0.1); };
                el.querySelector('.op-down').onclick = (e) => { e.stopPropagation(); updateAction('scale', -0.1); };
                el.querySelector('.op-rotate').onclick = (e) => { e.stopPropagation(); updateAction('rotate', 15); };
                el.querySelector('.op-flip').onclick = (e) => { e.stopPropagation(); updateAction('flip', 0); };
                el.querySelector('.op-reset').onclick = (e) => { e.stopPropagation(); updateAction('reset', 0); };
                el.querySelector('.btn-single-remove').onclick = (e) => {
                    e.stopPropagation();
                    stickers = stickers.filter(item => item !== s);
                    selectedSticker = null;
                    renderStickers();
                };
            }

            // ✅ 드래그 이동 활성화 (mousedown)
            // [A] 드래그 이동 기능 (mousedown 이벤트 핸들러)
            el.onmousedown = (e) => {
                // 꾸미기 모드가 아니거나 조작 패널을 클릭한 경우는 드래그 무시
                if (!isDecorating || e.target.closest('.sticker-control-panel')) return;

                e.preventDefault();
                e.stopPropagation();

                selectedSticker = s; // 클릭한 스티커 선택 상태로 변경
                renderStickers(); // 테두리 표시를 위해 즉시 다시 그림

                const rect = targetLayer.getBoundingClientRect();

                // 마우스가 움직일 때 실행될 함수
                const onMouseMove = (mE) => {
                    // 부모 레이어 안에서의 상대적 좌표(%) 계산
                    let newX = ((mE.clientX - rect.left) / rect.width) * 100;
                    let newY = ((mE.clientY - rect.top) / rect.height) * 100;

                    // 화면 밖으로 나가지 않도록 0~100 사이로 제한
                    s.x = Math.max(0, Math.min(100, newX));
                    s.y = Math.max(0, Math.min(100, newY));

                    // ✅ 실시간 위치 반영
                    el.style.left = s.x + '%';
                    el.style.top = s.y + '%';
                };

                // 마우스를 뗐을 때 실행될 함수
                const onMouseUp = () => {
                    document.removeEventListener('mousemove', onMouseMove);
                    document.removeEventListener('mouseup', onMouseUp);

                    // 최종 위치 확정을 위해 한 번 더 렌더링
                    renderStickers();
                };

                // 문서 전체에 이벤트 등록 (스티커 밖으로 마우스가 나가도 드래그 유지되도록)
                document.addEventListener('mousemove', onMouseMove);
                document.addEventListener('mouseup', onMouseUp);
            };
            targetLayer.appendChild(el);
        });
    }

    function updateAction(type, val) {
        if (!selectedSticker) return;
        // 0.43 고정 요구에 따라 scale 변경 로직은 유지하되 렌더링 시 0.43 적용 (필요 시 scale 값 반영 가능)
        if (type === 'rotate') selectedSticker.rotation = ((selectedSticker.rotation || 0) + val) % 360;
        if (type === 'flip') selectedSticker.isFlipped = !selectedSticker.isFlipped;
        if (type === 'reset') { selectedSticker.rotation = 0; selectedSticker.isFlipped = false; }
        renderStickers();
    }

    // --- [2] 저장 기능: 삭제 상태 DB 동기화 (가장 중요) ---
    window.saveDecoration = async function() {
        const allImageLayers = Array.from(document.querySelectorAll('.sticker-layer'));
        const allImageIds = allImageLayers.map(l => Number(l.getAttribute('data-image-id')));

        const groups = stickers.reduce((acc, obj) => {
            if (!acc[obj.postImageId]) acc[obj.postImageId] = [];
            acc[obj.postImageId].push(obj);
            return acc;
        }, {});

        try {
            // ✅ 핵심: Promise.all 대신 순서대로(async/await) 하나씩 요청 보냄
            for (const imageId of allImageIds) {
                const layerStickers = groups[imageId] || [];

                // 한 레이어에 대한 저장이 완전히 끝날 때까지 기다립니다.
                await axios.post('/api/decorations', {
                    postImageId: imageId,
                    userId: Number(window.ST_DATA?.currentUserId || 1),
                    decorations: layerStickers.map(s => ({
                        stickerId: s.stickerId,
                        posX: parseFloat(s.x.toFixed(2)),
                        posY: parseFloat(s.y.toFixed(2)),
                        scale: 0.43,
                        rotation: s.rotation || 0,
                        zIndex: 10
                    }))
                });
                console.log(`이미지 ID ${imageId} 저장 완료`);
            }

            alert("모든 스티커 설정이 저장되었습니다! ✨");
            location.reload();

        } catch (error) {
            console.error("저장 중 오류 발생:", error);
            alert("저장 중 데드락 또는 통신 오류가 발생했습니다. 다시 시도해주세요.");
        }
    };

    window.clearAllStickers = function() {
        if (confirm('모든 스티커를 비우시겠습니까?')) {
            stickers = []; selectedSticker = null; renderStickers();
        }
    };

    // --- [3] 초기화 및 기타 로직 ---
    window.startDecoration = function() {
        isDecorating = true;
        document.querySelectorAll('.sticker-layer').forEach(l => l.style.pointerEvents = 'auto');
        document.getElementById('deco-active-view')?.classList.remove('hidden');
        document.getElementById('deco-start-view')?.classList.add('hidden');
        fetchStickerCategories();
    };

    window.handleStickerError = function(img) {
        const item = img.closest('.palette-item');
        if (item) item.remove();
    };

    async function fetchStickerCategories() {
        try {
            const response = await axios.get('/api/sticker-categories');
            categories = response.data;
            renderCategoryTabs();
            if (categories.length > 0) fetchStickersByCategory(categories[0].stickerCategoryId);
        } catch (err) { console.error("카테고리 로드 실패"); }
    }

    async function fetchStickersByCategory(categoryId) {
        try {
            const response = await axios.get(`/api/stickers/categories/${categoryId}`);
            stickersInPalette = response.data;
            renderPalette();
        } catch (err) { console.error("스티커 로드 실패"); }
    }

    function renderCategoryTabs() {
        const tabContainer = document.getElementById('sticker-category-tabs');
        if (!tabContainer) return;
        tabContainer.innerHTML = '';
        categories.forEach((cat, idx) => {
            const tab = document.createElement('button');
            tab.className = `category-btn ${idx === 0 ? 'active' : ''}`;
            tab.textContent = cat.name;
            tab.onclick = () => {
                document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
                tab.classList.add('active');
                fetchStickersByCategory(cat.stickerCategoryId);
            };
            tabContainer.appendChild(tab);
        });
    }

    function renderPalette() {
        const palette = document.getElementById('sticker-palette');
        if (!palette) return;
        palette.innerHTML = '';
        stickersInPalette.forEach((sticker) => {
            const div = document.createElement('div');
            div.className = 'palette-item cursor-grab p-2 hover:bg-pink-50 rounded-xl flex items-center justify-center bg-transparent';
            div.innerHTML = `<img src="${sticker.stickerImageUrl}" onerror="window.handleStickerError(this)" class="w-12 h-12 object-contain pointer-events-none bg-transparent" style="background:transparent !important;">`;
            div.draggable = true;
            div.addEventListener('dragstart', (e) => {
                e.dataTransfer.setData('imgUrl', sticker.stickerImageUrl);
                e.dataTransfer.setData('stickerId', sticker.stickerId);
            });
            palette.appendChild(div);
        });
    }

    document.addEventListener('DOMContentLoaded', () => {
        const postId = window.ST_DATA?.postId;
        if (postId) {
            axios.get(`/api/decorations/post/${postId}`).then(res => {
                stickers = res.data.map(item => ({
                    dbId: item.decorationId, postImageId: item.postImageId,
                    stickerId: item.stickerId, imgUrl: item.stickerImageUrl,
                    x: item.posX, y: item.posY, scale: 0.43,
                    rotation: item.rotation, zIndex: item.zIndex, isSaved: true
                }));
                renderStickers();
            });
        }

        document.querySelectorAll('.sticker-layer').forEach(layer => {
            layer.addEventListener('dragover', e => e.preventDefault());
            layer.addEventListener('drop', e => {
                if (!isDecorating) return;
                e.preventDefault();
                const imgUrl = e.dataTransfer.getData('imgUrl');
                const stickerId = e.dataTransfer.getData('stickerId');
                const imageId = layer.getAttribute('data-image-id');
                const rect = layer.getBoundingClientRect();
                if (!imgUrl || !imageId) return;

                stickers.push({
                    postImageId: Number(imageId), stickerId: Number(stickerId),
                    imgUrl: imgUrl, x: ((e.clientX - rect.left) / rect.width) * 100,
                    y: ((e.clientY - rect.top) / rect.height) * 100,
                    scale: 0.43, rotation: 0, isFlipped: false, isSaved: false
                });
                renderStickers();
            });
        });

        document.addEventListener('mousedown', (e) => {
            if (!e.target.closest('.sticker-item') && !e.target.closest('.sticker-control-panel')) {
                selectedSticker = null; renderStickers();
            }
        });
    });
})();