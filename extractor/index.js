import express from 'express';
import { extract } from './extractor.js';
const app = express();
const port = 3000;

// Middleware to parse JSON request body
app.use(express.json());

// Define a POST endpoint
app.post('/extract', (req, res) => {
    // Get data from the request body
    const data = req.body;
    console.log('Received data:', data);

    ;
    // Respond with the same data in JSON format
    res.json({
        content: extract(data['url'], data['jsPath'])
    });
});

// Start the server
app.listen(port, () => {
    console.log(`Server running on http://localhost:${port}`);
});

