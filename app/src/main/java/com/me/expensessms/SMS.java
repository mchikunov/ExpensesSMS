package com.me.expensessms;

public class SMS {

    private String bank;
    private Long date;
    private Double amount;
    private String category;
    private String comment;
    private String amountString;
    private String dateString;

    public String getAmountString() {
        return amountString;
    }

    public void setAmountString(String amountString) {
        this.amountString = amountString;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public SMS(){}

    public SMS(String bank, long date, Double amount, String category, String comment) {
        this.bank = bank;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.comment = comment;
    }

    public SMS(String bank, long date, Double amount) {
        this.bank = bank;
        this.date = date;
        this.amount = amount;
    }


    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }




    @Override
    public String toString() {
        return "SMS{" +
                "bank='" + bank + '\'' +
                ", date=" + date +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
