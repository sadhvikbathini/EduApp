package com.example.eduapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduapp.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.VH> {
    private List<Integer> banners; // drawable resource ids

    public BannerAdapter(List<Integer> banners) { this.banners = banners; }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        int res = banners.get(position);
        Glide.with(holder.itemView.getContext()).load(res).into(holder.img);
    }

    @Override
    public int getItemCount() { return banners.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        VH(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgBanner);
        }
    }
}
