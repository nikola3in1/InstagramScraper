//package com.nikola2934.service;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.nikola2934.model.entities.Follower;
//import com.nikola2934.service.json.Cookie;
//import com.nikola2934.service.json.Cookies;
//import com.nikola2934.service.Json;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//
//public class FollowerFetcherOld {
//
//    /*
//     * 200 requests per hour...
//     * 60 requests per hour for comments...
//     * */
//
//    /*
//    Cookie list
//    * Mandatory
//      -sessionid
//      -rur
//
//      Not mandatory
//      -csrftoken
//      -ds_user_id
//      -mcd
//      -mid
//      -urlgen
//    */
//
//    //Stats
//    /*
//     * Stats for one account!!!
//     *
//     * - Test 1 -
//     * requests: 11,
//     * elapsed_time:6500ms,
//     * followers:549
//     *
//     *  - Approximations -
//     * requests:200,
//     * elapsed: 130sec,
//     * followers: 10000
//     *
//     *
//     * If we assume that we can chain requests/cursors (NextCursor class) and evade duplicated results, then we may consider next:
//     *   About 28 account swaps can fit in one hour cycle.
//     *   After one hour, cycle restarts and the first account starts again.
//     *   number of followers = number of accounts * requests per account * followers per request
//     *   number of followers =     28 * 200 * 50   =   ~280,000 per/h
//     *   daily =     280,000 * 24    =   ~6,720,000 daily
//     *
//     * Confirmed!!! Cursors persist! :D
//     *
//     * */
//
//
//    /*Figure out how to get this hash. Possibly from sessionid
//    *Currently we get it from XHR request
//    *Every query has unique id represented with this hash.
//    *Hashes doesn't change over time.
//    */
//    //
//    private String query_hash = "56066f031e6239f35a904ac20c9f37d9";
//
//    //Main cookie
//    private String sessionidCookie;
//    final private int numberOfAccounts = 1;
//
//    //Cursor holder
//    private String lastCursor;
//
//    //Without cursor fetching
////    final private int requestsPerAccount = 200 - 1;
//    final private int requestsPerAccount = 2;
//
//
//    //Number of followers per request, Max = 50
//    final private int followersPerRequest = 50;
//
//    private HashSet<Follower> followerSet = new HashSet<>();
//
//    //Test
//    ArrayList<Follower> list = new ArrayList<>();
//    HashMap<String, Follower> map = new HashMap<>();
//    //
//
//    boolean init(String query_hash) {
//
//        if (this.query_hash.isEmpty()) {
//            this.query_hash = query_hash;
//            return true;
//        }
//        return false;
//    }
//
//    void fetchSessionidCookie() {
//        try {
//            System.out.println("Fetching sessionid cookie");
//            URL url = new URL("http://localhost:4000/next");
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("GET");
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String line = "";
//            StringBuffer response = new StringBuffer();
//
//            while ((line = in.readLine()) != null) {
//                response.append(line);
//            }
//
//            Gson gson = new GsonBuilder().create();
//            Cookies cookiesJson = gson.fromJson(response.toString(), Cookies.class);
//            Cookie cookie = null;
//
//            //looking for the sessionid cookie
//            for (Cookie c : cookiesJson.cookies) {
//                if (c.name.equals("sessionid")) {
//                    cookie = c;
//                }
//            }
//
//            //formating cookie data to string
//            String newCookie = cookie.name + "=" + cookie.value + "; Domain=" + cookie.domain + "; expires=" + cookie.expires + "; Path=/; Secure";
//            sessionidCookie = newCookie;
//
//            System.out.println("Cookie fetched. "+sessionidCookie);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    void getFollowers(String userId) {
//        try {
//            //24 is default, max is 50
//            String newUrl = "https://www.instagram.com/graphql/query/?query_hash=" + query_hash + "&variables=%7B%22id%22%3A%2219410587%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Afalse%2C%22first%22%3A" + followersPerRequest + "%2C%22after%22%3A%22QVFERFpHX0RGM2Joc0xWQlM2S0llSlBEcGcxOXBUbURHMGVQc0NGY1E2UGVJWkgxYzdpSGFJZU1ka25DVVlpc0pGQ3VYUk9oN2lPZ1I0ekUyYWd0SVd6Ng%3D%3D%22%7D";
//            URL url = new URL(newUrl);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.addRequestProperty("Cookie", sessionidCookie);
//            con.setRequestMethod("GET");
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            StringBuffer response = new StringBuffer();
//            String line = "";
//            while ((line = in.readLine()) != null) {
//                response.append(line);
//            }
//
//            Gson gson = new GsonBuilder().create();
//            Json json = gson.fromJson(response.toString(), Json.class);
//
//            Json.Data.User.FollowedBy.Node[] nodes = json.data.user.edge_followed_by.edges;
//
//            for (Json.Data.User.FollowedBy.Node n : nodes) {
//                followerSet.add(n.node);
//
//                //TEST
////                list.add(n.node);
////                map.putIfAbsent(n.node.username, n.node);
//            }
//            System.out.println("first request: " + followerSet.size());
//
//            lastCursor = json.data.user.edge_followed_by.page_info.end_cursor;
//            getFollowers(json.data.user.edge_followed_by.page_info, 0, 0);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    //Recursive? :/
//    void getFollowers(Json.Data.User.FollowedBy.NextCursor cursor, int counter, int accountNr) {
//
//        if (!cursor.has_next_page) {
//            System.out.println("No next page...");
//            return;
//        }
//
//        if (counter == requestsPerAccount) {
//            accountNr++;
//            if (accountNr == numberOfAccounts - 1) {
//                System.out.println("\nAll accounts were used.");
//                return;
//            }
//            fetchSessionidCookie();
//            System.out.println("Switching account. Account number: "+accountNr);
//            counter = 0;
//        }
//
//
//        try {
//            String cursorStr = cursor.end_cursor.replace("==", "");
//            String newUrl = "https://www.instagram.com/graphql/query/?query_hash=" + query_hash + "&variables=%7B%22id%22%3A%2219410587%22%2C%22include_reel%22%3Atrue%2C%22fetch_mutual%22%3Afalse%2C%22first%22%3A50%2C%22after%22%3A%22" + cursorStr + "%3D%3D%22%7D";
////            System.out.println("Sending request number: " + counter);
//            URL url = new URL(newUrl);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.addRequestProperty("Cookie", sessionidCookie);
//            con.setRequestMethod("GET");
//
//            System.out.print(counter+", ");
//            if(counter%10==0){
//                System.out.println("");
//            }
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            StringBuffer response = new StringBuffer();
//            String line = "";
//            while ((line = in.readLine()) != null) {
//                response.append(line);
//            }
//
//            Gson gson = new GsonBuilder().create();
//            Json json = gson.fromJson(response.toString(), Json.class);
//
//            Json.Data.User.FollowedBy.Node[] nodes = json.data.user.edge_followed_by.edges;
//            for (Json.Data.User.FollowedBy.Node n : nodes) {
//                followerSet.add(n.node);
//                //TEST
////                list.add(n.node);
////                map.putIfAbsent(n.node.username, n.node);
//            }
//
//            getFollowers(json.data.user.edge_followed_by.page_info, counter + 1, accountNr);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //Field Methods
//    private FollowerFetcherOld() {
//    }
//
//    private static FollowerFetcherOld ourInstance = new FollowerFetcherOld();
//
//    public static FollowerFetcherOld getInstance() {
//        return ourInstance;
//    }
//
//    public int getFollowersPerRequest() {
//        return followersPerRequest;
//    }
//
//    public int getNumberOfAccounts() {
//        return numberOfAccounts;
//    }
//
//    public int getRequestsPerAccount() {
//        return requestsPerAccount;
//    }
//
//    public String getQuery_hash() {
//        return query_hash;
//    }
//
//    public void setQuery_hash(String query_hash) {
//        this.query_hash = query_hash;
//    }
//
//    public HashSet<Follower> getFollowerSet() {
//        return followerSet;
//    }
//
//    public String getLastCursor() {
//        return lastCursor;
//    }
//
//    public void setLastCursor(String lastCursor) {
//        this.lastCursor = lastCursor;
//    }
//}
//
