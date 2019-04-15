package com.nikola2934.db;

import com.nikola2934.model.entities.Crawler;

import java.util.LinkedList;
import java.util.Queue;

public class CrawlerDao{

    public Queue<Crawler> getCrawlers() {
        Queue<Crawler> crawlers = new LinkedList<Crawler>(Db.getInstace().read(Crawler.class));
        return crawlers;
    }
}
