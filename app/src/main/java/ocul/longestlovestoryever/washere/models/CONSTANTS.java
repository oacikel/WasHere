package ocul.longestlovestoryever.washere.models;

public class CONSTANTS {

    //Refresh distance: Map will update its position once for every displacement of this value.
    public static final double MAP_REFRESH_DISTANCE = 10;
    public static String IS_AUTO_LOGIN_SELECTED = "IS_AUTO_LOGIN_SELECTED";
    public static String LAST_KNOWN_LAT = "LAST_KNOWN_LAT";
    public static String LAST_KNOWN_LNG = "LAST_KNOWN_LNG";

    public static final int MAP_REFRESH_TIME = 1000;
    public static Double RANGE_IN_METERS = 100.0;

    //Regarding Version Check
    public static String UPDATE_URL="https://play.google.com/store/apps/details?id=ocul.longestlovestoryever.washere";
    public static String UPDATE_URL_TAG="update_url";
    public static String KEY_RANGE_IN_METERS="range_in_meters";
    public static String FB_RC_KEY_TITLE = "update_title";
    public static String FB_RC_KEY_DESCRIPTION = "update_description";
    public static String FB_RC_KEY_FORCE_UPDATE_VERSION = "force_update_version";
    public static String FB_RC_KEY_LATEST_VERSION = "latest_version";

    public static void setRangeInMeters(Double rangeInMeters) {
        RANGE_IN_METERS = rangeInMeters;
    }
}
