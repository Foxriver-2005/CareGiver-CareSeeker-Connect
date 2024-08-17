package com.care.careme.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.care.careme.Models.Patient_Phone_No;

public class Care_Seeker_Session_Management {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String SHARED_PREF_NAME="session";
    private String SESSION_KEY="session_user";

    public Care_Seeker_Session_Management(Context context) {
        sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public  void saveSession(Patient_Phone_No patientPhoneNo){

        String phone_no= patientPhoneNo.getPhone_no();
        editor.putString(SESSION_KEY,phone_no).commit();

    }

    public String getSession(){

        return sharedPreferences.getString(SESSION_KEY, String.valueOf(-1));

    }

    public  void removeSession(){
        editor.putString(SESSION_KEY, String.valueOf(-1)).commit();
    }
}
