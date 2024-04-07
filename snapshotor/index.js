import express from 'express';
import {execSync} from 'child_process';
import fs from 'fs';
import path from 'path';

const app = express();
const port = 3000;
const outputDir = "/home/node";

// Middleware to parse JSON request body
app.use(express.json());
app.use(express.static(path.join(outputDir)));

// Define a POST endpoint
app.post('/snapshot', (req, res) => {
    // Get data from the request body
    console.log(req.body);
    const data = req.body;
    const url = data['url'];

    const filename = btoa(url) + ".html"

    if (fs.existsSync(`${outputDir}/${filename}`)) {
        return res.json({code: 200});
    }

    try {
        execSync("node /usr/src/app/node_modules/single-file-cli/single-file \"" + url + "\" "
            + filename
            + ` --browser-executable-path /usr/bin/chromium-browser --output-directory ${outputDir}`
            + ' --block-images true'
            + ' --block-fonts true'
            + ' --compress-CSS true'
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

