package com.example.kurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private List<Categorie> categories = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Categorie category);
    }

    public CategoriesAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.categoryName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Categorie category = categories.get(position);
        holder.name.setText(category.getCategories());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateCategories(List<Categorie> newCategories) {
        if (newCategories != null) {
            this.categories = newCategories;
            notifyDataSetChanged();
        }
    }
}