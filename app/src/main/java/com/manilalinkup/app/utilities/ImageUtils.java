package com.manilalinkup.app.utilities;

import android.util.Base64;

public class ImageUtils {
    public static byte[] decodeBase64Safe(String base64) {
        try {
            return Base64.decode(base64, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
