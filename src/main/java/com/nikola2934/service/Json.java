package com.nikola2934.service;

import com.nikola2934.model.entities.Follower;

//Gson POJO class for extracting data from request
class Json {
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

//Gson POJO for puppeteer GET cookies response
class Cookie {
    private String name;
    private String value;
    private String expires;
    private String domain;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return name + "=" + value + "; Domain=" + domain + "; expires=" + expires + "; Path=/; Secure";
    }

}

//Gson POJO for puppeteer GET cookies response
class Cookies {
    private Cookie cookie;
    private String account;

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Cookies{" +
                "cookie=" + cookie +
                ", account='" + account + '\'' +
                '}';
    }
}






