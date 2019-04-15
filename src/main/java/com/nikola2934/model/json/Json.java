package com.nikola2934.model.json;

import com.nikola2934.model.entities.Follower;

//Gson POJO class for extracting data from request
public class Json {
    Data data;

    class Data {
        User user;

        class User {
            FollowedBy edge_followed_by;

            class FollowedBy {
                //Number followers
                int count;
                NextCursor page_info;
                Node[] edges;

                class NextCursor {
                    boolean has_next_page;
                    String end_cursor;
                }

                class Node {
                    Follower node;
                }
            }
        }
    }
}
