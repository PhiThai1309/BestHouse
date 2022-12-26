package com.team5.besthouse.services;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class StoreService {
    private static final String PREFERENCES_MANAGER = "PreferenceManager";

    SharedPreferences sharedPreferences;

    public StoreService(Context context)
    {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_MANAGER, Context.MODE_PRIVATE);
    }
    public boolean storeStringValue(String key, String value)
    {
        try
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }catch (Exception e)
        {
            Log.d("AppError", e.getMessage());
            return false;
        }
    }

    public boolean containValue(String key)
    {
        return   sharedPreferences.contains(key);
    }

    public boolean storeBooleanValue(String key,boolean value)
    {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);

            return editor.commit();
        } catch (Exception e) {
            Log.d("AppError", e.getMessage());
            return false;
        }
    }

    public boolean storeIntValue(String key, int value)
    {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        } catch (Exception e) {
            Log.d ("AppError", e.getMessage());
            return false;
        }

    }

    public String getStringValue(String key)
    {
        return sharedPreferences.getString(key, null);
    }

    public boolean getBooleanValue(String key)
    {
        return sharedPreferences.getBoolean(key, false);
    }

    public int getIntValue(String key)
    {
        return sharedPreferences.getInt(key, -1);
    }

    public boolean clearTheStore()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        return editor.commit();
    }

}
