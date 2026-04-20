package com.manilalinkup.app.utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.ResponseBody;

public class ErrorUtils {
    public static void showErrorMessage(Context context, ResponseBody errorBody) {
        try {
            String errorJson = errorBody.string();
            JSONObject jObjError = new JSONObject(errorJson);

            String message = "";
            if (jObjError.has("error")) {
                message = jObjError.getString("error");
            } else if (jObjError.has("message")) {
                message = jObjError.getString("message");
            } else {
                message = "Unknown error occurred";
            }

            Log.e("ErrorUtils", message);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("ErrorUtils", "Connection error");
            Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show();
        }
    }

    public static void showThrowableError(Context context, Throwable t) {
        String errorMessage = "Unknown error";

        if (t instanceof java.net.ConnectException) {
            errorMessage = "Connection Refused";
        } else if (t instanceof java.net.SocketTimeoutException) {
            errorMessage = "Connection Timeout";
        } else if (t instanceof java.net.UnknownHostException) {
            errorMessage = "Unknown Host";
        } else {
            errorMessage = t.getMessage();
        }


        Log.e("ErrorUtils", errorMessage);
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }
}
