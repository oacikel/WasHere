package ocul.longestlovestoryever.washere.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ocul.longestlovestoryever.washere.Views.Fragments.Login_Fragment.LoginFragment;
import ocul.longestlovestoryever.washere.Views.Fragments.Register_Fragment.RegisterFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    private static String LOG_TAG = "OCUL - PagerAdapter";

    private int tabCount;

    public PagerAdapter(@NonNull FragmentManager fm, int tabCount) {
        super(fm, tabCount);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RegisterFragment tab1 = new RegisterFragment();
                return tab1;
            case 1:
                LoginFragment tab2 = new LoginFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
