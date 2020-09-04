package com.cabral.emaishamerchantsapp.wallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.cabral.emaishamerchantsapp.Fragments.AcceptPaymentsFragment;
import com.cabral.emaishamerchantsapp.Fragments.SettlementsFragment;
import com.cabral.emaishamerchantsapp.Fragments.TransactionsFragment;
import com.cabral.emaishamerchantsapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.reactivex.annotations.NonNull;

public class WalletActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        getSupportActionBar().setTitle("Wallet");
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new TransactionsFragment());

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_transaction:
                            openFragment(new TransactionsFragment());
                            return true;
                        case R.id.navigation_accept_payments:
                            openFragment(new AcceptPaymentsFragment());
                            return true;
                        case R.id.navigation_settlements:
                            openFragment(new SettlementsFragment());
                            return true;
                    }
                    return false;
                }
            };
}