package com.wabalub.cs65.litlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class CredentialsHandler {

    public static final String ACCESS_TOKEN_NAME = "webapi.credentials.access_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_AT = "expires_at";
    public static final String TAG = "CREDENTIALS";

    public static void setToken(Context context, String token, long expiresIn, TimeUnit unit) {
        Context appContext = context.getApplicationContext();

        long now = System.currentTimeMillis();
        long expiresAt = now + unit.toMillis(expiresIn);

        SharedPreferences sharedPref = getSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, token);
        editor.putLong(EXPIRES_AT, expiresAt);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context appContext) {
        return appContext.getSharedPreferences(ACCESS_TOKEN_NAME, Context.MODE_PRIVATE);
    }

    public static String getToken(Context context) {
        Log.e(TAG, "getting token");
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);

        String token = sharedPref.getString(ACCESS_TOKEN, null);
        long expiresAt = sharedPref.getLong(EXPIRES_AT, 0L);

        if (token == null || expiresAt < System.currentTimeMillis()) {
            return null;
        }

        Log.e(TAG, "token = " + token);
        return token;
    }
}
