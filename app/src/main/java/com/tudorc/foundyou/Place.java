package com.tudorc.foundyou;

import android.graphics.drawable.Drawable;

/**
 * Created by Tudor C on 9/19/2017.
 */

public class Place {

    String icon;
    String name;

    public Place(String icon, String name) {
        this.icon=icon;
        this.name=name;
    }

    public  String getIcon() {return icon;
    }
    public String getName() {return name;
    }

}