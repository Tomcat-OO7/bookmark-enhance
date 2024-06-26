import fs from 'fs';
import { Readability } from '@mozilla/readability';
import { JSDOM } from 'jsdom';

export function extract(url, jsPath) {
    const htmlContent = fs.readFileSync('/home/node/' + base64UrlEncode(url) + '.html','utf8');

    var doc = new JSDOM(htmlContent);

    if (typeof jsPath === 'string' && jsPath.trim() !== '') {
        var selected = doc.window.document.querySelector(jsPath).outerHTML;
        doc = new JSDOM(`<html><body>${selected}</body></html>`);
    } else if (doc.window.document.querySelector('article')) {
        var articleContent = doc.window.document.querySelector('article').outerHTML;
        doc = new JSDOM(`<html><body>${articleContent}</body></html>`);
    }

    let reader = new Readability(doc.window.document);
    let article = reader.parse();
    console.log(article);
    return article['textContent'];
}



function base64UrlEncode(str) {
    // 将字符串转换为Base64
    let base64Str = btoa(str);
    // 将Base64中的 '+' 替换为 '-'
    base64Str = base64Str.replace(/\+/g, '-');
    // 将Base64中的 '/' 替换为 '_'
    base64Str = base64Str.replace(/\//g, '_');
    return base64Str;
}
