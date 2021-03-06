package com.codepath.cityslicker;

import android.app.Application;
import com.codepath.cityslicker.models.Spot;
import com.codepath.cityslicker.models.Trip;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Spot.class);
        ParseObject.registerSubclass(Trip.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("4MsmWN7DI9U9qzH8D8xV95cZCPLFxdsoXxYA20UN")
                .clientKey("Z3kvJpiEajkOPuYKPEnaEB3DpQzj4X55ifk8gBas")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
