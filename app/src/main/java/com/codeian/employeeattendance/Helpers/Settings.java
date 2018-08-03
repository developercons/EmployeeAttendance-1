package com.codeian.employeeattendance.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.codeian.employeeattendance.MyApp;

public enum Settings {
    INSTANCE;

    private Context appContext;

    private Settings() {

    }

    SharedPreferences sharedPreferences;

    // Handle Api Base.
    public void setApiUrl(String apiUrl) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        sharedPreferences.edit().putString("api_base", apiUrl).apply();
    }

    public String getApiUrl() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        return sharedPreferences.getString("api_base","");
    }

    // Handle Admin Password
    public void setAdminPass(String adminPass) {
        if(adminPass.isEmpty()){
            adminPass = Hash.genHash("101101");
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        sharedPreferences.edit().putString("admin_pass_hash", adminPass).apply();
    }

    public String getAdminPass() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
        return sharedPreferences.getString("admin_pass_hash","");
    }


    public Context getAppContext() {
        appContext = MyApp.getContext();
        return appContext;
    }
}
