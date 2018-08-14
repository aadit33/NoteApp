package com.noteapp.Widgets;

import android.app.Application;
import android.util.Log;

import com.orm.SugarContext;

public class AppController extends Application {

    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
    }

}
