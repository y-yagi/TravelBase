package xyz.yyagi.travelbase.util;

import android.content.Context;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import xyz.yyagi.travelbase.BuildConfig;

/**
 * Created by yaginuma on 15/05/13.
 */
public class CryptoUtil {
    public static byte[] encrypt(Context context, String value) {
        Crypto crypto = new Crypto(new SharedPrefsBackedKeyChain(context), new SystemNativeCryptoLibrary());
        String key = BuildConfig.ENCRYPT_KEY;
        byte[] encryptedValue = null;

        try {
            encryptedValue = crypto.encrypt(value.getBytes("utf-8"), new Entity(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedValue;
    }

    public static String decrypt(Context context, byte[] value) {
        Crypto crypto = new Crypto(new SharedPrefsBackedKeyChain(context), new SystemNativeCryptoLibrary());
        String key = BuildConfig.ENCRYPT_KEY;
        String result = "";

        try {
            byte [] decryptedValue = crypto.decrypt(value, new Entity(key));
            result = new String(decryptedValue, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
