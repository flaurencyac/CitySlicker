package com.codepath.cityslicker;

import android.app.Application;
import com.codepath.cityslicker.models.Place;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models
        ParseObject.registerSubclass(Place.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("4MsmWN7DI9U9qzH8D8xV95cZCPLFxdsoXxYA20UN")
                .clientKey("Z3kvJpiEajkOPuYKPEnaEB3DpQzj4X55ifk8gBas")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
