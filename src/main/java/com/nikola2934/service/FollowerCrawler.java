package com.nikola2934.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nikola2934.model.entities.Crawler;
import com.nikola2934.model.entities.TargetAccount;
import com.nikola2934.service.util.QueryHashes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

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
    private String cookie = "";
    private Gson gson = new GsonBuilder().create();

    //Settings params
    private Integer followersPerRequest = 50; //default: 24, max:50
    private Integer requestsPerAccount = 4;
    private Integer numberOfRequests = 10; //Number of request to be made in a scraping session

    public FollowerCrawler(TargetAccount target) {
        this.target = target;
    }
    public FollowerCrawler(TargetAccount target, Integer numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
        this.target = target;
    }

    @Override
    public void run() {
        try {
            scrape();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void scrape() throws Exception {
        //Cycle
        for (int req = 0; req < numberOfRequests; req++) {
            //Set the next crawler account
            if (req % requestsPerAccount == 0){ prepareCrawler();}
            scrapeFollowers();
        }
        System.out.println("Scraping is complete!");
    }

    private void scrapeFollowers() {
        try {
            System.out.print("Scraping...");
            String response = HTTPClient.getRequest(getUrl(), this.cookie);
            Json json = gson.fromJson(response, Json.class);

            //Adding new follwers to the TargetAccount follower set
            Json.Data.User.FollowedBy.Node[] nodes = json.data.user.edge_followed_by.edges;
            for (Json.Data.User.FollowedBy.Node n : nodes) {
                target.addFollower(n.node);
            }

            //Updating the TargetAccounts lastCursor
            String lastCursor = json.data.user.edge_followed_by.page_info.end_cursor.replace("==", "");
            target.setLastCursor(lastCursor);
            System.out.println("done!");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void prepareCrawler() throws Exception {
        System.out.print("Preparing crawler account... ");
        Crawler crawler = CrawlerRotationService.getCrawler();
        //Fetching crawler account,
        //if not available atm, wait
        while (crawler == null) {
            crawler = CrawlerRotationService.getCrawler();
            System.out.println(crawler.getUsername());
        }
        System.out.print(crawler.getUsername());
        fetchSessionCookie(crawler);
        System.out.println(" done!");
    }

    private void fetchSessionCookie(Crawler crawler) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        final String url = COOKIE_SERVICE_URL + "cookie";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("username", crawler.getUsername())
                .queryParam("password", crawler.getPassword());
        ResponseEntity<Cookies> response = restTemplate.getForEntity(builder.toUriString(), Cookies.class);
        if (Objects.requireNonNull(response.getBody()).getCookie() != null) {
            //Setting cookie
            this.cookie = response.getBody().getCookie().toString();
            cookieHeaders.set("Cookie", this.cookie);
        } else {
            System.out.println("Failed fetching cookie");
            throw new Exception("NoCookieException");
        }
    }

    private String getUrl() {
        if (!target.getLastCursor().isEmpty())
            return "https://www.instagram.com/graphql/query/?query_hash=" + getFollowersQueryHash() + "&variables=%7B%22id%22%3A%22" +
                    target.getId() + "%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Afalse%2C%22first%22%3A" +
                    followersPerRequest + "%2C%22after%22%3A%22" + target.getLastCursor() + "%3D%3D%22%7D";

        //If the TargetAccount is not scraped jet.
        return "https://www.instagram.com/graphql/query/?query_hash=" + getFollowersQueryHash() + "&variables=%7B%22id%22%3A%22" +
                target.getId() + "%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Atrue%2C%22first%22%3A" + 24 + "%7D";
    }

    private String getFollowersQueryHash() {
        return QueryHashes.queries.get("GetFollowers");
    }

    static class HTTPClient {
        static String getRequest(String url, String cookie) throws IOException {
            //Making GET request
            URL requestUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) requestUrl.openConnection();
            String formatedCookie = cookie.replace("[", "").replace("]", "");
            con.addRequestProperty("Cookie", formatedCookie);
            con.setRequestMethod("GET");
            //Returning response as a String
            return getResponse(con.getInputStream());
        }

        static String getResponse(InputStream connectionInput) {
            //Gets response from InputStream
            StringBuilder response = new StringBuilder();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(connectionInput));
                String line = "";
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response.toString();
        }
    }




    ///TESTING, FIXING

//    private String scrapeFirstCursor() {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpEntity<String> requestEntity = new HttpEntity<>("", cookieHeaders);
//
//        System.out.println(requestEntity.getHeaders() + " <==headers");
//        ResponseEntity<String> result = restTemplate.exchange(getUrl(), HttpMethod.GET, requestEntity, String.class);
//        System.out.println(result.getHeaders());
//        System.out.println(result.toString());
//        return result.toString();
//    }
//
//    private void scrapeFollowers() {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpEntity<String> requestEntity = new HttpEntity<>(null, cookieHeaders);
//        ResponseEntity<String> result = restTemplate.exchange(getUrl(), HttpMethod.GET, requestEntity, String.class);
//        System.out.println(result);
//    }
}

