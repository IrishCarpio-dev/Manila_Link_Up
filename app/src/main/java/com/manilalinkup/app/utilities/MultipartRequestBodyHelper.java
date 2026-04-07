package com.manilalinkup.app.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultipartRequestBodyHelper {

    public static MultipartBody.Part prepareImagePart(Context context, Uri uri, String field) {
        try {
            InputStream isp = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(isp);
            isp.close();

            File file = new File(context.getCacheDir(), field + "Upload.jpg");
            FileOutputStream fos = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);

            fos.flush();
            fos.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            return MultipartBody.Part.createFormData(field, file.getName(), requestFile);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse("text/plain"), descriptionString);
    }
}
