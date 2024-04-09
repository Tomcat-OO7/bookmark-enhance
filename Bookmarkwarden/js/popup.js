chrome.storage.sync.get('requestUrl', (data) => {
    let requestUrl = data.requestUrl || "http://localhost:3005"; // 如果没有获取到值，使用默认地址

    chrome.tabs.query({currentWindow: true, active: true}, function (tabs) {
        let currUrl = tabs[0].url;
        fetch(`${requestUrl}/bookmark/checkTask?url=` + encodeURIComponent(currUrl))
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
});

document.getElementById('send-btn').addEventListener('click', function () {
    var jsPath = document.getElementById('html-js-path').value;

    // 因为是在事件监听器中，需要再次从存储中获取 requestUrl
    chrome.storage.sync.get('requestUrl', (data) => {
        let requestUrl = data.requestUrl || "http://localhost:3005"; // 如果没有获取到值，使用默认地址

        chrome.tabs.query({currentWindow: true, active: true}, function (tabs) {
            let currUrl = tabs[0].url;

            fetch(`${requestUrl}/bookmark/addTask`, {
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
        });
    });
});

document.getElementById('logo').addEventListener('click', function () {
    chrome.storage.sync.get('requestUrl', (data) => {
        let requestUrl = data.requestUrl || "http://localhost:3005"; // 如果没有获取到值，使用默认地址
        window.open(`${requestUrl}/index.html`, '_blank'); // 在新标签页中打开 URL
    });
});
