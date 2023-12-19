package com.enigma.entity;

public class Film {
    private Integer id;
    private String title;
    private Integer duration;
    private String showDate;
    private Integer price;
    private Integer ratingId;

    public Film() {
    }

    public Film(Integer id, String title, Integer duration, String showDate, Integer price, Integer ratingId) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.showDate = showDate;
        this.price = price;
        this.ratingId = ratingId;
    }

    public Film(String title, Integer duration, String showDate, Integer price, Integer ratingId) {
        this.title = title;
        this.duration = duration;
        this.showDate = showDate;
        this.price = price;
        this.ratingId = ratingId;
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", showDate='" + showDate + '\'' +
                ", price=" + price +
                ", ratingId=" + ratingId +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getRatingId() {
        return ratingId;
    }

    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }
}
