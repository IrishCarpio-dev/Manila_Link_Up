package com.manilalinkup.app.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultipartRequestBodyHelper {

    private static final int PROFILE_SIZE = 512;

    private static Bitmap cropToSquare(Bitmap original, int newSize) {
        int width = original.getWidth();
        int height = original.getHeight();
        int size = Math.min(width, height);
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        Bitmap square = Bitmap.createBitmap(original, x, y, size, size);
        return Bitmap.createScaledBitmap(square, newSize, newSize, true);
    }

    public static MultipartBody.Part prepareImagePart(Context context, Uri uri, String field) {
        try {
            InputStream isp = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(isp);
            isp.close();

            if (field.toLowerCase().contains("profile")) {
                bitmap = cropToSquare(bitmap, PROFILE_SIZE);
            }

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
    public static MultipartBody.Part prepareProfilePhotoBase64Part(Context context, Uri uri) {
        try {
            InputStream isp = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(isp);
            isp.close();

            bitmap = cropToSquare(bitmap, PROFILE_SIZE);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            String base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

            return MultipartBody.Part.createFormData("profilePhoto", base64);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse("text/plain"), descriptionString);
    }
}
