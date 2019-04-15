package com.nikola2934.service;


import com.nikola2934.model.entities.Crawler;
import com.nikola2934.model.entities.TargetAccount;
import com.nikola2934.model.json.Cookie;
import com.nikola2934.model.json.Cookies;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

public class FollowerCrawler extends Thread{
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

    //Settings
    private Integer followersPerRequest = 50;
    private Integer requestsPerAccount = 2;
    private Integer numberOfAccounts;

    //Results

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Crawler c = CrawlerRotationService.getCrawler();
                if (c != null) {
                    fetchSessionCookie(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void process() throws Exception {
        //
        Crawler crawler = CrawlerRotationService.getCrawler();
        if (crawler != null) {
            fetchSessionCookie(crawler);
        }

    }

    private void fetchSessionCookie(Crawler crawler) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        final String url = COOKIE_SERVICE_URL+"cookie";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("username", crawler.getUsername())
                .queryParam("password", crawler.getPassword());

        ResponseEntity<Cookies> response = restTemplate.getForEntity(builder.toUriString(),Cookies.class);
        if (Objects.requireNonNull(response.getBody()).getCookie()!=null) {
            //Setting cookie
            System.out.println("Cookie fetched" + response.getBody().getCookie().toString());
            cookieHeaders.add("Cookie",response.getBody().getCookie().toString());
        }else{
            System.out.println("Failed fetching cookie");
            throw new Exception("NoCookieException");
        }
    }

    private void scrapeFollowers() {
    }



}
