package ocul.longestlovestoryever.washere.Views.Activities.User_Entrance_Activity;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import ocul.longestlovestoryever.washere.helpers.VersionHelper;

public class UserEntranceActivityViewModel extends AndroidViewModel {
    private static String LOG_TAG = "OCUL - UserEntranceActivityVM";
    private Context context;

    public UserEntranceActivityViewModel(@NonNull Application application) {
        super(application);
    }
}
