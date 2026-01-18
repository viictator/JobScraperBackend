import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import StaleElementReferenceException, TimeoutException
from bs4 import BeautifulSoup
import pickle
import os
from urllib.parse import urlparse
import time

COOKIES_FILE = "cookies.pkl"
URL = "https://ek.jobteaser.com/en/job-offers?abroad_only=false&lat=55.68672426010366&lng=12.570072346106372&localized_location=Copenhagen&radius=30&saved_search=true&locale=da&locale=en&location=Denmark%3A%3A%3A%3A%3A%3ACopenhagen%3A%3A_ZUypNDmddaF7ZbS2ZHo1eFF3ib4%3D&position_category_uuid=ddc0460c-ce0b-4d98-bc5d-d8829ff9cf11&study_levels=1&study_levels=2&work_experience_code=young_graduate"

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

    print("🔐 Navigating to ek.jobteaser.com...")
    driver.get("https://ek.jobteaser.com")

    WebDriverWait(driver, 30).until(
        lambda d: "connect.jobteaser.com" in d.current_url
    )
    print("🔄 Landed on connect.jobteaser.com")

    # Click EK login link
    for attempt in range(5):
        try:
            login_link = WebDriverWait(driver, 30).until(
                EC.element_to_be_clickable(
                    (By.XPATH, "//a[contains(text(), 'EK') and (contains(text(), 'konto') or contains(text(), 'account'))]")
                )
            )
            login_link.click()
            break
        except Exception as e:
            print(f"❌ EK login link not ready (attempt {attempt + 1}): {e}")
            time.sleep(2)
    else:
        raise Exception("Could not find or click EK login link.")

    WebDriverWait(driver, 20).until(
        lambda d: "login.microsoftonline.com" in d.current_url
    )
    print("➡️ On Microsoft login page")

    email_input = WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.NAME, "loginfmt"))
    )
    email_input.clear()
    email_input.send_keys(username)

    next_btn = WebDriverWait(driver, 10).until(
        EC.element_to_be_clickable((By.ID, "idSIButton9"))
    )
    next_btn.click()
    print("➡️ Submitted email")

    password_input = WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.NAME, "passwd"))
    )
    password_input.clear()
    password_input.send_keys(password)

    for attempt in range(3):
        try:
            sign_in_btn = WebDriverWait(driver, 10).until(
                EC.element_to_be_clickable((By.ID, "idSIButton9"))
            )
            time.sleep(1)
            sign_in_btn.click()
            break
        except StaleElementReferenceException:
            time.sleep(1)

    try:
        stay_signed_in_btn = WebDriverWait(driver, 5).until(
            EC.element_to_be_clickable((By.ID, "idBtn_Back"))
        )
        stay_signed_in_btn.click()
    except Exception:
        pass

    WebDriverWait(driver, 60).until(
        lambda d: any(domain in d.current_url for domain in ["ek.jobteaser.com", "jobteaser.com"])
    )
    print("🎉 Logged in!")

def scrape():
    options = uc.ChromeOptions()
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-gpu")
    options.add_argument("--start-maximized")
    options.add_argument("--window-size=1280,800")
    # options.add_argument("--headless")  # Uncomment to run headless

    class PatchedChrome(uc.Chrome):
        def __del__(self):
            pass

    driver = PatchedChrome(options=options)
    print("🌐 Browser launched")

    driver.get("https://ek.jobteaser.com")

    if os.path.exists(COOKIES_FILE):
        try:
            load_cookies(driver, COOKIES_FILE)
            driver.refresh()
        except Exception:
            login(driver)
            save_cookies(driver, COOKIES_FILE)
    else:
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
    job_cards = soup.select("div.sk-CardContainer_container__PNt2O")

    data = []

    for div in job_cards:
        try:
            companyName = div.select_one("p.JobAdCard_companyName__7vp_H").get_text(strip=True)
            jobTitle = div.select_one("h3.JobAdCard_title__l2BSO").get_text(strip=True)
            link = "https://ek.jobteaser.com" + div.select_one("h3 a")["href"]
            time_text = div.select_one("time").get_text(strip=True)
            contract = div.select_one("div.JobAdCard_contractInfo__8S_AD span").get_text(strip=True)
            location = div.select_one('div[data-testid="jobad-card-location"] span').get_text(strip=True)

            # --- Visit job detail page for description ---
            description = "N/A"
            if link:
                try:
                    driver.get(link)
                    WebDriverWait(driver, 10).until(
                        EC.presence_of_element_located((By.CSS_SELECTOR, "div.Description_content__Ais4T"))
                    )
                    job_soup = BeautifulSoup(driver.page_source, 'html.parser')
                    content_block = job_soup.select_one("div.Description_content__Ais4T")
                    if content_block:
                        # Add spacing before and after headings
                        for h in content_block.find_all(["h2", "h3", "h4"]):
                            h.insert_before("\n\n")
                            h.insert_after("\n")
                        # Prepend bullets and add line breaks for <li>
                        for li in content_block.find_all("li"):
                            li.insert_before("• ")
                            li.append("\n")
                        description = content_block.get_text(separator="\n", strip=True)
                except TimeoutException:
                    print(f"⚠️ Timeout loading description for job: {jobTitle}")
                except Exception as e:
                    print(f"⚠️ Error scraping description for job: {jobTitle} - {e}")
                time.sleep(0.5)
                driver.get(URL)
                WebDriverWait(driver, 5).until(
                    EC.presence_of_element_located((By.CSS_SELECTOR, "div.sk-CardContainer_container__PNt2O"))
                )

            data.append({
                "jobTitle": jobTitle or "N/A",
                "companyName": companyName or "N/A",
                "location": location or "N/A",
                "link": link or "N/A",
                "time": time_text or "N/A",
                "contract": contract or "N/A",
                "description": description or "N/A",
                "originSite": "EK Jobportal"
            })

        except Exception as e:
            print(f"Skipping job card due to error: {e}")
            continue

    driver.quit()
    print("🎉 Successfully scraped KEA jobs with descriptions")
    return data
