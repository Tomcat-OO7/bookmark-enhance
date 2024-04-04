let currUrl;
chrome.tabs.query({currentWindow: true, active: true}, function (tabs) {
    currUrl = tabs[0].url;

    fetch('http://localhost:8082/bookmark/checkTask?url=' + encodeURIComponent(currUrl))
        .then(response => response.text())  // 将响应转换为文本
        .then(data => {
            let sendBtn = document.getElementById('send-btn');
            if (data === 'EXIST TASK' || data === 'EXIST INDEX') {
                sendBtn.innerText = '当前网页' + data;
                sendBtn.disabled = true;
                sendBtn.style = ''; // 清空按钮样式
                sendBtn.style.backgroundColor = 'gray';
            }
        })
        .catch((error) => {
            console.error('请求失败:', error);
        });
});


document.getElementById('send-btn').addEventListener('click', function () {
    var jsPath = document.getElementById('html-js-path').value;
    fetch('http://localhost:8082/bookmark/addTask', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            url: currUrl,
            jsPath: jsPath
        }),
    }).then(response => response.text())
        .then(text => {
            console.log('请求成功:', text);
            window.close();
        })
        .catch((error) => {
            console.error('请求失败:', error);
        });
})