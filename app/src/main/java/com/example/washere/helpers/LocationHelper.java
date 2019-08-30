package com.example.washere.helpers;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.PositioningManager;

public class LocationHelper {
    private PositioningManager positioningManager;
    private MapEngine mapEngine;

    public GeoCoordinate getCurrentPosition() {
        return positioningManager.getPosition().getCoordinate();
    }
}
