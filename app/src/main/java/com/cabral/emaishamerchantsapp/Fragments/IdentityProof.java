package com.cabral.emaishamerchantsapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishamerchantsapp.R;
import com.kofigyan.stateprogressbar.StateProgressBar;

public class IdentityProof extends Fragment {
    String[] descriptionData = {"Personal Details", "Contact Details", "Identity Proof"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.identity_proof, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StateProgressBar stateProgressBar = view.findViewById(R.id.your_state_progress_bar_identity_proof);
        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setStateDescriptionTypeface("font/josefin_sans.xml");
        stateProgressBar.setStateNumberTypeface("font/josefin_sans.xml");

        TextView next = view.findViewById(R.id.txt_next_submit);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new FingerprintAuthentication());
            }
        });

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.open_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
