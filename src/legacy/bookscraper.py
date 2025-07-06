import requests
from bs4 import BeautifulSoup

def scrape():
    url = "https://books.toscrape.com/"
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')

    data = []
    for article in soup.select("article.product_pod"):
        title = article.select_one("h3 a")["title"]
        price = article.select_one(".price_color").get_text(strip=True)
        data.append({"title": title, "price": price})

    return data


def send_to_backend(data):
    response = requests.post(
        "http://localhost:8080/api/scraped-books",
        json=data,
        headers={'Content-Type': 'application/json'}
    )
    print("Status:", response.status_code)

if __name__ == "__main__":
    scraped = scrape()
    print(scraped)
    send_to_backend(scraped)
