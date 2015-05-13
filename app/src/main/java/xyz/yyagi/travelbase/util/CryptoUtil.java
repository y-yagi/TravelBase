package xyz.yyagi.travelbase.util;

import android.content.Context;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

/**
 * Created by yaginuma on 15/05/13.
 */
public class CryptoUtil {
    public static String encrypt(String value, Context context) {
        Crypto crypto = new Crypto(new SharedPrefsBackedKeyChain(context), new SystemNativeCryptoLibrary());
        String key = "";
        String encryptedValue = "";

        try {
            byte[] cipherText = crypto.encrypt(value.getBytes("utf-8"), new Entity(key));
            encryptedValue = new String(cipherText, "UTF-8");

            // 暗号化されたbyte[]を復号化して、UTF8で文字列に戻す
            byte[] decrypted = crypto.decrypt(cipherText, new Entity(key));
            String data = new String(decrypted, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedValue;
    }
}
