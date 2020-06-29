package com.PlankAssistant;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesOperations {
    Context context;
    SharedPreferences sPrefs;
    private static final String DATEPATTERN = "dd-MM-yyyy";
    private static final String TRAININGDAY = "TRAININGDAY";
    private static final String PREFERENCES = "PREFERENCES";
    private static final String BEGINNINGTIME = "BEGINNINGTIME";
    private static final String PLUSTIME = "PLUSTIME";


 SharedPreferencesOperations(Context context)   {
     this.context=context;
     sPrefs = context.getSharedPreferences(PREFERENCES,MODE_PRIVATE);
 }

 void putLong(String key,Long value){
     SharedPreferences.Editor e= sPrefs.edit();
     e.putLong(key,value);
     e.apply();

 }

}
