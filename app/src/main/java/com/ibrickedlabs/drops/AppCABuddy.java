package com.ibrickedlabs.drops;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ibrickedlabs.drops.adapters.Filter;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by RajeshAatrayan on 28-08-2018.
 */

public class AppCABuddy extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //build up the default configuration
        // initialize Realm
        Realm.init(this);

        // create your Realm configuration
        RealmConfiguration realmConfiguration = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        //set the default configurtaion
        Realm.setDefaultConfiguration(realmConfiguration);

    }
    public static void save(Context context,int filterOption) {
        //Instantiating the Sharedprefernces
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor seditor = pref.edit();
        seditor.putInt("filter", filterOption);
        seditor.apply();

    }

public static  int load(Context context) {
        SharedPreferences loadPref = PreferenceManager.getDefaultSharedPreferences(context);
        int filterOptions = loadPref.getInt("filter", Filter.NONE);//value,default value
        return filterOptions;
    }
}
