package com.cabral.emaishamerchantsapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.wallet.WalletActivity;

public class AcceptPaymentsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Accept Payment");
        return inflater.inflate(R.layout.fragment_accept_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txtSubmit = view.findViewById(R.id.txt_accept_payments_submit);

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                ViewGroup viewGroup = v.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_accept_payment_pin, viewGroup, false);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                EditText input_pin = dialogView.findViewById(R.id.etxt_create_accept_payment_pin);
                TextView submit_button = dialogView.findViewById(R.id.txt_custom_accept_payment__pin);

                submit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), WalletActivity.class);
                        startActivity(intent);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        ViewGroup viewGroup = v.findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_dialog_accept_payment_success, viewGroup, false);
                        builder.setView(dialogView);
                        AlertDialog alertSuccessDialog = builder.create();
                        alertSuccessDialog.show();
                    }
                });
            }
        });

    }
}
