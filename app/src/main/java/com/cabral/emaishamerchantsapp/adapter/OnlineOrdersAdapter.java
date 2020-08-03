package com.cabral.emaishamerchantsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishamerchantsapp.R;

import java.util.HashMap;
import java.util.List;

public class OnlineOrdersAdapter extends RecyclerView.Adapter<OnlineOrdersAdapter.MyViewHolder> {
    Context context;
    private List<HashMap<String, String>> orderData;

    public OnlineOrdersAdapter(Context context, List<HashMap<String, String>> orderData) {
        this.context = context;
        this.orderData = orderData;
    }

    @NonNull
    @Override
    public OnlineOrdersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_order_item, parent, false);
        return new OnlineOrdersAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineOrdersAdapter.MyViewHolder holder, int position) {
        String customer_name = orderData.get(position).get("customer_name");
        String storage_status = orderData.get(position).get("storage_status");
        String order_status = orderData.get(position).get("order_status");


            holder.txt_customer_name.setText(customer_name);
            holder.txt_order_status.setText(order_status);


    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_customer_name, txt_customer_address, txt_order_status;
        ImageView imgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);

            txt_customer_name = itemView.findViewById(R.id.txt_order_customer_name);
            txt_customer_address = itemView.findViewById(R.id.txt_order_customer_address);
            txt_order_status = itemView.findViewById(R.id.txt_order_status);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
//            Intent i = new Intent(context, OrderDetailsActivity.class);
//            i.putExtra("order_id",orderData.get(getAdapterPosition()).get("invoice_id"));
//            i.putExtra("customer_name",orderData.get(getAdapterPosition()).get("customer_name"));
//            i.putExtra("order_date",orderData.get(getAdapterPosition()).get("order_date"));
//            i.putExtra("order_time",orderData.get(getAdapterPosition()).get("order_time"));
//            i.putExtra("order_status",orderData.get(getAdapterPosition()).get("order_status"));
//            i.putExtra("storage_status",orderData.get(getAdapterPosition()).get("storage_status"));
//            context.startActivity(i);
        }
    }
}
