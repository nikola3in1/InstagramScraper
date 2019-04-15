package com.nikola2934;

import com.nikola2934.db.Db;
import com.nikola2934.model.entities.Crawler;
import com.nikola2934.model.entities.Follower;
import com.nikola2934.model.entities.TargetAccount;

import com.nikola2934.service.CrawlerRotationService;
import com.nikola2934.service.CrawlingWorker;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        new Main();
    }

    static {
        //Prevent logging
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);
        CrawlerRotationService crawlerRotationService = new CrawlerRotationService(Db.getInstace().read(Crawler.class));
    }

    private Main() {
        CrawlingWorker worker = new CrawlingWorker();
        worker.start();

    }

    void addCrawlers() {
        Crawler crawler = new Crawler("johnwhitegrey", "John2White", new Date(System.currentTimeMillis()));
        Crawler crawler1 = new Crawler("jennygreengrey", "Jenny2Green", new Date(System.currentTimeMillis()));
        Crawler crawler2 = new Crawler("johnjonhxster", "123testiramo321", new Date(System.currentTimeMillis()));
        Crawler crawler3 = new Crawler("imkekul", "cloudisgood1", new Date(System.currentTimeMillis()));
        Crawler crawler4 = new Crawler("dusanekecmane", "tridvajedan0", new Date(System.currentTimeMillis()));
        List<Crawler> crawlerList = new ArrayList<>();
        crawlerList.add(crawler);
        crawlerList.add(crawler1);
        crawlerList.add(crawler2);
        crawlerList.add(crawler3);
        crawlerList.add(crawler4);

        for (Crawler c : crawlerList) {
            Db.getInstace().save(c);
        }

    }

    void dbTest() {
        Follower follower = new Follower("asd", "sad", "asd", false, false,
                false, false, new HashSet<TargetAccount>());
        Follower follower2 = new Follower("as1241242d", "2223", "asd121242", false, false,
                false, false, new HashSet<TargetAccount>());


        HashSet<Follower> followers = new HashSet<>();
        followers.add(follower);

        TargetAccount targetAccount = new TargetAccount("123tet", "usernametest", "firstCursor test",
                "lastCursor test", followers);

        Db.getInstace().save(targetAccount);

        followers = new HashSet<>();
        followers.add(follower2);

        targetAccount.setFollowers(followers);

        Db.getInstace().save(targetAccount);
    }

    void getRequestTest() {
        RestTemplate restTemplate = new RestTemplate();
        final String url = "http://localhost:4000/list";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response != null) {
            System.out.println(response.getBody());
        }
    }

//    void fetcherTest(){
//        FollowerFetcherOld.getInstance().fetchSessionidCookie();
//
//        long start = System.currentTimeMillis();
//        FollowerFetcherOld.getInstance().getFollowers("19410587");
//        long end = System.currentTimeMillis() - start;
//        System.out.println("Number of followers: "+ FollowerFetcherOld.getInstance().getFollowerSet().size());
//        System.out.println("Number of accounts: "+ FollowerFetcherOld.getInstance().getNumberOfAccounts());
//        System.out.println("Number of requests per account: "+ FollowerFetcherOld.getInstance().getRequestsPerAccount());
//        System.out.println("Number of followers per request: "+ FollowerFetcherOld.getInstance().getFollowersPerRequest());
//
//
////        for (Follower f : FollowerFetcherOld.getInstance().getFollowerSet()) {
////            System.out.println(f);
////        }
////        System.out.println("///////////////////////////////////////////////////////////////////////////////");
////        System.out.println("Number of requests: 10");
////        System.out.println("Number of followers: " + FollowerFetcherOld.getInstance().getFollowerSet().size());
////        System.out.println("Time needed to crawl followers: "+end+"ms. ");
////        System.out.println("///////////////////////////////////////////////////////////////////////////////");
//
////        System.out.println("//////////////////////////////////////////");
////        System.out.println("Number of requests per account: 50");
////        System.out.println("Items in list: " + FollowerFetcherOld.getInstance().list.size());
////        System.out.println("Items in map: " + FollowerFetcherOld.getInstance().map.values().size());
//
////        System.out.println(FollowerFetcherOld.getInstance().getLastCurrsor());
//
//    }

}
