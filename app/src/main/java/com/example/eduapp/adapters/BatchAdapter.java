package com.example.eduapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduapp.R;
import com.example.eduapp.model.Batch;

import java.util.ArrayList;
import java.util.List;

public class BatchAdapter extends RecyclerView.Adapter<BatchAdapter.VH> {
    private List<Batch> originalList;
    private List<Batch> displayedList;
    private OnItemClickListener listener;

    private String filterQuery = "";
    private String filterCategory = "All";

    public interface OnItemClickListener { void onItemClick(Batch batch); }

    public BatchAdapter(List<Batch> list, OnItemClickListener l) {
        this.originalList = list;
        this.displayedList = new ArrayList<>(list);
        this.listener = l;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_batch, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Batch b = displayedList.get(position);
        holder.tvTitle.setText(b.getTitle());
        holder.tvCategory.setText(b.getCategory());
        holder.tvNewBadge.setVisibility(b.isNew() ? View.VISIBLE : View.GONE);
        Glide.with(holder.itemView.getContext()).load(b.getImageRes()).into(holder.img);
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onItemClick(b); });
    }

    @Override
    public int getItemCount() {
        return displayedList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvTitle, tvCategory, tvNewBadge;
        VH(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgBatch);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvNewBadge = itemView.findViewById(R.id.tvNewBadge);
        }
    }

    // call this to update filters
    public void setFilters(String query, String category) {
        this.filterQuery = query != null ? query.toLowerCase().trim() : "";
        this.filterCategory = category != null ? category : "All";
        applyFilters();
    }

    private void applyFilters() {
        displayedList.clear();
        for (Batch b : originalList) {
            boolean matchesCategory = filterCategory.equals("All") || b.getCategory().equalsIgnoreCase(filterCategory);
            boolean matchesQuery = filterQuery.isEmpty() || b.getTitle().toLowerCase().contains(filterQuery);
            if (matchesCategory && matchesQuery) displayedList.add(b);
        }
        notifyDataSetChanged();
    }
}
