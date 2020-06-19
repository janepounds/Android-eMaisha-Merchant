package com.cabral.emaishamerchant.settings.categories;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishamerchant.R;
import com.cabral.emaishamerchant.database.DatabaseAccess;
import com.cabral.emaishamerchant.utils.BaseActivity;

import es.dmoral.toasty.Toasty;

public class AddCategoryActivity extends BaseActivity {


    EditText etxtCategoryName;
    TextView txtAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_category);

        etxtCategoryName = findViewById(R.id.etxt_category_name);
        txtAddCategory = findViewById(R.id.txt_add_category);


        txtAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String category_name = etxtCategoryName.getText().toString().trim();

                if (category_name.isEmpty()) {
                    etxtCategoryName.setError(getString(R.string.enter_category_name));
                    etxtCategoryName.requestFocus();
                } else {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddCategoryActivity.this);
                    databaseAccess.open();

                    boolean check = databaseAccess.addCategory(category_name);

                    if (check) {
                        Toasty.success(AddCategoryActivity.this, R.string.category_added_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddCategoryActivity.this, CategoriesActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        Toasty.error(AddCategoryActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });


    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
