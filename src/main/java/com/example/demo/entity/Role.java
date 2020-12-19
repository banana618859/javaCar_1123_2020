package com.example.demo.entity;

import java.util.ArrayList;

public class Role {
    private int id;
    private String name;
    private String roleRight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getRoleRight() {
        return roleRight;
    }

    public void setRoleRight(String roleRight) {
        this.roleRight = roleRight;
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", roleRight=" + roleRight +
                '}';
    }
}
