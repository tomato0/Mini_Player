package com.example.administrator.wplayer.models;

/**
 * 知其然，而后知其所以然
 * 倔强小指，成名在望
 * 作者： Tomato
 * on 2017/4/12 0012.
 * com.example.administrator.wplayer.models
 * 功能、作用：
 */

public class MusicType {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MusicType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
