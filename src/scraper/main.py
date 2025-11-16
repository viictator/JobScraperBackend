from keascraperFinal import scrape as scrape_kea
from jobindexscraper import scrape as scrape_jobindex
import requests
import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')

def send_to_backend(data):
    try:
        response = requests.post(
            "http://localhost:8080/scraped-jobs",
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
        kea_jobs = scrape_kea()
        if kea_jobs:              # non-empty list = True
            all_jobs += kea_jobs
        else:
            print("⚠️ KEA returned no jobs")
    except Exception as e:
        print("⚠️ KEA scraper failed:", e)

    print("\n=== Scraping Jobindex ===")
    try:
        jobindex_jobs = scrape_jobindex()
        if jobindex_jobs:
            all_jobs += jobindex_jobs
        else:
            print("⚠️ Jobindex returned no jobs")
    except Exception as e:
        print("⚠️ Jobindex scraper failed:", e)

    # Only send if BOTH have results
    if kea_jobs and jobindex_jobs:
        print(f"🎉 Total scraped: {len(all_jobs)} jobs")
        send_to_backend(all_jobs)
    else:
        print("🚫 Not sending to backend — one or both scrapers returned 0 jobs.")
