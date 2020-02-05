package ocul.longestlovestoryever.washere.helpers;

import android.util.Log;

import com.here.android.mpa.common.PositioningManager;

import ocul.longestlovestoryever.washere.models.eLocationStatus;
import ocul.longestlovestoryever.washere.repositories.WasRepository;

public class MapHelper {

    public static String LOG_TAG = "OCUL - MapHeler";
    private WasRepository wasRepository = WasRepository.getInstance();

    public void managePositionStatus(PositioningManager positioningManager, PositioningManager.LocationStatus locationStatus) {
        if (locationStatus == PositioningManager.LocationStatus.TEMPORARILY_UNAVAILABLE) {
            wasRepository.setUpdateLocationStatus(eLocationStatus.TEMPORARILY_UNAVAILABLE);
            //Log.w(LOG_TAG, "Location Status is TEMPORARILY UNAVAILABLE");
        } else if (locationStatus == PositioningManager.LocationStatus.OUT_OF_SERVICE) {
            Log.w(LOG_TAG, "Location Status is OUT OF SERVICE");
            wasRepository.setUpdateLocationStatus(eLocationStatus.OUT_OF_SERVICE);
        } else if (locationStatus == PositioningManager.LocationStatus.AVAILABLE) {
            Log.w(LOG_TAG, "Location Status is AVAILABLE.");
            if (positioningManager.getPosition().getCoordinate().getLatitude() != -1.7976931348623157E308 && positioningManager.getPosition().getCoordinate().getLongitude() != -1.7976931348623157E308) {
                if (wasRepository.getLocationStatus().getValue() != eLocationStatus.AVAILABLE) {
                    wasRepository.setUpdateLocationStatus(eLocationStatus.AVAILABLE);
                }
            } else if (positioningManager.getPosition().getCoordinate().getLatitude() == -1.7976931348623157E308 && positioningManager.getPosition().getCoordinate().getLongitude() == -1.7976931348623157E308) {
                Log.w("OCUL - Positioning", "Wrong location pulled");
                wasRepository.setUpdateLocationStatus(eLocationStatus.WRONG_LOCATION);
            }
        }
    }

    public void startPositionManager(PositioningManager positioningManager) {
        positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
    }
}
