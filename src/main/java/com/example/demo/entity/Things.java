package com.example.demo.entity;

public class Things {
    private int id;
    private String name;
    private int total;
    private int finish;
    private int unFinish;
    private Float finishRatio;
    private int owner;

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public int getUnFinish() {
        return unFinish;
    }

    public void setUnFinish(int unFinish) {
        this.unFinish = unFinish;
    }

    public Float getFinishRatio() {
        return finishRatio;
    }

    public void setFinishRatio(Float finishRatio) {
        this.finishRatio = finishRatio;
    }

    @Override
    public String toString() {
        return "Things{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", total=" + total +
                ", finish=" + finish +
                ", unFinish=" + unFinish +
                ", finishRatio=" + finishRatio +
                ", owner=" + owner +
                '}';
    }
}
