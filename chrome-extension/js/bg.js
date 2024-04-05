chrome.runtime.onInstalled.addListener(function () {
    chrome.contextMenus.create({
        id: 'sendUrlMenu',
        title: '收藏当前网页',
        type: 'normal',
        contexts: ['page'],
    });
});

chrome.contextMenus.onClicked.addListener(function (info, tab) {
    if (info.menuItemId === 'sendUrlMenu') {
        const url = tab.url;
        // 在这里获取 requestUrl
        chrome.storage.sync.get('requestUrl', (data) => {
            let requestUrl = data.requestUrl || "http://localhost:3005"; // 如果没有获取到值，使用默认地址

            fetch(`${requestUrl}/bookmark/addTask`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    url: url
                }),
            })
                .then(response => response.text())
                .then(text => {
                    console.log('请求成功:', text);
                    // 如果接口返回的是字符串 "OK"，将菜单项设置为不可点击
                    // if (text === 'OK') {
                    //     chrome.contextMenus.update('sendUrlMenu', {
                    //         enabled: false
                    //     });
                    // }
                })
                .catch((error) => {
                    console.error('请求失败:', error);
                });
        });
    }
});
