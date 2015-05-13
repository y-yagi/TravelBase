package xyz.yyagi.travelbase.model;

import io.realm.RealmObject;

/**
 * Created by yaginuma on 15/05/13.
 */
public class User extends RealmObject{
    private String name;
    private String accessToken;
    private String uid;
    private String provider;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
