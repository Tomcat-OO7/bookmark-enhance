import express from 'express';
import {exec} from 'child_process';

const app = express();
const port = 3000;

// Middleware to parse JSON request body
app.use(express.json());

// Define a POST endpoint
app.post('/snapshot', (req, res) => {
    // Get data from the request body
    console.log(req.body);
    const data = req.body;
    const url = data['url'];
    exec(" node /usr/src/app/node_modules/single-file-cli/single-file \"" + url + "\" "
        + btoa(url) + ".html "
        + "--browser-executable-path /usr/bin/chromium-browser --output-directory /home/node --dump-content"
        // + "--filename-conflict-action overwrite "
        , (err, stdout, stderr) => {
            console.log(url + " file saved");
            if (err) {
                console.error(err);
                return res.json({
                    code: 500
                });
            }
        }
    )
    ;
    // Respond with the same data in JSON format
    res.json({
        code: 200
    });
});

// Start the server
app.listen(port, () => {
    console.log(`Server running on http://localhost:${port}`);
});

