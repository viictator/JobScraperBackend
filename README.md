# 🎓 Student Job Finder

A Python-based job scraping tool designed for myself to automatically collect relevant student job postings from multiple platforms — including KEA's internal job portal and other external sites.

This project uses undetected Chrome automation to bypass bot detection and makes finding job postings a breeze.

---

## 🚀 Features

- 🔐 Automatically Logs into KEA's student job portal using secure credentials
- 🌐 Scrapes job listings from additional external websites
- 🛡️ Uses `undetected-chromedriver` to bypass bot detection
- 🤖 Generate cover letter drafts for jobs automatically based on your profile bio
- 🔧 Easily configurable with environment variables
- 🧩 Modular structure (`main.py`, `keascraper.py`, etc.)

---

📦 Requirements
Before running, make sure the following are installed:

- [Python](https://www.python.org/downloads/)
- [Google Chrome](https://www.google.com/chrome/)

⚙️ Installation
1. Clone Both Repositories(Frontend & Backend)

# Backend (you're here)
git clone https://github.com/viictator/JobScraperBackend.git

cd JobScraperBackend

pip install setuptools

pip install undetected-chromedriver

pip install beautifulsoup4




# Linux/macOS Environment Variables

export LOGIN_EMAIL=your_email@stud.kea.dk

export LOGIN_PASSWORD=your_secure_password

# Windows CMD Environment Variables

set LOGIN_EMAIL=your_email@stud.kea.dk

set LOGIN_PASSWORD=your_secure_password




# Frontend

git clone https://github.com/viictator/JobScraperFrontend.git

cd JobScraperFrontend

npm install

npm run dev








▶️ How to Use

Start the backend in one terminal/tab.

Start the frontend in another using npm run dev.

Open your browser to http://localhost:5173/.

Now you're able to register and sign in to your account.

Go to your profile, write a bio and put in your contact details.

From here, go to the job listings -> select a job -> generate a cover letter automatically (will be based on your profile and the job description) and apply to the job. ✔️


