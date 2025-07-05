import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import requests
from bs4 import BeautifulSoup
import pickle
import os
from urllib.parse import urlparse

from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
from selenium.common.exceptions import StaleElementReferenceException


COOKIES_FILE = "cookies.pkl"
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





def login(driver):
    username = os.getenv("LOGIN_EMAIL")
    password = os.getenv("LOGIN_PASSWORD")
    if not username or not password:
        raise Exception("LOGIN_EMAIL and LOGIN_PASSWORD environment variables must be set")

    print("🔐 Navigating to kea.jobteaser.com...")
    driver.get("https://kea.jobteaser.com")

    # Wait for redirect to connect.jobteaser.com
    WebDriverWait(driver, 20).until(
        lambda d: "connect.jobteaser.com" in d.current_url
    )
    print("🔄 Landed on connect.jobteaser.com")

    # Click KEA login link (a-tag containing 'KEA' and ('konto' or 'account'))
    for attempt in range(5):
        try:
            print(f"🎓 Looking for KEA login link... (attempt {attempt + 1})")
            login_link = WebDriverWait(driver, 10).until(
                EC.element_to_be_clickable((
                    By.XPATH,
                    "//a[contains(text(), 'KEA') and (contains(text(), 'konto') or contains(text(), 'account'))]"
                ))
            )
            login_link.click()
            print("✅ Clicked KEA login link!")
            break
        except Exception as e:
            print(f"❌ KEA login link not ready (attempt {attempt + 1}): {e}")
            time.sleep(2)
    else:
        raise Exception("Could not find or click KEA login link.")

    # Wait for Microsoft login page to load
    WebDriverWait(driver, 20).until(
        lambda d: "login.microsoftonline.com" in d.current_url
    )
    print("➡️ On Microsoft login page")

    # Enter email
    WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.NAME, "loginfmt"))
    )
    email_input = driver.find_element(By.NAME, "loginfmt")
    email_input.clear()
    email_input.send_keys(username)
    driver.find_element(By.ID, "idSIButton9").click()  # Next
    print("➡️ Submitted email")

    # Enter password
    WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.NAME, "passwd"))
    )
    password_input = driver.find_element(By.NAME, "passwd")
    password_input.clear()
    password_input.send_keys(password)

    # Retry clicking sign-in button in case of stale element
    for attempt in range(3):
        try:
            sign_in_btn = WebDriverWait(driver, 10).until(
                EC.element_to_be_clickable((By.ID, "idSIButton9"))
            )
            time.sleep(1)  # short delay before clicking
            sign_in_btn.click()
            print("➡️ Submitted password")
            break
        except StaleElementReferenceException:
            print(f"⚠️ Stale element, retrying click attempt {attempt + 1}")
            time.sleep(1)
    else:
        raise Exception("Failed to click the sign-in button after retries")

    # Handle "Stay signed in?" prompt if it appears
    try:
        stay_signed_in_btn = WebDriverWait(driver, 5).until(
            EC.element_to_be_clickable((By.ID, "idBtn_Back"))
        )
        stay_signed_in_btn.click()
        print("➡️ Dismissed 'Stay signed in?' prompt")
    except Exception:
        print("➡️ No 'Stay signed in?' prompt appeared")

    # Wait for redirect back to kea.jobteaser.com or jobteaser domain after login
    WebDriverWait(driver, 60).until(
        lambda d: any(domain in d.current_url for domain in ["kea.jobteaser.com", "jobteaser.com"])
    )
    print("🎉 Successfully logged in and redirected!")



def scrape():
    options = uc.ChromeOptions()
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-gpu")
    # options.add_argument("--headless")  # Uncomment to run headless

    class PatchedChrome(uc.Chrome):
        def __del__(self):
            pass  # suppress undetected_chromedriver cleanup bug

    driver = PatchedChrome(options=options)
    print("Browser launched")

    driver.get("https://kea.jobteaser.com")

    if os.path.exists(COOKIES_FILE):
        try:
            load_cookies(driver, COOKIES_FILE)
            driver.refresh()
            print("Cookies applied and page refreshed")
        except Exception as e:
            print("Failed to load cookies:", e)
            print("Attempting login...")
            login(driver)
            save_cookies(driver, COOKIES_FILE)
    else:
        print("No cookies found, logging in...")
        login(driver)
        save_cookies(driver, COOKIES_FILE)

    driver.get(URL)
    print("Target URL requested")

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
