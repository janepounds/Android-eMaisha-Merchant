package com.cabral.emaishamerchant.product;

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
import com.cabral.emaishamerchant.HomeActivity;
import com.cabral.emaishamerchant.R;
import com.cabral.emaishamerchant.database.DatabaseAccess;
import com.cabral.emaishamerchant.database.DatabaseOpenHelper;
import com.cabral.emaishamerchant.models.CategoriesResponse;
import com.cabral.emaishamerchant.models.Category;
import com.cabral.emaishamerchant.models.Product;
import com.cabral.emaishamerchant.models.ProductResponse;
import com.cabral.emaishamerchant.network.RetrofitClient;
import com.cabral.emaishamerchant.storage.SharedPrefManager;
import com.cabral.emaishamerchant.utils.BaseActivity;
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


    ProgressDialog loading;
    private List<Category> categories;
    private List<Product> products;

    public static EditText etxtProductCode;
    EditText etxtProductName, etxtProductCategory, etxtProductDescription, etxtProductBuyPrice, etxtProductSellPrice, etxtProductStock, etxtProductSupplier, etxtProdcutWeightUnit, etxtProductWeight;
    TextView txtAddProdcut, txtChooseImage;
    ImageView imgProduct, imgScanCode;
    String mediaPath, encodedImage = "N/A";
    ArrayAdapter<String> categoryAdapter, supplierAdapter, weightUnitAdapter, productAdapter;
    List<String> categoryNames, supplierNames, weightUnitNames;
    Integer selectedCategoryID;
    Integer selectedProductID;
    String selectedSupplierID;
    String selectedWeightUnitID;
    private List<String> catNames;
    private List<String> productNames;
    String selectectedCategoryName, selectedProductName;


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
        etxtProdcutWeightUnit = findViewById(R.id.etxt_product_weight_unit);
        etxtProductWeight = findViewById(R.id.etxt_product_weight);

        txtAddProdcut = findViewById(R.id.txt_add_product);
        imgProduct = findViewById(R.id.image_product);
        imgScanCode = findViewById(R.id.img_scan_code);
        txtChooseImage = findViewById(R.id.txt_choose_image);

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

        imgScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProductActivity.this, ScannerViewActivity.class);
                startActivity(intent);
            }
        });


        txtChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddProductActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
            }
        });

        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddProductActivity.this, ImageSelectActivity.class);
                intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                startActivityForResult(intent, 1213);
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


        etxtProductCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Categories", String.valueOf(categories));
                catNames= new ArrayList<>();
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
                                        category_id
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
        });

        etxtProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productNames = new ArrayList<>();
                Log.d("Products", String.valueOf(products));
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


        etxtProdcutWeightUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightUnitAdapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.list_row);
                weightUnitAdapter.addAll(weightUnitNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddProductActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

//                dialog_title.setText(getString(R.string.zone));
                dialog_title.setText(R.string.product_weight_unit);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(weightUnitAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        weightUnitAdapter.getFilter().filter(charSequence);
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
                        final String selectedItem = weightUnitAdapter.getItem(position);

                        String weight_unit_id = "0";
                        etxtProdcutWeightUnit.setText(selectedItem);


                        for (int i = 0; i < weightUnitNames.size(); i++) {
                            if (weightUnitNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                weight_unit_id = weightUnit.get(i).get("weight_id");
                            }
                        }


                        selectedWeightUnitID = weight_unit_id;

                        Log.d("weight_unit", selectedWeightUnitID);
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
                String id = shop_name+"PDT"+timestamp.toString();
                Integer shop_id = SharedPrefManager.getInstance(AddProductActivity.this).getShopId();
                Integer product_id = selectedProductID;
                String product_name = etxtProductName.getText().toString();
                String product_code = etxtProductCode.getText().toString();
                String product_category_name = etxtProductCategory.getText().toString();
                String product_category_id = selectedCategoryID + "";
                String product_description = etxtProductDescription.getText().toString();
                String product_buy_price = etxtProductBuyPrice.getText().toString();
                String product_sell_price = etxtProductSellPrice.getText().toString();
                String product_stock = etxtProductStock.getText().toString();
                String product_supplier_name = etxtProductSupplier.getText().toString();
                String product_supplier = selectedSupplierID;
                String product_Weight_unit_name = etxtProdcutWeightUnit.getText().toString();
                String product_weight_unit_id = selectedWeightUnitID;
                String product_weight = etxtProductWeight.getText().toString();


                if (product_name == null || product_name.isEmpty()) {
                    etxtProductName.setError(getString(R.string.product_name_cannot_be_empty));
                    etxtProductName.requestFocus();
                } else if (product_code == null || product_code.isEmpty()) {
                    etxtProductCode.setError(getString(R.string.product_code_cannot_be_empty));
                    etxtProductCode.requestFocus();
                } else if (product_category_name == null || product_category_id == null || product_category_name.isEmpty() || product_category_id.isEmpty()) {
                    etxtProductCategory.setError(getString(R.string.product_category_cannot_be_empty));
                    etxtProductCategory.requestFocus();
                } else if (product_description == null || product_description.isEmpty()) {
                    etxtProductDescription.setError(getString(R.string.product_description_cannot_be_empty));
                    etxtProductDescription.requestFocus();
                } else if (product_buy_price == null || product_buy_price.isEmpty()) {
                    etxtProductBuyPrice.setError(getString(R.string.product_buy_price_cannot_be_empty));
                    etxtProductBuyPrice.requestFocus();
                } else if (product_sell_price == null || product_sell_price.isEmpty()) {
                    etxtProductSellPrice.setError(getString(R.string.product_sell_price_cannot_be_empty));
                    etxtProductSellPrice.requestFocus();
                } else if (product_Weight_unit_name == null || product_weight == null || product_Weight_unit_name.isEmpty() || product_weight.isEmpty()) {
                    etxtProductWeight.setError(getString(R.string.product_weight_cannot_be_empty));
                    etxtProductWeight.requestFocus();
                } else if (product_stock == null || product_stock.isEmpty()) {
                    etxtProductStock.setError(getString(R.string.product_stock_cannot_be_empty));
                    etxtProductStock.requestFocus();
                }else if (Integer.parseInt(product_stock)<=0) {
                    etxtProductStock.setError("Stock should be greater than zero");
                    etxtProductStock.requestFocus();
                } else if (product_supplier_name == null || product_supplier == null || product_supplier_name.isEmpty() || product_supplier.isEmpty()) {
                    etxtProductSupplier.setError(getString(R.string.product_supplier_cannot_be_empty));
                    etxtProductSupplier.requestFocus();
                } else {
                    Log.d("id",id);
                    Log.d("product id", String.valueOf(product_id));
                    Log.d("shop_id", String.valueOf(shop_id));
                    Log.d("product buy price",product_buy_price);
                    Log.d("product sell price",product_sell_price);
                    Log.d("product supplier",product_supplier);
                    Log.d("product stock",product_stock);
                    Log.d("product weight",product_weight);
                    Log.d("product weight unit id",product_weight_unit_id);
                    Log.d("product weight",product_weight);
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
                                    product_stock,
                                    encodedImage,
                                    product_weight_unit_id,
                                    product_weight


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
                                boolean check = databaseAccess.addProduct(product_id.toString(), product_name, product_code, product_category_id, product_description, product_buy_price, product_sell_price, product_stock, product_supplier, encodedImage, product_weight_unit_id, product_weight);

                                if (check) {
                                    Toasty.success(AddProductActivity.this, R.string.product_successfully_added, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddProductActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {


                                    Toasty.error(AddProductActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                progressDialog.dismiss();
                                String s = null;
                                if(s!=null){
                                    try {
                                         s = response.errorBody().string();
                                        Log.d("Response",s);
                                        JSONObject jsonObject = new JSONObject(s);
                                        Toasty.error(AddProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        Log.d("Error Response", jsonObject.getString("message"));
                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }

                                }else{
                                    Toasty.error(AddProductActivity.this, "An error Occurred", Toast.LENGTH_SHORT).show();
                                    Log.d("Error Response", String.valueOf(response.errorBody()));
                                    Log.d("Error Response Code", String.valueOf(response.code()));
                                }


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
                imgProduct.setImageBitmap(selectedImage);

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

    public void getCategories() {
        Call<CategoriesResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .getCategories();
        call.enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful()) {
                    categories = response.body().getCategories();
                    Log.d("Categories", String.valueOf(categories));

                } else {
                    Log.d("Failed", "Categories failed");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {

            }
        });
    }

    public void saveList(List<Category> categories) {
        this.categories = categories;
    }

    public void savePtdList(List<Product> products) {
        this.products = products;
    }


}



