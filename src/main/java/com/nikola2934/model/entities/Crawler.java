package com.nikola2934.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "crawler")
public class Crawler extends Model implements Serializable{
    @Id
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "last_crawl")
    private Date lastCrawl;

    public Crawler(String username) {
        this.username = username;
    }

    public Crawler(String username, String password, Date lastCrawl) {
        this.username = username;
        this.password = password;
        this.lastCrawl = lastCrawl;
    }

    public Crawler(Crawler crawler) {
        this.username = crawler.username;
        this.password = crawler.password;
        this.lastCrawl = crawler.lastCrawl;
    }

    public Crawler() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLastCrawl() {
        return lastCrawl;
    }

    public void setLastCrawl(Date lastCrawl) {
        this.lastCrawl = lastCrawl;
    }

    @Override
    public String toString() {
        return "Crawler{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", lastCrawl=" + lastCrawl +
                '}';
    }
}
