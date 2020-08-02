package com.cabral.emaishamerchantApp.adapter;

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

import com.cabral.emaishamerchantApp.R;
import com.cabral.emaishamerchantApp.database.DatabaseAccess;
import com.cabral.emaishamerchantApp.suppliers.EditSuppliersActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.MyViewHolder> {


    private List<HashMap<String, String>> supplierData;
    private Context context;


    public SupplierAdapter(Context context, List<HashMap<String, String>> supplierData) {
        this.context = context;
        this.supplierData = supplierData;

    }


    @NonNull
    @Override
    public SupplierAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final SupplierAdapter.MyViewHolder holder, int position) {

        final String suppliers_id = supplierData.get(position).get("suppliers_id");
        String name = supplierData.get(position).get("suppliers_name");
        String contact_perosn = supplierData.get(position).get("suppliers_contact_person");
        String cell = supplierData.get(position).get("suppliers_cell");
        String email = supplierData.get(position).get("suppliers_email");
        String address = supplierData.get(position).get("suppliers_address");
        String address_two = supplierData.get(position).get("suppliers_address_two");
        String base64Image = supplierData.get(position).get("suppliers_image");

        holder.txtSuppliersName.setText(name);
        holder.txtSupplierContactPerson.setText(contact_perosn);
        holder.txtSupplierCell.setText(context.getString(R.string.area_code)+cell);
        holder.txtSupplierEmail.setText(email);
        holder.txtSupplierAddress.setText(address);
        holder.txtSupplierAddressTwo.setText(address_two);

        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64base", base64Image);
                holder.imageSupplier.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.imageSupplier.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

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
                builder.setMessage(R.string.want_to_delete_supplier)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean deleteSupplier = databaseAccess.deleteSupplier(suppliers_id);

                                if (deleteSupplier) {
                                    Toasty.error(context, R.string.supplier_deleted, Toast.LENGTH_SHORT).show();

                                    supplierData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                } else {
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
        return supplierData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtSuppliersName, txtSupplierContactPerson, txtSupplierCell, txtSupplierEmail, txtSupplierAddress, txtSupplierAddressTwo;
        ImageView imgDelete,imgCall,  imageSupplier;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtSuppliersName = itemView.findViewById(R.id.txt_supplier_name);
            txtSupplierContactPerson = itemView.findViewById(R.id.txt_contact_person);
            txtSupplierCell = itemView.findViewById(R.id.txt_supplier_cell);
            txtSupplierEmail = itemView.findViewById(R.id.txt_supplier_email);
            txtSupplierAddress = itemView.findViewById(R.id.txt_supplier_address);
            txtSupplierAddressTwo = itemView.findViewById(R.id.txt_supplier_address_two);

            imgDelete = itemView.findViewById(R.id.img_delete);
            imgCall = itemView.findViewById(R.id.img_call);
            imageSupplier = itemView.findViewById(R.id.supplier_image);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(context, EditSuppliersActivity.class);
            i.putExtra("suppliers_id", supplierData.get(getAdapterPosition()).get("suppliers_id"));
            i.putExtra("suppliers_name", supplierData.get(getAdapterPosition()).get("suppliers_name"));
            i.putExtra("suppliers_contact_person", supplierData.get(getAdapterPosition()).get("suppliers_contact_person"));
            i.putExtra("suppliers_cell", supplierData.get(getAdapterPosition()).get("suppliers_cell"));
            i.putExtra("suppliers_email", supplierData.get(getAdapterPosition()).get("suppliers_email"));
            i.putExtra("suppliers_address", supplierData.get(getAdapterPosition()).get("suppliers_address"));
            i.putExtra("suppliers_address_two", supplierData.get(getAdapterPosition()).get("suppliers_address_two"));
            i.putExtra("suppliers_image", supplierData.get(getAdapterPosition()).get("suppliers_image"));
            context.startActivity(i);
        }
    }


}
