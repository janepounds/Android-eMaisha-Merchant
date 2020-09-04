package com.cabral.emaishamerchantsapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.cabral.emaishamerchantsapp.HomeActivity;
import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.wallet.AccountOpeningActivity;
import com.cabral.emaishamerchantsapp.wallet.WalletActivity;

public class TransactionsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView account_opening = view.findViewById(R.id.card_account_opening);

        account_opening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountOpeningActivity.class);
                startActivity(intent);
            }
        });
    }
}
