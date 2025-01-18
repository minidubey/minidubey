document.addEventListener('DOMContentLoaded', () => {
    const dropArea = document.getElementById('dropArea');
    const fileInput = document.getElementById('fileInput');
    const summarizeBtn = document.getElementById('summarizeBtn');
    const summaryOutput = document.getElementById('summaryOutput');

    // Handle drag and drop
    dropArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropArea.classList.add('dragging');
    });

    dropArea.addEventListener('dragleave', () => {
        dropArea.classList.remove('dragging');
    });

    dropArea.addEventListener('drop', (e) => {
        e.preventDefault();
        dropArea.classList.remove('dragging');

        const file = e.dataTransfer.files[0];
        if (file) {
            fileInput.files = e.dataTransfer.files;
            summarizeBtn.style.display = 'block';
        }
    });

    dropArea.addEventListener('click', () => fileInput.click());

    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length > 0) {
            summarizeBtn.style.display = 'block';
        }
    });

    // Handle summarize button click
    summarizeBtn.addEventListener('click', async () => {
        const file = fileInput.files[0];
        if (!file) {
            alert('Please upload a file.');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch('http://localhost:5001/summarize', {
                method: 'POST',
                body: formData,
            });

            if (response.ok) {
                const { summary } = await response.json();
                summaryOutput.textContent = summary;
            } else {
                summaryOutput.textContent = 'Failed to generate summary.';
            }
        } catch (error) {
            summaryOutput.textContent = 'Error connecting to server.';
            console.error('Error:', error);
        }
    });
});
