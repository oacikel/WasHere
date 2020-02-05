package ocul.longestlovestoryever.washere.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;


import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import ocul.longestlovestoryever.washere.BuildConfig;

import static ocul.longestlovestoryever.washere.models.CONSTANTS.FB_RC_KEY_DESCRIPTION;
import static ocul.longestlovestoryever.washere.models.CONSTANTS.FB_RC_KEY_FORCE_UPDATE_VERSION;
import static ocul.longestlovestoryever.washere.models.CONSTANTS.FB_RC_KEY_LATEST_VERSION;
import static ocul.longestlovestoryever.washere.models.CONSTANTS.FB_RC_KEY_TITLE;
import static ocul.longestlovestoryever.washere.models.CONSTANTS.UPDATE_URL;
import static ocul.longestlovestoryever.washere.models.CONSTANTS.UPDATE_URL_TAG;

public class VersionHelper {

    private static String LOG_TAG = "OCUL - VersionHelper";

    // To get the version code from the auto generated file
    final String versionCode = Integer.toString(BuildConfig.VERSION_CODE);
    // Hashmap which contains the default values for all the parameter defined in the remote config server
    final HashMap<String, Object> versionMap = new HashMap<>();
    // Instance to access the remote config parameters
    FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private Context context;
    private String title, description, forceUpdateVersion, latestAppVersion,updateUrl;

    public VersionHelper(Activity context) {
        this.context = context;
        versionMap.put(FB_RC_KEY_TITLE, "Update Available");
        versionMap.put(FB_RC_KEY_DESCRIPTION, "A new version of the application is available please click below to update the latest version.");
        versionMap.put(FB_RC_KEY_FORCE_UPDATE_VERSION, "" + versionCode);
        versionMap.put(FB_RC_KEY_LATEST_VERSION, "" + versionCode);
        versionMap.put(UPDATE_URL_TAG,UPDATE_URL);
        // To set the default values for the remote config parameters
        mFirebaseRemoteConfig.setDefaults(versionMap);
    }


    public void manageVersionStatusFromBackend() {

        Task<Void> fetchTask = mFirebaseRemoteConfig.fetch(BuildConfig.DEBUG ? 0 : TimeUnit.HOURS.toSeconds(4));

        fetchTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    mFirebaseRemoteConfig.activateFetched();
                    title = mFirebaseRemoteConfig.getString(FB_RC_KEY_TITLE);
                    description = mFirebaseRemoteConfig.getString(FB_RC_KEY_DESCRIPTION);
                    forceUpdateVersion = mFirebaseRemoteConfig.getString(FB_RC_KEY_FORCE_UPDATE_VERSION);
                    latestAppVersion = mFirebaseRemoteConfig.getString(FB_RC_KEY_LATEST_VERSION);
                    updateUrl=mFirebaseRemoteConfig.getString(UPDATE_URL_TAG);
                    String a =mFirebaseRemoteConfig.getString("latest_version");
                    Log.i(LOG_TAG, "Fetched Data:");
                    Log.i(LOG_TAG,"Title: "+title+"\nDescription: "+description+"\nForce Update Version: "+forceUpdateVersion+"\n Latest App Version: "+latestAppVersion+"\nUpdate URL: "+updateUrl);


                    versionMap.put(FB_RC_KEY_TITLE, title);
                    versionMap.put(FB_RC_KEY_DESCRIPTION, description);
                    versionMap.put(FB_RC_KEY_FORCE_UPDATE_VERSION, forceUpdateVersion);
                    versionMap.put(FB_RC_KEY_LATEST_VERSION, latestAppVersion);
                    versionMap.put(UPDATE_URL_TAG,updateUrl);

                    if (isUpdateNecessary(versionCode,versionMap.get(FB_RC_KEY_FORCE_UPDATE_VERSION).toString())){
                        showUpdateNeededAlert();
                    }
                } else {
                    Log.e(LOG_TAG, "Fetching Data failed");
                    if (isUpdateNecessary(versionCode,versionMap.get(FB_RC_KEY_FORCE_UPDATE_VERSION).toString())){
                        showUpdateNeededAlert();
                    }
                }
            }
        });
    }

    public void showUpdateNeededAlert() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore(updateUrl);
                            }
                        }).setNegativeButton("No, thanks",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);

                            }
                        }).create();
        dialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private boolean isUpdateNecessary(String deviceVersion, String fetchedVerison) {
        if (deviceVersion.equals(fetchedVerison)) {
            return false;
        } else {
            return true;
        }
    }


}
