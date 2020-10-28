package com.cabral.emaishamerchantsapp.product;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.cabral.emaishamerchantsapp.HomeActivity;
import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.database.DatabaseAccess;
import com.cabral.emaishamerchantsapp.database.DatabaseOpenHelper;
import com.cabral.emaishamerchantsapp.models.CategoriesResponse;
import com.cabral.emaishamerchantsapp.models.Category;
import com.cabral.emaishamerchantsapp.models.Manufacturer;
import com.cabral.emaishamerchantsapp.models.ManufacturersResponse;
import com.cabral.emaishamerchantsapp.models.Product;
import com.cabral.emaishamerchantsapp.models.ProductResponse;
import com.cabral.emaishamerchantsapp.network.RetrofitClient;
import com.cabral.emaishamerchantsapp.storage.SharedPrefManager;
import com.cabral.emaishamerchantsapp.utils.BaseActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends BaseActivity {


    public static EditText etxtProductCode;
    ProgressDialog loading;
    EditText etxtProductName, etxtProductCategory, etxtProductDescription, etxtProductBuyPrice, etxtProductSellPrice, etxtProductStock, etxtProductSupplier, etxtProdcutWeightUnit, etxtProductWeight, etxtProductManufucturer;
    TextView txtAddProdcut;
    ImageView imgScanCode;
    String mediaPath, encodedImage = "N/A";
    ArrayAdapter<String> categoryAdapter, supplierAdapter, productAdapter, manufacturersAdapter;
    List<String> categoryNames, supplierNames, weightUnitNames;
    Integer selectedCategoryID;
    Integer selectedManufacturersID;
    Integer selectedProductID;
    String selectedSupplierID;
    String selectedWeightUnitID;
    String selectectedCategoryName, selectedProductName, selectedManufacturerName;
    private List<Category> categories;
    private List<Product> products;
    private List<String> catNames;
    private List<Manufacturer> manufacturers;
    private List<String> productNames;
    private List<String> manufacturersNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_product);

        etxtProductName = findViewById(R.id.etxt_product_name);
        etxtProductCode = findViewById(R.id.etxt_product_code);
        etxtProductCategory = findViewById(R.id.etxt_product_category);
        etxtProductDescription = findViewById(R.id.etxt_product_description);
        etxtProductBuyPrice = findViewById(R.id.etxt_buy_price);
        etxtProductSellPrice = findViewById(R.id.etxt_product_sell_price);
        etxtProductStock = findViewById(R.id.etxt_product_stock);
        etxtProductSupplier = findViewById(R.id.etxt_supplier);
        etxtProductManufucturer = findViewById(R.id.etxt_product_manufucturer);

        txtAddProdcut = findViewById(R.id.txt_add_product);


        Call<ManufacturersResponse> call1 = RetrofitClient
                .getInstance()
                .getApi()
                .getManufacturers();
        call1.enqueue(new Callback<ManufacturersResponse>() {
            @Override
            public void onResponse(Call<ManufacturersResponse> call, Response<ManufacturersResponse> response) {
                if (response.isSuccessful()) {
                    manufacturers = response.body().getManufacturers();
                    saveManufacturersList(manufacturers);
                    Log.d("Categories", String.valueOf(categories));

                } else {
                    Log.d("Failed", "Manufacturers Fetch failed");

                }
            }

            @Override
            public void onFailure(Call<ManufacturersResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("Failed", "Manufacturers Fetch failed");

            }
        });

        Call<CategoriesResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .getCategories();
        call.enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful()) {
                    categories = response.body().getCategories();
                    saveList(categories);
                    Log.d("Categories", String.valueOf(categories));

                } else {
                    Log.d("Failed", "Categories failed");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("Failed", "Categories failed");

            }
        });


        categoryNames = new ArrayList<>();
        supplierNames = new ArrayList<>();
        weightUnitNames = new ArrayList<>();


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddProductActivity.this);
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> productCategory, productSupplier, weightUnit;
        productCategory = databaseAccess.getProductCategory();

        //need to open database in every query to get data from local db
        databaseAccess.open();
        productSupplier = databaseAccess.getProductSupplier();


        //need to open database in every query to get data from local db
        databaseAccess.open();
        weightUnit = databaseAccess.getWeightUnit();

        for (int i = 0; i < productCategory.size(); i++) {

            // Get the ID of selected Country
            categoryNames.add(productCategory.get(i).get("category_name"));

        }

        for (int i = 0; i < productSupplier.size(); i++) {

            // Get the ID of selected supplier
            supplierNames.add(productSupplier.get(i).get("suppliers_name"));

        }

        for (int i = 0; i < weightUnit.size(); i++) {

            // Get the ID of selected weight unit
            weightUnitNames.add(weightUnit.get(i).get("weight_unit"));

        }

        etxtProductManufucturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Categories", String.valueOf(categories));
                manufacturersNames = new ArrayList<>();
                for (int i = 0; i < manufacturers.size(); i++) {
                    manufacturersNames.add(manufacturers.get(i).getManufacturer_name());
                }

                manufacturersAdapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.list_row);
                manufacturersAdapter.addAll(manufacturersNames);


                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = dialogView.findViewById(R.id.dialog_list);


                dialog_title.setText("Manufacturers");
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(manufacturersAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        manufacturersAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = manufacturersAdapter.getItem(position);

                        Integer manufacturers_id = 0;
                        String manufacturers_name = "";
                        etxtProductManufucturer.setText(selectedItem);


                        for (int i = 0; i < manufacturersNames.size(); i++) {
                            if (manufacturersNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                manufacturers_id = manufacturers.get(i).getManufacturers_id();
                                manufacturers_name = manufacturers.get(i).getManufacturer_name();
                            }
                        }


                        selectedManufacturersID = manufacturers_id;
                        selectedManufacturerName = manufacturers_name;

                        Log.d("Manufucturer_id", String.valueOf(manufacturers_id));
                    }
                });


            }
        });

        etxtProductCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Categories", String.valueOf(categories));
                catNames = new ArrayList<>();
                if (validateManufacturer()) {

                for (int i = 0; i < categories.size(); i++) {
                    catNames.add(categories.get(i).getCategories_slug());
                }

                categoryAdapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.list_row);
                categoryAdapter.addAll(catNames);


                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = dialogView.findViewById(R.id.dialog_list);


                dialog_title.setText(R.string.product_category);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(categoryAdapter);


                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        categoryAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = categoryAdapter.getItem(position);

                        Integer category_id = 0;
                        String category_name = "";
                        etxtProductCategory.setText(selectedItem);


                        for (int i = 0; i < catNames.size(); i++) {
                            if (catNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                category_id = categories.get(i).getCategories_id();
                                category_name = categories.get(i).getCategories_slug();
                            }
                        }

                        Call<ProductResponse> call = RetrofitClient
                                .getInstance()
                                .getApi()
                                .getProducts(
                                        category_id,
                                        selectedManufacturersID
                                );
                        call.enqueue(new Callback<ProductResponse>() {
                            @Override
                            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                                if (response.isSuccessful()) {
                                    products = response.body().getProducts();
                                    savePtdList(products);
                                    Log.d("Products", String.valueOf(products));

                                } else {
                                    Log.d("Product Fetch", "Product Fetch failed");

                                }
                            }

                            @Override
                            public void onFailure(Call<ProductResponse> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });


                        selectedCategoryID = category_id;
                        selectectedCategoryName = category_name;

                        Log.d("category_id", String.valueOf(category_id));
                    }
                });
            }
            }
        });

        etxtProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productNames = new ArrayList<>();
                Log.d("Products", String.valueOf(products));
                if (validateProductCategory()) {
                    for (int i = 0; i < products.size(); i++) {
                        productNames.add(products.get(i).getProducts_slug());
                    }

                    productAdapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.list_row);
                    productAdapter.addAll(productNames);


                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);

                    Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = dialogView.findViewById(R.id.dialog_list);


                    dialog_title.setText("Products");
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(productAdapter);


                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            productAdapter.getFilter().filter(charSequence);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                    final AlertDialog alertDialog = dialog.create();

                    dialog_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();


                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            alertDialog.dismiss();
                            final String selectedItem = productAdapter.getItem(position);

                            Integer product_id = 0;
                            String product_name = "";
                            etxtProductName.setText(selectedItem);


                            for (int i = 0; i < productNames.size(); i++) {
                                if (productNames.get(i).equalsIgnoreCase(selectedItem)) {
                                    // Get the ID of selected Country
                                    product_id = products.get(i).getProducts_id();
                                    product_name = products.get(i).getProducts_slug();
                                }
                            }

                            selectedProductID = product_id;
                            selectedProductName = product_name;
                            Log.d("Product ID", String.valueOf(product_id));
                        }
                    });
                }
            }
        });


        etxtProductSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supplierAdapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.list_row);
                supplierAdapter.addAll(supplierNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

//                dialog_title.setText(getString(R.string.zone));
                dialog_title.setText(R.string.suppliers);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(supplierAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        supplierAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = supplierAdapter.getItem(position);

                        String supplier_id = "0";
                        etxtProductSupplier.setText(selectedItem);


                        for (int i = 0; i < supplierNames.size(); i++) {
                            if (supplierNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                supplier_id = productSupplier.get(i).get("suppliers_id");
                            }
                        }


                        selectedSupplierID = supplier_id;

                    }
                });
            }
        });


        txtAddProdcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toasty.warning(AddProductActivity.this, "Add Product is disable in demo version. Please purchase from Codecanyon.Thank you ", Toast.LENGTH_SHORT).show();
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddProductActivity.this);
                databaseAccess.open();
                List<HashMap<String, String>> shop_information = databaseAccess.getShopInformation();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String shop_name = shop_information.get(0).get("shop_name");
                String id = shop_name.replaceAll(" ", "") + "PDT" + timestamp.toString().replaceAll(" ", "");
                Integer shop_id = SharedPrefManager.getInstance(AddProductActivity.this).getShopId();
                Integer product_id = selectedProductID;
                String product_name = etxtProductName.getText().toString().trim();
                String product_code = etxtProductCode.getText().toString().trim();
                String product_category_name = etxtProductCategory.getText().toString().trim();
                String product_category_id = selectedCategoryID + "";
                String product_buy_price = etxtProductBuyPrice.getText().toString().trim();
                String product_sell_price = etxtProductSellPrice.getText().toString().trim();
                String product_stock = etxtProductStock.getText().toString().trim();
                String product_supplier_name = etxtProductSupplier.getText().toString().trim();
                String product_supplier = selectedSupplierID;


                if (product_name == null || product_name.isEmpty()) {
                    etxtProductName.setError(getString(R.string.product_name_cannot_be_empty));
                    etxtProductName.requestFocus();
                } else if (product_code == null || product_code.isEmpty()) {
                    etxtProductCode.setError(getString(R.string.product_code_cannot_be_empty));
                    etxtProductCode.requestFocus();
                } else if (product_category_name == null || product_category_id == null || product_category_name.isEmpty() || product_category_id.isEmpty()) {
                    etxtProductCategory.setError(getString(R.string.product_category_cannot_be_empty));
                    etxtProductCategory.requestFocus();
                } else if (product_buy_price == null || product_buy_price.isEmpty()) {
                    etxtProductBuyPrice.setError(getString(R.string.product_buy_price_cannot_be_empty));
                    etxtProductBuyPrice.requestFocus();
                } else if (product_sell_price == null || product_sell_price.isEmpty()) {
                    etxtProductSellPrice.setError(getString(R.string.product_sell_price_cannot_be_empty));
                    etxtProductSellPrice.requestFocus();
                } else if (product_stock == null || product_stock.isEmpty()) {
                    etxtProductStock.setError(getString(R.string.product_stock_cannot_be_empty));
                    etxtProductStock.requestFocus();
                } else if (Integer.parseInt(product_stock) <= 0) {
                    etxtProductStock.setError("Stock should be greater than zero");
                    etxtProductStock.requestFocus();
                } else if (product_supplier_name == null || product_supplier == null || product_supplier_name.isEmpty() || product_supplier.isEmpty()) {
                    etxtProductSupplier.setError(getString(R.string.product_supplier_cannot_be_empty));
                    etxtProductSupplier.requestFocus();
                } else {

                    Call<ResponseBody> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .postProduct(
                                    id,
                                    shop_id,
                                    product_id,
                                    product_buy_price,
                                    product_sell_price,
                                    product_supplier,
                                    Integer.parseInt(product_stock)
                            );

                    ProgressDialog progressDialog = new ProgressDialog(AddProductActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                progressDialog.dismiss();
                                Log.d("Product Save", "Product successfully saved");
                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddProductActivity.this);
                                databaseAccess.open();
                                String s = null;
                                try {
                                    s = response.body().string();
                                    if (s != null) {
                                        JSONObject jsonObject = new JSONObject(s);
                                        boolean check = databaseAccess.addProduct(product_id.toString(), product_name, product_code, product_category_id, jsonObject.getJSONObject("data").getString("product_description"), product_buy_price, product_sell_price, product_stock, product_supplier, jsonObject.getJSONObject("data").getString("product_image"), jsonObject.getJSONObject("data").getString("product_weight_unit"), jsonObject.getJSONObject("data").getString("products_weight"));

                                        if (check) {
                                            Toasty.success(AddProductActivity.this, R.string.product_successfully_added, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AddProductActivity.this, ProductActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toasty.error(AddProductActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }


                            } else {
                                progressDialog.dismiss();
                                Toasty.error(AddProductActivity.this, "Failed", Toast.LENGTH_SHORT).show();


                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            Log.e("Add Product Error:", t.getMessage());

                        }
                    });


                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // When an Image is picked
            if (requestCode == 1213 && resultCode == RESULT_OK && null != data) {


                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(mediaPath);


                encodedImage = encodeImage(selectedImage);


            }


        } catch (Exception e) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
        }

    }


    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_product_menu, menu);
        return true;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_import:


                fileChooser();

                //onImport();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //import data from Excel xls file
    public void onImport(String path) {

        String directory_path = path;
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(AddProductActivity.this);
        databaseAccess.open();


        File file = new File(directory_path);
        if (!file.exists()) {
            Toast.makeText(this, R.string.no_file_found, Toast.LENGTH_SHORT).show();
            return;
        }

        // Is used to import data from excel without dropping table
//         ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(),DatabaseOpenHelper.DATABASE_NAME);

        // if you want to add column in excel and import into DB, you must drop the table
        ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, false);
        // Import EXCEL FILE to SQLite
        excelToSQLite.importFromFile(directory_path, new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(AddProductActivity.this);
                loading.setMessage(getString(R.string.data_importing_please_wait));
                loading.setCancelable(false);
                loading.show();

            }

            @Override
            public void onCompleted(String dbName) {


                Handler mHand = new Handler();
                mHand.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        loading.dismiss();
                        Toasty.success(AddProductActivity.this, R.string.data_successfully_imported, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddProductActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();


                    }
                }, 5000);


            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Log.d("Error : ", "" + e.getMessage());
                Toasty.error(AddProductActivity.this, R.string.data_import_fail, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void fileChooser() {
        new ChooserDialog(AddProductActivity.this)


                .displayPath(true)
                .withFilter(false, false, "xls") //filter file type

                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
//                        Toast.makeText(AddProductActivity.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
                        onImport(path);
                    }
                })
                // to handle the back key pressed or clicked outside the dialog:
                .withOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Log.d("CANCEL", "CANCEL");
                        dialog.cancel(); // MUST have
                    }
                })
                .build()
                .show();
    }


    public void saveList(List<Category> categories) {
        this.categories = categories;
    }

    public void savePtdList(List<Product> products) {
        this.products = products;
    }

    public void saveManufacturersList(List<Manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
    }

    public boolean validateManufacturer() {
        if (etxtProductManufucturer.getText().toString().isEmpty()) {
            etxtProductManufucturer.setError(getString(R.string.select_manufacturer));
            return false;
        } else {
            etxtProductManufucturer.setError(null);
            return true;
        }
    }

    public boolean validateProductCategory() {
        if (etxtProductCategory.getText().toString().isEmpty()) {
            etxtProductCategory.setError(getString(R.string.select_product_category));
            return false;
        } else {
            etxtProductCategory.setError(null);
            return true;
        }
    }
}



