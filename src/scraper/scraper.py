import requests
from bs4 import BeautifulSoup

def scrape():
    url = "https://books.toscrape.com/"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')

    data = []
    for item in soup.select("article.product_pod h3 a"):
        title = item['title']  # <a title="The Title">
        data.append({"title": title})

    return data


def send_to_backend(data):
    response = requests.post(
        "http://localhost:8080/api/scraped-data",
        json=data,
        headers={'Content-Type': 'application/json'}
    )
    print("Status:", response.status_code)

if __name__ == "__main__":
    scraped = scrape()
    send_to_backend(scraped)
