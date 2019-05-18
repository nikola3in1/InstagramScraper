package com.nikola2934.service;

import com.nikola2934.model.entities.Crawler;
import com.nikola2934.model.entities.Follower;
import com.nikola2934.model.entities.TargetAccount;
import com.nikola2934.model.json.Cookies;
import com.nikola2934.service.util.QueryHashes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

public class FollowerCrawler extends Thread {
    /*
    -----------BOUNDARIES-----------
     * 200 requests per hour...
     * 60 requests per hour for comments...
     *
    -----------COOKIES----------
    * Mandatory
      -sessionid
      -rur

      Not mandatory
      -csrftoken
      -ds_user_id
      -mcd
      -mid
      -urlgen
     ------------STATS-----------
     * Stats for one account!!!
     *
     * - Test 1 -
     * requests: 11,
     * elapsed_time:6500ms,
     * followers:549
     *
     *  - Approximations -
     * requests:200,
     * elapsed: 130sec,
     * followers: 10000
     *
     *
     * If we assume that we can chain requests/cursors (NextCursor class) and evade duplicated results, then we may consider next:
     *   About 28 account swaps can fit in one hour cycle.
     *   After one hour, cycle restarts and the first account starts again.
     *   number of followers = number of accounts * requests per account * followers per request
     *   number of followers =     28 * 200 * 50   =   ~280,000 per/h
     *   daily =     280,000 * 24    =   ~6,720,000 daily
     *
     * Confirmed!!! Cursors persist! :D
     *
     *TODO:
     *Figure out how to get this hash. Possibly from sessionid
     *Currently we get it from XHR request
     *Every query has unique id represented with this hash.
     *Hashes doesn't change over time.
     */

    //Instagram account that will be scraped
    private TargetAccount target;
    private final String COOKIE_SERVICE_URL = "http://localhost:4000/";
    private HttpHeaders cookieHeaders = new HttpHeaders();
    //default: 24, max:50

    //Settings params
    private boolean initCrawl = false;
    private Integer followersPerRequest = 50;
    private Integer requestsPerAccount = 2;
    private Integer numberOfAccounts;

    //Results?

    public FollowerCrawler(TargetAccount target, boolean initCrawl) {
        this.target = target;
        this.initCrawl = initCrawl;
    }

    @Override
    public void run() {
        try {
            if (initCrawl) {
                initialCrawl();
            } else {
                process();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void process() throws Exception {
        prepareCrawler();
        //cycle
        //for(i){}
        scrapeFollowers();
    }

    void initialCrawl() throws Exception {
        prepareCrawler();
        scrapeFirstCursor();
    }

//    private String scrapeFirstCursor() {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpEntity<String> requestEntity = new HttpEntity<>("", cookieHeaders);
//
//        System.out.println(requestEntity.getHeaders()+" <==headers");
//        ResponseEntity<String> result = restTemplate.exchange(getUrl(), HttpMethod.GET, requestEntity, String.class);
//        System.out.println(result.getHeaders());
//        System.out.println(result.toString());
//        return result.toString();
//    }


    ///TESTING, FIXING
    private String scrapeFirstCursor() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://www.instagram.com/graphql/query/?query_hash=56066f031e6239f35a904ac20c9f37d9&variables={\"id\":\"19410587\",\"include_reel\":true,\"fetch_mutual\":true,\"first\":24}";
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        System.out.println(result.getHeaders());
        System.out.println(result.toString());
        return result.toString();
    }

    private void scrapeFollowers() {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, cookieHeaders);
        ResponseEntity<String> result = restTemplate.exchange(getUrl(), HttpMethod.GET, requestEntity, String.class);
        System.out.println(result);
    }

    private void fetchSessionCookie(Crawler crawler) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        final String url = COOKIE_SERVICE_URL + "cookie";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("username", crawler.getUsername())
                .queryParam("password", crawler.getPassword());

        System.out.println(builder.toUriString()+ "<<< uri string");
        ResponseEntity<Cookies> response = restTemplate.getForEntity(builder.toUriString(), Cookies.class);
        if (Objects.requireNonNull(response.getBody()).getCookie() != null) {
            //Setting cookie
            System.out.println("Cookie fetched" + response.getBody().getCookie().toString());
            cookieHeaders.add("Cookie", response.getBody().getCookie().toString());
        } else {
            System.out.println("Failed fetching cookie");
            throw new Exception("NoCookieException");
        }
    }

    void prepareCrawler() throws Exception {
        System.out.print("Preparing crawler account...");
        Crawler crawler = CrawlerRotationService.getCrawler();
        //Fetching crawler account,
        //if not available atm, wait
        while (crawler == null) {
            crawler = CrawlerRotationService.getCrawler();
            System.out.println(crawler.getUsername());
        }
        fetchSessionCookie(crawler);
        System.out.println("done!");
    }

    //Util
    private String getUrl() {
        if (initCrawl)
            return "https://www.instagram.com/graphql/query/?query_hash=" + getFollowersQueryHash() + "&variables=%7B%22id%22%3A%22" +
                    target.getId() + "%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Atrue%2C%22first%22%3A"+24+"%7D";

        return "https://www.instagram.com/graphql/query/?query_hash=" + getFollowersQueryHash() + "&variables=%7B%22id%22%3A%22" +
                target.getId() + "%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Afalse%2C%22first%22%3A" +
                followersPerRequest + "%2C%22after%22%3A%22" + target.getLastCursor() + "%3D%3D%22%7D";
    }

    private String getFollowersQueryHash() {
        return QueryHashes.queries.get("GetFollowers");
    }

}