// 加载并显示当前存储的请求地址
document.addEventListener('DOMContentLoaded', () => {
    chrome.storage.sync.get('requestUrl', (data) => {
        document.getElementById('request-url').value = data.requestUrl || '';
    });
});

// 保存请求地址到存储
document.getElementById('config-form').addEventListener('submit', (event) => {
    event.preventDefault();
    const requestUrl = document.getElementById('request-url').value;
    chrome.storage.sync.set({requestUrl}, () => {
        console.log('Request URL saved:', requestUrl);
    });
});