package com.cabral.emaishamerchantsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.database.DatabaseAccess;
import com.cabral.emaishamerchantsapp.wallet.WalletActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.cabral.emaishamerchantsapp.utils.MultiLanguageApp.getContext;

public class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.MyViewHolder> {


    MediaPlayer player;
    private List<HashMap<String, String>> productData;
    private Context context;


    public PosProductAdapter(Context context, List<HashMap<String, String>> productData) {
        this.context = context;
        this.productData = productData;
        player = MediaPlayer.create(context, R.raw.delete_sound);

    }


    @NonNull
    @Override
    public PosProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PosProductAdapter.MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);

        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        final String product_id = productData.get(position).get("product_id");
        String name = productData.get(position).get("product_name");
        final String product_weight = productData.get(position).get("product_weight");
        final String product_price = productData.get(position).get("product_sell_price");
        final String weight_unit_name = productData.get(position).get("product_weight_unit");
        String base64Image = productData.get(position).get("product_image");


        databaseAccess.open();
//        final String weight_unit_name = databaseAccess.getWeightUnitName(weight_unit_id);

        holder.txtProductName.setText(name + product_weight);
//        holder.txtWeight.setText(product_weight + " " + weight_unit_name);
        holder.txtPrice.setText(currency + " " + product_price);


        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64base", base64Image);
                holder.product_image.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.product_image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }



        holder.product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Log.d("w_id", weight_unit_i+" ");
                databaseAccess.open();

                int check = databaseAccess.addToCart(product_id, product_weight, weight_unit_name, product_price, 1);

                if (check == 1) {
                    Toasty.success(context, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                    player.start();
                } else if (check == 2) {

                    Toasty.info(context, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();

                } else {
                    Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtWeight, txtPrice;
       // Button btnAddToCart;
        ImageView product_image, imgDelete;
        CardView cardProductItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtWeight = itemView.findViewById(R.id.txt_weight);
            txtPrice = itemView.findViewById(R.id.txt_product_sell_price_value);
            product_image = itemView.findViewById(R.id.product_image);
            cardProductItem = itemView.findViewById(R.id.card_product_item);
            imgDelete = itemView.findViewById(R.id.img_delete);
            imgDelete.setVisibility(View.GONE);
           // btnAddToCart = itemView.findViewById(R.id.btn_add_cart);

            cardProductItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    ViewGroup viewGroup = v.findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_pos_product_details, viewGroup, false);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    TextView cancel = dialogView.findViewById(R.id.btn_cancel);
                    TextView update = dialogView.findViewById(R.id.btn_update);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alertDialog.dismiss();
                        }
                    });
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                        }
                    });
                }
            });

        }
    }


}
