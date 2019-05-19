package com.nikola2934.service;

import com.nikola2934.db.Db;
import com.nikola2934.model.entities.Crawler;

import java.util.*;

/*CrawlerRotationService used for tracking & managing timeout of the Crawlers*/
public class CrawlerRotationService extends Thread {
    private static Integer timeoutPeriod = 1000 * 60 * 60; //one hour
    private static Queue<Crawler> availableCrawlers = null;
    //K: curr ms when put, V: Crawler; Sorted ASC
    private static SortedMap<Long, Crawler> busyCrawlers = Collections.synchronizedSortedMap(new TreeMap<Long, Crawler>());

    public CrawlerRotationService(HashSet<Crawler> crawlers) {
        availableCrawlers = new LinkedList<Crawler>(crawlers);
    }

    @Override
    public void run() {
        System.out.println("********CrawlerRotation service is running********");
        try {
            while (true) {
                if (availableCrawlers.isEmpty()) {
                    /*If there are no availableCrawlers, we sleep until the first one is ready*/
                    Crawler firstToBeReady = busyCrawlers.get(busyCrawlers.firstKey());
                    //We wait a bit more, just to be sure
                    Long shortestWait = busyCrawlers.firstKey() + 50;
                    System.out.println("CrawlerRotationService is sleeping...");
                    Thread.sleep(shortestWait - System.currentTimeMillis());
                    System.out.println("CrawlerRotationService is awakened");
                    availableCrawlers.offer(firstToBeReady);
                    busyCrawlers.remove(busyCrawlers.firstKey());

                }
                if (!busyCrawlers.isEmpty() && isTimedOut(busyCrawlers.firstKey())) {
                    /*If crawler is ready, we put it back to queue*/
                    Crawler ready = busyCrawlers.get(busyCrawlers.firstKey());
                    availableCrawlers.offer(ready);
                    busyCrawlers.remove(busyCrawlers.firstKey());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized Crawler getCrawler() {
        if (!availableCrawlers.isEmpty()) {
            Crawler ready = availableCrawlers.poll();
            busyCrawlers.put(System.currentTimeMillis() + 200 + timeoutPeriod, ready);
            return ready;
        }
        System.out.println("No available crawlers...");
        return null;
    }


    //Utill
    private boolean isTimedOut(Long timeToWait) {
        return (timeToWait + timeoutPeriod <= System.currentTimeMillis());
    }

}

