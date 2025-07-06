import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import requests
from bs4 import BeautifulSoup
import pickle
import os
import time
from urllib.parse import urlparse

COOKIES_FILE = "../scraper/cookies.pkl"
URL = "https://kea.jobteaser.com/en/job-offers?location=Australie%3A%3AWestern+Australia%3A%3A%3A%3ADenmark%3A%3A_zaRTaoLz4zNLdqZHB3aBLFMpQDM%3D&position_category_uuid=ddc0460c-ce0b-4d98-bc5d-d8829ff9cf11&position_category_uuid=fbab2736-0eea-4d61-899c-161eea6a2b45&locale=da&study_levels=1&study_levels=2&work_experience_code=young_graduate"

def save_cookies(driver, path):
    with open(path, "wb") as file:
        pickle.dump(driver.get_cookies(), file)
    print(f"Cookies saved to {path}")

def load_cookies(driver, path):
    with open(path, "rb") as file:
        cookies = pickle.load(file)

    current_domain = urlparse(driver.current_url).netloc
    for cookie in cookies:
        if 'expiry' in cookie:
            del cookie['expiry']
        cookie_domain = cookie.get('domain', '')
        cookie_domain_clean = cookie_domain.lstrip('.')
        if current_domain == cookie_domain_clean or current_domain.endswith('.' + cookie_domain_clean):
            try:
                driver.add_cookie(cookie)
            except Exception as e:
                print(f"Skipping cookie due to error: {e} - cookie domain: {cookie_domain}")
        else:
            print(f"Skipping cookie with mismatched domain: {cookie_domain} (current domain: {current_domain})")
    print(f"Cookies loaded from {path}")

def scrape():
    options = uc.ChromeOptions()
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-gpu")
    # options.add_argument("--headless")  # Enable for headless scraping once confirmed working

    driver = uc.Chrome(options=options)
    print("Browser launched")

    driver.get("https://kea.jobteaser.com")

    if os.path.exists(COOKIES_FILE):
        try:
            load_cookies(driver, COOKIES_FILE)
            driver.refresh()
            print("Cookies applied and page refreshed")
        except Exception as e:
            print("Failed to load cookies:", e)
    else:
        print("No cookies found, please log in manually within 60 seconds...")
        time.sleep(60)
        save_cookies(driver, COOKIES_FILE)
        print("Cookies saved, please restart the script now.")
        driver.quit()
        return []

    driver.get(URL)
    print("Target URL requested")

    # No longer waiting for captcha manually
    try:
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "div.sk-CardContainer_container__PNt2O"))
        )
    except Exception as e:
        print("Timeout waiting for job cards:", e)
        driver.quit()
        return []

    soup = BeautifulSoup(driver.page_source, 'html.parser')

    data = []
    for div in soup.select("div.sk-CardContainer_container__PNt2O"):
        try:
            companyName = div.select_one("p.JobAdCard_companyName__Ieoi3").get_text(strip=True)
            jobTitle = div.select_one("h3.JobAdCard_title__vdhrP").get_text(strip=True)
            link = "https://kea.jobteaser.com" + div.select_one("h3 a")["href"]

            data.append({
                "companyName": companyName,
                "jobTitle": jobTitle,
                "link": link
            })
        except Exception as e:
            print(f"Skipping job card due to error: {e}")
            continue

    driver.quit()
    print("Browser closed")
    return data

def send_to_backend(data):
    try:
        response = requests.post(
            "http://localhost:8080/api/scraped-jobs",
            json=data,
            headers={'Content-Type': 'application/json'}
        )
        print("✅ Data sent to backend. Status code:", response.status_code)
        print(data)
    except Exception as e:
        print("❌ Failed to send data to backend:", e)

if __name__ == "__main__":
    jobs = scrape()
    if jobs:
        print(jobs)
        send_to_backend(jobs)
