package com.example.kurs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {
    private Context context;
    private List<Brand> brands;

    public BrandAdapter(Context context, List<Brand> brands) {
        this.context = context;
        this.brands = brands;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand brand = brands.get(position);
        holder.brandNameTextView.setText(brand.getBrand1());
        Glide.with(context)
                .load(brand.getImgBrand())
                .into(holder.brandImageView);
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    static class BrandViewHolder extends RecyclerView.ViewHolder {
        ImageView brandImageView;
        TextView brandNameTextView;

        BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            brandImageView = itemView.findViewById(R.id.brandImageView);
            brandNameTextView = itemView.findViewById(R.id.brandNameTextView);
        }
    }
}