package com.test.diary4life.Activities.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.test.diary4life.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    Button btnQuote;
    public static TextView txtQuote;

    private AdView mAdView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });





        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        mAdView = (AdView)root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        btnQuote = root.findViewById(R.id.btnQuote);
        txtQuote = root.findViewById(R.id.txtQuote);

        btnQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchQuoteData process = new fetchQuoteData();
                process.execute();

            }
        });

        final TextView textView = root.findViewById(R.id.text_slideshow);

        settingsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

}
