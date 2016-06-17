package xyz.yyagi.travelbase.util;

import android.content.Context;

import com.facebook.android.crypto.keychain.AndroidConceal;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.CryptoConfig;
import com.facebook.crypto.Entity;
import com.facebook.crypto.keychain.KeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import xyz.yyagi.travelbase.BuildConfig;

/**
 * Created by yaginuma on 15/05/13.
 */
public class CryptoUtil {
    public static byte[] encrypt(Context context, String value) {
        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
        Crypto crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
        Entity entity = Entity.create(BuildConfig.ENCRYPT_KEY);
        byte[] encryptedValue = null;
        try {
            encryptedValue = crypto.encrypt(value.getBytes("UTF-8"), entity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedValue;
    }

    public static String decrypt(Context context, byte[] value) {
        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
        Crypto crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
        Entity entity = Entity.create(BuildConfig.ENCRYPT_KEY);
        String result = "";

        try {
            byte [] decryptedValue = crypto.decrypt(value, entity);
            result = new String(decryptedValue, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
