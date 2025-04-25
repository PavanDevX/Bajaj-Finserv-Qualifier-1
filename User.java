package com.example.bajaj.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class User {
    private int id;
    private String name;

    @JsonProperty("follows")
    private List<Integer> follows;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getFollows() {
        return follows;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFollows(List<Integer> follows) {
        this.follows = follows;
    }
}