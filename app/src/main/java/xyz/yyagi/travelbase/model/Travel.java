package xyz.yyagi.travelbase.model;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yaginuma on 15/05/06.
 */
public class Travel extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private Date start_date;
    private Date end_date;
    private String formatted_start_date;
    private String formatted_end_date;
    private String memo;
    private RealmList<TravelDate> travel_dates;
    private String user_id;

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

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getFormatted_start_date() {
        return formatted_start_date;
    }

    public void setFormatted_start_date(String formatted_start_date) {
        this.formatted_start_date = formatted_start_date;
    }

    public String getFormatted_end_date() {
        return formatted_end_date;
    }

    public void setFormatted_end_date(String formatted_end_date) {
        this.formatted_end_date = formatted_end_date;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public RealmList<TravelDate> getTravel_dates() {
        return travel_dates;
    }

    public void setTravel_dates(RealmList<TravelDate> travel_dates) {
        this.travel_dates = travel_dates;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

