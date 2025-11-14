/* 指定された video タグを再生する処理*/
function playVideo(videoId) {
    const video = document.getElementById(videoId);
    if(video) {
        video.currentTime = 0;// 再生位置を先頭に戻してから再生
        video.play();
    }
}

/*ステータスを「確認済み or 管理者承認」に更新する処理*/
function toggleStatus(btn, logId, type) {
    btn.disabled = true;
    
// CSRF トークンを meta タグから取得（
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    
// 送信データ生成
    let bodyData = { id: logId };
    if (type === 'kakunin') bodyData.statusKakunin = 1;
    else if (type === 'kanri') bodyData.statusKanri = 1;
    
// 通話ログのステータスを更新する処理を呼び出す
    fetch('/updateStatus', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken// CSRF 対策
        },
        credentials: 'same-origin',
        body: JSON.stringify(bodyData)
    }).then(res => {
        if(res.ok){
            alert('更新成功');
            btn.classList.add('disabled');
        } else {
            alert('更新失敗');
            btn.disabled = false;
        }
    }).catch(err => {
        console.error('Error:', err);
        alert('通信エラー');
        btn.disabled = false;
    });
}


