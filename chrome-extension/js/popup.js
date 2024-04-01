var currentUrl;
chrome.tabs.query({currentWindow: true, active: true}, function (tabs) {
    currentUrl = tabs[0].url;
});

document.getElementById('send-btn').addEventListener('click', function () {
    var jsPath = document.getElementById('html-js-path').value;
    fetch('http://localhost:8082/bookmark/addTask', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            url: currentUrl,
            jsPath: jsPath
        }),
    }).then(response => response.text())
        .then(text => {
            console.log('请求成功:', text);
        })
        .catch((error) => {
            console.error('请求失败:', error);
        });
})

// //    let translatePageBtn = $("#translate-page");
// //    console.log($("body").html());

//     let newTranslateBtn = $("#new-translation");
//     newTranslateBtn.on("click", function() {
//         $(".translate-content").hide();
//         $("#search-bar").show();
//     });
// })();

// function translate(type, text, from, to) {
//     let url = "https://translate.favlink.cn/";
//     const typeName = window.typeMapping(type);
//     url += typeName;
//     url += "?text=" + encodeURIComponent(text);
//     let isCharacter = window.isCharacter(text);
//     if(!from) {
//         if(isCharacter) {
//             from = "zh";
//         } else {
//             from = "en";
//         }
//     }
//     url += "&from=" + from;

//     if(!to) {
//         if(isCharacter) {
//             to = "en";
//         } else {
//             to = "zh";
//         }
//     }
//     url += "&to=" + to;
//     window.commonTranslate(type, url, function(ret) {
//         $("#search-bar").hide();
//         $(".translate-content").show();
//         const originLang = isCharacter ? "中文" : "英文";
//         const targetLang = isCharacter ? "英文" : "中文";
//         // 有道的特殊处理一下
//         let html = `
//                 <div class="origin-lang">源语种: ${originLang}</div>
//                 <div class="origin-text">${text}</div>
//                 <div class="target-lang">目标语种: ${targetLang}</div>
//              `;
//         if(!ret.data) {
//             return;
//         }
//         if(type === "4") {
//             if(ret.data.explains) {
//                 if(ret.data.phonetic) {
//                     html += `<div class="target-text">音标: <span class="red">[${ret.data.phonetic}]</span></div>`;
//                 }
//                 for(let item of ret.data.explains) {
//                     html += `<div class="target-text">${item}</div>`;
//                 }
//             } else if(ret.data.translation) {
//                 for(let item of ret.data.translation) {
//                     html += `<div class="target-text">${item}</div>`;
//                 }
//             }
//         } else {
//             html += `<div class="target-text">${ret.data}</div>`;
//         }
//         $("#translation").html(html);
//     }, function (err) {
//         alert(err);
//     });
// }