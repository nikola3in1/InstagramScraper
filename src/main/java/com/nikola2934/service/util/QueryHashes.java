package com.nikola2934.service.util;

import java.util.HashMap;
import java.util.Map;

//Temp Dictionary for currently available request operations
//TODO: Add to db
public class QueryHashes {
    public static HashMap<String, String> queries = new HashMap<>();
    static {
        queries.put("GetFollowers", "56066f031e6239f35a904ac20c9f37d9");
    }
    public void help(){
        System.out.println("\nList of available request queries");
        for (Map.Entry<String, String> entry : queries.entrySet()) {
            System.out.println("-"+entry.getKey());
        }
        System.out.println("");
    }
}
