import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from bs4 import BeautifulSoup
import time

BASE_URL = "https://thehub.io/jobs?positionTypes=5b8e46b3853f039706b6ea72&roles=analyst&roles=backenddeveloper&roles=datascience&roles=devops&roles=engineer&roles=frontenddeveloper&roles=fullstackdeveloper&roles=mobiledevelopment&roles=qualityassurance&location=Copenhagen,%20Danmark&countryCode=DK&sorting=mostPopular"

def scrape():
    options = uc.ChromeOptions()
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-gpu")
    # options.add_argument("--headless")  # Uncomment for headless scraping

    # --- Patched Chrome to avoid UC cleanup bug ---
    class PatchedChrome(uc.Chrome):
        def __del__(self):
            pass  # Prevent undetected_chromedriver from calling quit() twice

    driver = PatchedChrome(options=options)
    print("🌐 Browser launched")

    driver.get(BASE_URL)

    # Wait for job cards to load
    try:
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "div.card.card-job-find-list"))
        )
        print("✅ Job cards loaded")
    except TimeoutException:
        print("❌ Timeout: No job cards found")
        driver.quit()
        return []

    soup = BeautifulSoup(driver.page_source, 'html.parser')
    job_cards = soup.select("div.card.card-job-find-list")

    data = []

    for job in job_cards:
        try:
            # --- Basic info from job card ---
            title_tag = job.select_one("span.card-job-find-list__position")
            title = title_tag.get_text(strip=True) if title_tag else "N/A"

            info_spans = job.select("div.bullet-inline-list span")
            company = info_spans[0].get_text(strip=True) if len(info_spans) > 0 else "N/A"
            location = info_spans[1].get_text(strip=True) if len(info_spans) > 1 else "N/A"
            contract = info_spans[2].get_text(strip=True) if len(info_spans) > 2 else "N/A"

            link_tag = job.select_one("a.card-job-find-list__link")
            link = "https://thehub.io" + link_tag["href"] if link_tag else "N/A"

            # --- Scrape description by visiting job link ---
            description = "N/A"
            if link != "N/A":
                try:
                    driver.get(link)
                    WebDriverWait(driver, 10).until(
                        EC.presence_of_element_located((By.CSS_SELECTOR, "content.text-block__content"))
                    )
                    job_soup = BeautifulSoup(driver.page_source, 'html.parser')
                    content_block = job_soup.select_one("content.text-block__content")
                    if content_block:
                        description = content_block.get_text(separator="\n", strip=True)
                except TimeoutException:
                    print(f"⚠️ Timeout loading description for job: {title}")
                except Exception as e:
                    print(f"⚠️ Error scraping description for job: {title} - {e}")

                # Small delay to avoid hammering
                time.sleep(0.5)

                # Return to main page
                driver.get(BASE_URL)
                WebDriverWait(driver, 5).until(
                    EC.presence_of_element_located((By.CSS_SELECTOR, "div.card.card-job-find-list"))
                )

            # --- Append scraped data ---
            data.append({
                "jobTitle": title or "N/A",
                "companyName": company or "N/A",
                "location": location or "N/A",
                "link": link or "N/A",
                "time": "N/A",
                "contract": contract or "N/A",
                "description": description or "N/A",
                "originSite": "TheHub"
            })

        except Exception as e:
            print(f"⚠️ Skipping job card due to error: {e}")
            continue

    driver.quit()
    print(f"🎉 Successfully scraped {len(data)} jobs from TheHub.io")
    return data
