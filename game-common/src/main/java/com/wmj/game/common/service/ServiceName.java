package com.wmj.game.common.service;

public enum ServiceName {
    GATEWAY(1, "gateway", "网关"),
    HALL(2, "hall", "大厅"),
    //
    ;

    int id;
    String name;
    String description;

    ServiceName(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
