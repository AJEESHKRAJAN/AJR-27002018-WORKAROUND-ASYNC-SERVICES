package com.workaround.ajeesh.ajr_27002018_workaround_async_services.Model;

import android.media.Image;

/**
 * Package Name : com.workaround.ajeesh.ajr_27002018_workaround_async_services.Model
 * Created by ajesh on 07-03-2018.
 * Project Name : AJR-27002018-WORKAROUND-ASYNC-SERVICES
 */

public class ModelListItem {
    private String heading;
    private String description;
    private String imageUrl;


    public ModelListItem(String heading, String description, String imageUrl) {
        this.heading = heading;
        this.description = description;
        this.imageUrl = imageUrl;
    }


    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
