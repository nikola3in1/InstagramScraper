package com.nikola2934.model.json;

//Gson POJO for puppeteer GET cookies response
public class Cookies {
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
