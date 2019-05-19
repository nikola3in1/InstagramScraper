package com.nikola2934.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name ="target")
public class TargetAccount extends Model implements Serializable{
    @Id
    @Column(name ="user_id")
    private String user_id;
    @Column(name = "username")
    private String username;
    @Column(name = "first_cursor")
    private String firstCursor;
    @Column(name = "last_cursor")
    private String lastCursor;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinTable(
            name = "target_has_follower",
            joinColumns = { @JoinColumn(name = "target_user_id") },
            inverseJoinColumns = { @JoinColumn(name = "follower_id") }
    )
    Set<Follower> followers = new HashSet<>();


    public TargetAccount() {
    }

    public TargetAccount(String id, String username, String firstCursor, String lastCursor, Set<Follower> followers) {
        this.user_id = id;
        this.username = username;
        this.firstCursor = firstCursor;
        this.lastCursor = lastCursor;
        this.followers = followers;
    }

    public void addFollower(Follower follower) {
        this.getFollowers().add(follower);
    }

    public String getId() {
        return user_id;
    }

    public void setId(String id) {
        this.user_id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstCursor() {
        return firstCursor;
    }

    public void setFirstCursor(String firstCursor) {
        this.firstCursor = firstCursor;
    }

    public String getLastCursor() {
        return lastCursor;
    }

    public void setLastCursor(String lastCursor) {
        this.lastCursor = lastCursor;
    }

    public Set<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<Follower> followers) {
        this.followers = followers;
    }

    @Override
    public String toString() {
        return "TargetAccount{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", firstCursor='" + firstCursor + '\'' +
                ", lastCursor='" + lastCursor + '\'' +
                ", followers=" + followers +
                '}';
    }
}
