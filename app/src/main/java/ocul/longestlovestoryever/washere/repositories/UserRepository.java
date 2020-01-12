package ocul.longestlovestoryever.washere.repositories;

import androidx.lifecycle.MutableLiveData;

import ocul.longestlovestoryever.washere.models.User;
import ocul.longestlovestoryever.washere.models.eLoginState;
import com.google.firebase.auth.FirebaseAuth;

public class UserRepository {
    private static UserRepository instance;
    private static User currentUser;
    private FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    private MutableLiveData<eLoginState> loginState = new MutableLiveData<>();
    private MutableLiveData<Boolean> isNewUserCreated = new MutableLiveData<>();
    private String message;

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }


    //User

    public  User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        UserRepository.currentUser = currentUser;
    }

    //Firebase
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    //Authorization

    public MutableLiveData<eLoginState> getLoginState() {
        return loginState;
    }

    public void setLoginState(MutableLiveData<eLoginState> loginState) {
        this.loginState = loginState;
    }

    public MutableLiveData<Boolean> getIsNewUserCreated() {
        return isNewUserCreated;
    }

    public void setIsNewUserCreated(MutableLiveData<Boolean> isNewUserCreated) {
        this.isNewUserCreated = isNewUserCreated;
    }

    //Message

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
