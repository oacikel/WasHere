package com.example.washere.Views.Fragments.Main_Button_Set_Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.washere.R;

public class MainButtonSetFragment extends Fragment implements View.OnClickListener {

    private Button buttonRecordAudio;
    private MainButtonSetFragmentViewModel mainButtonSetFragmentViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_main_button_set, container, false);
        initViews(view);
        setOnClickListeners();

        mainButtonSetFragmentViewModel = ViewModelProviders.of(this).get(MainButtonSetFragmentViewModel.class);
        mainButtonSetFragmentViewModel.init();
        //UI Items
        buttonRecordAudio.setVisibility(View.INVISIBLE);

        //Observers

        //Location Status
        mainButtonSetFragmentViewModel.getLocationStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer state) {
                if (state != null) {
                    if (state == 1) {
                        buttonRecordAudio.setVisibility(View.VISIBLE);
                    } else if (state == 2) {
                        buttonRecordAudio.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        return view;


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initViews(View view) {
        buttonRecordAudio = view.findViewById(R.id.buttonRecordAudio);
    }


    private void setOnClickListeners() {
        buttonRecordAudio.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if (view == buttonRecordAudio) {
            Log.i("OCUL -ButtonSetFragment", "Clicked on upload Was Menu Button");
            mainButtonSetFragmentViewModel.prepareRecorder();
        }
    }
}
