from keascraperFinal import scrape as scrape_kea
from jobindexscraper import scrape as scrape_jobindex
import requests
import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')

def send_to_backend(data):
    try:
        response = requests.post(
            "http://localhost:8080/api/scraped-jobs",
            json=data,
            headers={'Content-Type': 'application/json'}
        )
        print("✅ Sent to backend. Status code:", response.status_code)
    except Exception as e:
        print("❌ Error sending to backend:", e)

if __name__ == "__main__":
    all_jobs = []

    print("\n=== Scraping KEA ===")
    try:
        all_jobs += scrape_kea()
    except Exception as e:
        print("⚠️ KEA scraper failed:", e)

    print("\n=== Scraping Jobindex ===")
    try:
        all_jobs += scrape_jobindex()
    except Exception as e:
        print("⚠️ Jobindex scraper failed:", e)

    if all_jobs:
        print(f"🎉 Total scraped: {len(all_jobs)} jobs")
        send_to_backend(all_jobs)
    else:
        print("🚫 No jobs scraped.")
