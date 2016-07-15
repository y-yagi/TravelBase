package xyz.yyagi.travelbase.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import xyz.yyagi.travelbase.util.DateUtil;

/**
 * Created by yaginuma on 16/07/15.
 */
public class Event extends RealmObject{
    @PrimaryKey
    private int id;
    private String name;
    private String detail;
    private Date start_date;
    private Date end_date;
    private int place_id;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String period() {
        String period = "";
        if(start_date == end_date) {
            period = String.format("%s\n", DateUtil.format(start_date));
        } else {
            period = String.format("%s〜%s\n", DateUtil.format(start_date), DateUtil.format(end_date));
        }
        return period;
    }
}
