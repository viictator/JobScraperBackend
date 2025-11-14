import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from bs4 import BeautifulSoup
import requests

BASE_URL = "https://www.jobindex.dk/jobsoegning/storkoebenhavn?employment_type=3&subid=1&subid=93"

def scrape():
    options = uc.ChromeOptions()
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-gpu")
    # options.add_argument("--headless")  # Uncomment for headless scraping

    class PatchedChrome(uc.Chrome):
        def __del__(self):
            pass  # Prevent undetected_chromedriver cleanup bug

    driver = PatchedChrome(options=options)
    print("🌐 Browser launched")

    data = []
    page = 1

    while True:
        page_url = f"{BASE_URL}&page={page}"
        print(f"➡️ Navigating to {page_url}")
        driver.get(page_url)

        try:
            WebDriverWait(driver, 5).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, "div.jobsearch-result"))
            )
            print(f"✅ Job cards loaded on page {page}")
        except TimeoutException:
            print(f"❌ No job listings or timeout on page {page}, stopping.")
            break

        soup = BeautifulSoup(driver.page_source, 'html.parser')
        job_cards = soup.select("div.jobsearch-result")

        if not job_cards:
            print(f"⚠️ No job cards found on page {page}, stopping.")
            break

        for job in job_cards:
            try:
                title_tag = job.select_one("h4 a")
                company_tag = job.select_one("div.jix-toolbar-top__company a")
                location_tag = job.select_one("div.jobad-element-area span")
                date_tag = job.select_one("time")

                # Reset description parts for each job
                description_parts = []
                description_ptags = job.select("p")
                for p in description_ptags:
                    description_parts.append(p.get_text(strip=True))

                description_litags = job.select("ul li")
                for li in description_litags:
                    description_parts.append("• " + li.get_text(strip=True))

                title = title_tag.text.strip() if title_tag else "N/A"
                link = f"{title_tag['href']}" if title_tag else "N/A"
                company = company_tag.text.strip() if company_tag else "N/A"
                location = location_tag.text.strip() if location_tag else "N/A"
                date_posted = date_tag.text.strip() if date_tag else "N/A"
                description = "\n".join(description_parts) if description_parts else "N/A"

                data.append({
                    "jobTitle": title,
                    "companyName": company,
                    "location": location,
                    "link": link,
                    "time": date_posted,
                    "description": description,
                    "contract": "No information",  # Not always available
                    "originsite": "Jobindex"
                })
            except Exception as e:
                print(f"⚠️ Skipping job card due to error: {e}")
                continue

        page += 1  # Move to next page

    driver.quit()
    print(f"🎉 Successfully scraped {len(data)} jobs from Jobindex.")
    return data





