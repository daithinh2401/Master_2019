import scrapy
from scrapy.crawler import CrawlerProcess
import os

class WikiScraper(scrapy.Spider):
    name = 'wiki_raw_data'
    def start_requests(self):
        urls = ['https://en.wikipedia.org/wiki/Data_mining']
        for url in urls:
            yield scrapy.Request(url= url, callback=self.parse)
    
    def parse(self, response):
        cwd = os.getcwd()
        page = response.url.split('/')[-2]
        file_name = 'crawled_data.txt'.format(cwd,page)

        with open(file_name, 'wb') as f:
            f.write(response.body)

        self.log('Saved {}'.format(file_name))

if __name__ == "__main__":
    process = CrawlerProcess({
    'USER_AGENT': 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)'
    })

    process.crawl(WikiScraper)
    process.start()