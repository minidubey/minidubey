const express = require('express');
const multer = require('multer');
const pdfParse = require('pdf-parse');
const bodyParser = require('body-parser');
const axios = require('axios');
require('dotenv').config();

const app = express();
const port = 5001;

// Middleware to parse JSON
app.use(bodyParser.json());

// Multer setup for file uploads with file size limit (10 MB)
const upload = multer({
    dest: 'uploads/',
    limits: { fileSize: 10 * 1024 * 1024 }, // 10 MB limit
});

// Endpoint to summarize text from uploaded files
app.post('/summarize', upload.single('file'), async (req, res) => {
    if (!req.file) {
        return res.status(400).json({ error: 'No file provided.' });
    }

    try {
        // Check for supported file types
        if (!['text/plain', 'application/pdf'].includes(req.file.mimetype)) {
            return res.status(400).json({ error: 'Unsupported file type. Only .txt and .pdf files are allowed.' });
        }

        let text = '';

        // Handle .txt files
        if (req.file.mimetype === 'text/plain') {
            text = require('fs').readFileSync(req.file.path, 'utf-8');
        }

        // Handle .pdf files
        if (req.file.mimetype === 'application/pdf') {
            const pdfBuffer = require('fs').readFileSync(req.file.path);
            const pdfData = await pdfParse(pdfBuffer);
            text = pdfData.text;
        }

        if (!text) {
            return res.status(400).json({ error: 'Failed to extract text from file.' });
        }

        // Summarize using MeaningCloud API
        const response = await axios.post(
            'https://api.meaningcloud.com/summarization-1.0',
            null,
            {
                params: {
                    key: process.env.MEANINGCLOUD_API_KEY,
                    txt: text,
                    sentences: 5, // Adjust the number of sentences
                },
            }
        );

        if (response.data && response.data.summary) {
            return res.json({ summary: response.data.summary });
        } else {
            return res.status(500).json({ error: 'Failed to generate summary.' });
        }
    } catch (error) {
        console.error('Error:', error.message);
        return res.status(500).json({ error: 'An error occurred while processing the file.' });
    }
});

// Start the server
app.listen(port, () => {
    console.log(`Server running on port ${port}`);
});
