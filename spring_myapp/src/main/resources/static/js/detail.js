function playVideo(videoId) {
    const video = document.getElementById(videoId);
    if(video) {
        video.currentTime = 0;
        video.play();
    }
}

function toggleStatus(btn, logId, type) {
    btn.disabled = true;

    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    let bodyData = { id: logId };
    if (type === 'kakunin') bodyData.statusKakunin = 1;
    else if (type === 'kanri') bodyData.statusKanri = 1;

    fetch('/updateStatus', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
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
