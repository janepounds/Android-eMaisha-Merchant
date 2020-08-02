package com.cabral.emaishamerchantsapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.customers.EditCustomersActivity;
import com.cabral.emaishamerchantsapp.database.DatabaseAccess;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {


    private List<HashMap<String, String>> customerData;
    private Context context;


    public CustomerAdapter(Context context, List<HashMap<String, String>> customerData) {
        this.context = context;
        this.customerData = customerData;

    }


    @NonNull
    @Override
    public CustomerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CustomerAdapter.MyViewHolder holder, int position) {

        final String customer_id=customerData.get(position).get("customer_id");
        String name=customerData.get(position).get("customer_name");
        String cell=customerData.get(position).get("customer_cell");
        String email=customerData.get(position).get("customer_email");
        String address=customerData.get(position).get("customer_address");
        String addressTwo=customerData.get(position).get("customer_address_two");
        String base64Image = customerData.get(position).get("customer_image");

        holder.txtCustomerName.setText(name);
        holder.txtCell.setText(context.getString(R.string.area_code)+" "+cell);
        holder.txtEmail.setText(email);
        holder.txtAddress.setText(address);
        holder.txtAddressTwo.setText(addressTwo);

        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64base", base64Image);
                holder.imageCustomer.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.imageCustomer.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String phone = "tel:" + cell;
                callIntent.setData(Uri.parse(phone));
                context.startActivity(callIntent);
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.want_to_delete_customer)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean deleteCustomer=databaseAccess.deleteCustomer(customer_id);

                                if (deleteCustomer)
                                {
                                    Toasty.error(context, R.string.customer_deleted, Toast.LENGTH_SHORT).show();

                                    customerData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                }

                                else
                                {
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }
                                dialog.cancel();

                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform Your Task Here--When No is pressed
                                dialog.cancel();
                            }
                        }).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtCustomerName, txtCell, txtEmail, txtAddress, txtAddressTwo;
        ImageView imgDelete,imgCall, imageCustomer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCustomerName = itemView.findViewById(R.id.txt_customer_name);
            txtCell = itemView.findViewById(R.id.txt_cell);
            txtEmail = itemView.findViewById(R.id.txt_email);
            txtAddress = itemView.findViewById(R.id.txt_address);
            txtAddressTwo = itemView.findViewById(R.id.txt_address_two);
            imgDelete = itemView.findViewById(R.id.img_delete);
            imgCall = itemView.findViewById(R.id.img_call);
            imageCustomer = itemView.findViewById(R.id.customer_image);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditCustomersActivity.class);
            i.putExtra("customer_id", customerData.get(getAdapterPosition()).get("customer_id"));
            i.putExtra("customer_name", customerData.get(getAdapterPosition()).get("customer_name"));
            i.putExtra("customer_cell", customerData.get(getAdapterPosition()).get("customer_cell"));
            i.putExtra("customer_email", customerData.get(getAdapterPosition()).get("customer_email"));
            i.putExtra("customer_address", customerData.get(getAdapterPosition()).get("customer_address"));
            i.putExtra("customer_address_two", customerData.get(getAdapterPosition()).get("customer_address_two"));
            i.putExtra("customer_image", customerData.get(getAdapterPosition()).get("customer_image"));
            context.startActivity(i);
        }
    }



}
