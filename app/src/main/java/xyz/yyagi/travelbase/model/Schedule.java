package xyz.yyagi.travelbase.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yaginuma on 15/05/16.
 */
public class Schedule extends RealmObject{
    @PrimaryKey
    private int id;
    private String memo;
    private String start_time;
    private String formatted_start_time;
    private String end_time;
    private String formatted_end_time;
    private Place place;
    private Route route;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String detail) {
        this.memo = memo;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getFormatted_start_time() {
        return formatted_start_time;
    }

    public void setFormatted_start_time(String formatted_start_time) {
        this.formatted_start_time = formatted_start_time;
    }

    public String getFormatted_end_time() {
        return formatted_end_time;
    }

    public void setFormatted_end_time(String formatted_end_time) {
        this.formatted_end_time = formatted_end_time;
    }
}
