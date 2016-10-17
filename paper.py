import json

with open('Paper.json', 'rt') as file:
	jsons = file.readlines()
file.close()

with open('paper.txt', 'wt') as f:
	for each in jsons:
		data = json.loads(each)
		p = data.get('result').get('title')
		if p:
			f.write(data.get('url'))
			f.write('\n')
			for e in p:
				f.write('\t')
				f.write(e)
				f.write('\n')
f.close()



#!/usr/bin/env python
# -*- encoding: utf-8 -*-
# Created on 2016-10-15 12:56:14
# Project: Paper

from pyspider.libs.base_handler import *
import re

class Handler(BaseHandler):
    crawl_config = {
    }

    @every(minutes=24 * 60)
    def on_start(self):
        self.crawl('http://www.ccf.org.cn/sites/ccf/biaodan.jsp?contentId=2903940690850', callback=self.index_page)

    @config(age=10 * 24 * 60 * 60)
    def index_page(self, response):
        for each in response.doc('a[href^="http://dblp.uni-trier.de/db/conf"]').items():
            self.crawl(each.attr.href, callback=self.conf_page)

    
    @config(priority=2)
    def conf_page(self, response):
        for each in response.doc('a[href^="http://dblp.uni-trier.de/db/conf"]').items():
            match = re.search('\d{4}',each.attr.href)
            if match:
                if int(match.group()) >= 2014:
                    self.crawl(each.attr.href, callback=self.detail_page)
            else:
                continue
    
    def detail_page(self, response):
        paper = [];
        for each in response.doc('ul.publ-list > li').items():
            papertitle = each('.title').text();
            if papertitle.find('droid') > -1:
                paper.append(papertitle)
        return{
            'title': paper
        }



