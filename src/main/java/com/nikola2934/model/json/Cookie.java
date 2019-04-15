package com.nikola2934.model.json;

//Gson POJO for puppeteer GET cookies response
public class Cookie {
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
