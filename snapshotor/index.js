import express from 'express';
import {execSync} from 'child_process';

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
    try {
        execSync("node /usr/src/app/node_modules/single-file-cli/single-file \"" + url + "\" "
            + btoa(url) + ".html "
            + "--browser-executable-path /usr/bin/chromium-browser --output-directory /home/node --dump-content");
        console.log(url + " file saved");
        res.json({ code: 200 });
    } catch (err) {
        console.error(err);
        res.json({ code: 500 });
    }
});

// Start the server
app.listen(port, () => {
    console.log(`Server running on http://localhost:${port}`);
});

