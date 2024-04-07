import subprocess
import base64
from flask import Flask, request, jsonify
import os

server = Flask(__name__)

SINGLEFILE_EXECUTABLE = '/node_modules/single-file/cli/single-file'
BROWSER_PATH = '/opt/google/chrome/google-chrome'
BROWSER_ARGS = '["--no-sandbox"]'
SINGLEFILE_PATH = '/home/node'

@server.route('/snapshot', methods=['POST'])
def singlefile():
    try:
        json_data = request.get_json() 
        url = json_data.get('url')
        p = subprocess.Popen([
            SINGLEFILE_EXECUTABLE,
            '--browser-executable-path=' + BROWSER_PATH,
            "--browser-args='%s'" % BROWSER_ARGS,
            url,
            '--dump-content',
            ], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        singlefile_html, stderr = p.communicate()  # 等待进程完成并获取输出

        if p.returncode != 0:
            raise Exception(f"Subprocess error: {stderr.decode('utf-8')}")

        singlefile_html = singlefile_html.decode('utf-8')  # 将字节串解码为字符串
        file_name = base64.urlsafe_b64encode(url.encode('utf-8')).decode('utf-8')
        file_path = os.path.join(SINGLEFILE_PATH, file_name + '.html')
        with open(file_path, 'w', encoding='utf-8') as file:
            file.write(singlefile_html)
        
        return jsonify({'code': 200, 'message': 'Success'})
    except Exception as e:
        return jsonify({'code': 500, 'message': f'Internal server error: {str(e)}'}), 500

if __name__ == '__main__':
    server.run(host='0.0.0.0', port=80)
