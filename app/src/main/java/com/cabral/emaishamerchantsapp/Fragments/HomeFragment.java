package com.cabral.emaishamerchantsapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.wallet.AccountOpeningActivity;
import com.cabral.emaishamerchantsapp.wallet.BalanceInquiryActivity;
import com.cabral.emaishamerchantsapp.wallet.DepositsActivity;
import com.cabral.emaishamerchantsapp.wallet.SettlementActivity;
import com.cabral.emaishamerchantsapp.wallet.TransferFundsActivity;
import com.cabral.emaishamerchantsapp.wallet.WithdrawActivity;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView card_account_opening = view.findViewById(R.id.card_account_opening);
        CardView card_deposits = view.findViewById(R.id.card_deposists);
        CardView card_withdraws = view.findViewById(R.id.card_withdraws);
        LinearLayout linear_transfer = view.findViewById(R.id.linear_transfer);
        LinearLayout linear_settle = view.findViewById(R.id.linear_settle);
        CardView card_balance_inquiry = view.findViewById(R.id.card_balance_inquiry);

        card_account_opening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountOpeningActivity.class);
                startActivity(intent);
            }
        });


        card_deposits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DepositsActivity.class);
                startActivity(intent);

            }
        });

        card_withdraws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WithdrawActivity.class);
                startActivity(intent);
            }
        });

        linear_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransferFundsActivity.class);
                startActivity(intent);

            }
        });
        linear_settle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettlementActivity.class);
                startActivity(intent);

            }
        });

        card_balance_inquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BalanceInquiryActivity.class);
                startActivity(intent);
            }
        });
    }


}
