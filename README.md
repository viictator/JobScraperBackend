# 🎓 Student Job Finder

A Python-based job scraping tool designed for myself to automatically collect relevant student job postings from multiple platforms — including KEA's internal job portal and other external sites.

This project uses undetected Chrome automation to bypass bot detection and makes finding job postings a breeze.

---

## 🚀 Features

- 🔐 Logs into KEA's student job portal using secure credentials
- 🌐 Scrapes job listings from additional external websites
- 🛡️ Uses `undetected-chromedriver` to bypass bot detection
- 🔧 Easily configurable with environment variables
- 🧩 Modular structure (`main.py`, `keascraperFinal.py`, etc.)

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

Set Environment Variables
# Linux/macOS
export LOGIN_EMAIL=your_email@stud.kea.dk
export LOGIN_PASSWORD=your_secure_password

# Windows CMD
set LOGIN_EMAIL=your_email@stud.kea.dk
set LOGIN_PASSWORD=your_secure_password

After this, start the Spring Boot backend.

# Frontend (in a separate terminal/tab)
git clone https://github.com/viictator/JobScraperFrontend.git
cd JobScraperFrontend
npm install
npm run dev



▶️ How to Use
Start the backend in one terminal/tab.

Start the frontend in another using npm run dev.

Open your browser to http://localhost:5173/.

Click the "Start Scraper" button to begin finding relevant IT/Programmer student jobs

🧠 The frontend sends a request to the backend, which triggers the appropriate scraper scripts (main.py).


