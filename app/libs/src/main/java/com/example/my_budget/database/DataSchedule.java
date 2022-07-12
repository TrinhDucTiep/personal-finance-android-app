package com.example.my_budget.database;

public class DataSchedule {
    private String item, date, id, note;
    private Long amount;

    public DataSchedule(){

    }

    public DataSchedule(String item, String date, String id, String note, Long amount) {
        this.item = item;
        this.date = date;
        this.id = id;
        this.note = note;
        this.amount = amount;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
