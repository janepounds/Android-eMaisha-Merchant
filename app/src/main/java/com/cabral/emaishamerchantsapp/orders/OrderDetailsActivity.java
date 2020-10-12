package com.cabral.emaishamerchantsapp.orders;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.adapter.OrderDetailsAdapter;
import com.cabral.emaishamerchantsapp.database.DatabaseAccess;
import com.cabral.emaishamerchantsapp.pdf_report.TemplatePDF;
import com.cabral.emaishamerchantsapp.utils.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OrderDetailsActivity extends BaseActivity {


    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPrice, txtPdfReceipt;
    String order_id, order_date, order_time, customer_name;
    double total_price;
    String longText, shortText, order_status, storage_status;
    String currency;
    private RecyclerView recyclerView;
    private OrderDetailsAdapter orderDetailsAdapter;
    //how many headers or column you need, add here by using ,
    //headers and get clients para meter must be equal
    private String[] header = {"Description", "Price"};
    private TemplatePDF templatePDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        txtPdfReceipt = findViewById(R.id.txt_pdf_receipt);


        txtNoProducts = findViewById(R.id.txt_no_products);
        order_id = getIntent().getExtras().getString("order_id");
        order_date = getIntent().getExtras().getString("order_date");
        order_time = getIntent().getExtras().getString("order_time");
        customer_name = getIntent().getExtras().getString("customer_name");
        order_status = getIntent().getExtras().getString("order_status");
        storage_status = getIntent().getExtras().getString("storage_status");

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.order_details);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);


        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(order_id);

        if (orderDetailsList.size() <= 0) {
            //if no data in local db, then load data from server
            Toasty.info(OrderDetailsActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
        } else {
            orderDetailsAdapter = new OrderDetailsAdapter(OrderDetailsActivity.this, orderDetailsList);

            recyclerView.setAdapter(orderDetailsAdapter);


        }


        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();

        String shop_name = shopData.get(0).get("shop_name");
        String shop_contact = shopData.get(0).get("shop_contact");
        String shop_email = shopData.get(0).get("shop_email");
        String shop_address = shopData.get(0).get("shop_address");
        currency = shopData.get(0).get("shop_currency");


        databaseAccess.open();
        total_price = databaseAccess.totalOrderPrice(order_id);
        txtTotalPrice.setText(currency + total_price);


        //for pdf report

        shortText = "Customer Name: Mr/Mrs. " + customer_name;

        longText = "Thanks for purchase. Visit again";


        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument();
        templatePDF.addMetaData("Order Receipt", "Order Receipt", "Smart POS");
        templatePDF.addTitle(shop_name, shop_address + "\n Email: " + shop_email + "\nContact: " + shop_contact + "\nInvoice ID:" + order_id, order_time + " " + order_date);
        //templatePDF.addTitle(getDrName,"Patient Prescription",getDate);
        templatePDF.addParagraph(shortText);
        // templatePDF.addParagraph(longText);


        templatePDF.createTable(header, getClients());
        templatePDF.addRightParagraph(longText);
        templatePDF.closeDocument();


        txtPdfReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                templatePDF.viewPDF();
            }
        });


    }


    //for pdf
    private ArrayList<String[]> getClients() {
        ArrayList<String[]> rows = new ArrayList<>();

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(order_id);
        String name, price, qty, weight;
        double cost_total;

        for (int i = 0; i < orderDetailsList.size(); i++) {
            name = orderDetailsList.get(i).get("product_name");
            price = orderDetailsList.get(i).get("product_price");
            qty = orderDetailsList.get(i).get("product_qty");
            weight = orderDetailsList.get(i).get("product_weight");

            cost_total = Integer.parseInt(qty) * Double.parseDouble(price);

            rows.add(new String[]{name + "\n" + weight + "\n" + "(" + qty + "x" + currency + price + ")", currency + cost_total});


        }
        rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{currency + "  " + total_price});
//        you can add more row above format
        return rows;
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

