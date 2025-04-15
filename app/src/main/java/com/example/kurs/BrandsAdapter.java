package com.example.kurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder> {
    private List<Brand> brands = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Brand brand);
    }

    public BrandsAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.brandName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Brand brand = brands.get(position);
        holder.name.setText(brand.getBrand1());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(brand));
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    public void updateBrands(List<Brand> newBrands) {
        if (newBrands != null) {
            this.brands = newBrands;
            notifyDataSetChanged();
        }
    }
}