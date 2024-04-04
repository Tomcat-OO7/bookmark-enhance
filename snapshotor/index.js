import express from 'express';
import {execSync} from 'child_process';
import fs from 'fs';

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

    const outputDir = "/home/node";
    const filename = btoa(url) + ".html"

    if (fs.existsSync(`${outputDir}/${filename}`)) {
        return res.json({code: 200});
    }

    try {
        execSync("node /usr/src/app/node_modules/single-file-cli/single-file \"" + url + "\" "
            + filename
            + ` --browser-executable-path /usr/bin/chromium-browser --output-directory ${outputDir}`
            + " --dump-content"
        );

        console.log(url + " file saved");
        res.json({code: 200});
    } catch (err) {
        console.error(err);
        res.json({code: 500});
    }
});

// Start the server
app.listen(port, () => {
    console.log(`Server running on http://localhost:${port}`);
});

