package ocul.longestlovestoryever.washere.helpers;

import android.os.Handler;
import android.util.Log;

import com.here.android.mpa.common.PositioningManager;

import java.lang.ref.WeakReference;

import ocul.longestlovestoryever.washere.models.CONSTANTS;
import ocul.longestlovestoryever.washere.models.eLocationStatus;
import ocul.longestlovestoryever.washere.repositories.WasRepository;

public class MapHelper {

    public static String LOG_TAG = "OCUL - MapHeler";
    private WasRepository wasRepository = WasRepository.getInstance();

    public void managePositionStatus(PositioningManager positioningManager, PositioningManager.LocationStatus locationStatus) {
        Log.w(LOG_TAG, "Location Status is "+locationStatus);
        if (locationStatus == PositioningManager.LocationStatus.TEMPORARILY_UNAVAILABLE) {
            wasRepository.setUpdateLocationStatus(eLocationStatus.TEMPORARILY_UNAVAILABLE);
        } else if (locationStatus == PositioningManager.LocationStatus.OUT_OF_SERVICE) {
            wasRepository.setUpdateLocationStatus(eLocationStatus.OUT_OF_SERVICE);
        } else if (locationStatus == PositioningManager.LocationStatus.AVAILABLE) {
            if (positioningManager.getPosition().getCoordinate().getLatitude() != -1.7976931348623157E308 && positioningManager.getPosition().getCoordinate().getLongitude() != -1.7976931348623157E308) {
                if (wasRepository.getLocationStatus().getValue() != eLocationStatus.AVAILABLE) {
                    wasRepository.setUpdateLocationStatus(eLocationStatus.AVAILABLE);
                }
            } else if (positioningManager.getPosition().getCoordinate().getLatitude() == -1.7976931348623157E308 && positioningManager.getPosition().getCoordinate().getLongitude() == -1.7976931348623157E308) {
                Log.w(LOG_TAG, "Wrong location pulled");
                wasRepository.setUpdateLocationStatus(eLocationStatus.WRONG_LOCATION);
            }
        }
    }

    public void startPositionManager(PositioningManager positioningManager) {
        positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
    }

    public void updateLocation(final PositioningManager positioningManager) {
        final Handler handler = new Handler();
        final int delay = CONSTANTS.MAP_REFRESH_TIME;

        handler.postDelayed(new Runnable() {
            public void run() {
                if (wasRepository.getCurrentLocation().getValue() == null)
                    wasRepository.setUpdateCurrentLocation(positioningManager.getPosition().getCoordinate());
                else if (wasRepository.getCurrentLocation().getValue().isValid() && positioningManager.getPosition().getCoordinate().isValid()) {
                    if (wasRepository.getCurrentLocation().getValue().distanceTo(positioningManager.getPosition().getCoordinate()) > CONSTANTS.MAP_REFRESH_DISTANCE) {
                        Log.d(LOG_TAG, "Location changed by" + CONSTANTS.MAP_REFRESH_DISTANCE + " meters...");
                        wasRepository.setUpdateCurrentLocation(positioningManager.getPosition().getCoordinate());
                    }
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
}
