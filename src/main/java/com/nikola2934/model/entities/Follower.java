package com.nikola2934.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="follower")
public class Follower extends Model implements Serializable{
    @Id
    @Column(name = "id")
    private String id;
    @Column(name="username")
    private String username;
    @Column(name="full_name")
    private String full_name;
    @Column(name="is_private")
    private boolean is_private;
    @Column(name="is_verified")
    private boolean is_verified;
    @Column(name="followed_by_viewer")
    private boolean followed_by_viewer;
    @Column(name="requested_by_viewer")
    private boolean requested_by_viewer;

    @ManyToMany(mappedBy = "followers")
    private Set<TargetAccount> following= new HashSet<>();

    public Follower() {
    }

    public Follower(String id, String username, String full_name, boolean is_private, boolean is_verified, boolean followed_by_viewer, boolean requested_by_viewer, Set<TargetAccount> following) {
        this.id = id;
        this.username = username;
        this.full_name = full_name;
        this.is_private = is_private;
        this.is_verified = is_verified;
        this.followed_by_viewer = followed_by_viewer;
        this.requested_by_viewer = requested_by_viewer;
        this.following = following;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public boolean isIs_private() {
        return is_private;
    }

    public void setIs_private(boolean is_private) {
        this.is_private = is_private;
    }

    public boolean isIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    public boolean isFollowed_by_viewer() {
        return followed_by_viewer;
    }

    public void setFollowed_by_viewer(boolean followed_by_viewer) {
        this.followed_by_viewer = followed_by_viewer;
    }

    public boolean isRequested_by_viewer() {
        return requested_by_viewer;
    }

    public void setRequested_by_viewer(boolean requested_by_viewer) {
        this.requested_by_viewer = requested_by_viewer;
    }

    public Set<TargetAccount> getFollowing() {
        return following;
    }

    public void setFollowing(Set<TargetAccount> following) {
        this.following = following;
    }

    @Override
    public String toString() {
        return "Follower{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", full_name='" + full_name + '\'' +
                ", is_private=" + is_private +
                ", is_verified=" + is_verified +
                ", followed_by_viewer=" + followed_by_viewer +
                ", requested_by_viewer=" + requested_by_viewer+ "}";
    }
}
