package com.gzt.proje;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.RecViewHolder>{
    ArrayList<Product> products;

    public RecAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public RecAdapter.RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.final_view, parent, false);
        return new RecAdapter.RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecAdapter.RecViewHolder holder, int position) {
        Product product = products.get(position);
        Picasso.get().load(product.image).into(holder.pImage);
        holder.pTitle.setText(product.title);
        holder.pPrice.setText(product.price);
        holder.pSeller.setText(product.seller);
        holder.pButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(product.link));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class RecViewHolder extends RecyclerView.ViewHolder {
        ImageView pImage;
        TextView pTitle, pPrice, pSeller;
        Button pButton;
        public RecViewHolder(@NonNull View itemView) {
            super(itemView);
            pImage = itemView.findViewById(R.id.pImage);
            pTitle = itemView.findViewById(R.id.pTitle);
            pPrice = itemView.findViewById(R.id.pPrice);
            pSeller = itemView.findViewById(R.id.pSeller);
            pButton = itemView.findViewById(R.id.pButton);
        }
    }
}

