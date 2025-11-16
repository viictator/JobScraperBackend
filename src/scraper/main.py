import requests
import sys
import io

# Fix stdout encoding for special characters
sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')

# Import scrapers
from keascraperFinal import scrape as scrape_kea
from jobindexscraper import scrape as scrape_jobindex
from thehubscraper import scrape as scrape_thehub  # <-- TheHub scraper

# --- Backend sender ---
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

# --- Main execution ---
if __name__ == "__main__":
    all_jobs = []

    print("\n=== Scraping KEA ===")
    try:
        kea_jobs = scrape_kea()
        if kea_jobs:
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

    print("\n=== Scraping TheHub ===")
    try:
        thehub_jobs = scrape_thehub()
        if thehub_jobs:
            all_jobs += thehub_jobs
        else:
            print("⚠️ TheHub returned no jobs")
    except Exception as e:
        print("⚠️ TheHub scraper failed:", e)

    # Only send if all three scrapers returned results
    if kea_jobs and jobindex_jobs and thehub_jobs:
        print(f"🎉 Total scraped: {len(all_jobs)} jobs")
        send_to_backend(all_jobs)
    else:
        print("🚫 Not sending to backend — one or more scrapers returned 0 jobs.")
