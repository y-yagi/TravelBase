package xyz.yyagi.travelbase.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by yaginuma on 15/05/13.
 */
public class User extends RealmObject{
    private String uid;
    private String provider;
    private byte[] encryptedAccessToken;
    @Ignore
    private String accessToken;

    public byte[] getEncryptedAccessToken() {
        return encryptedAccessToken;
    }

    public void setEncryptedAccessToken(byte[] encryptedAccessToken) {
        this.encryptedAccessToken = encryptedAccessToken;
    }

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
