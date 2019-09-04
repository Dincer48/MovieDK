package com.example.dincerkizildere.fixmovie.data;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.dincerkizildere.fixmovie.R;
import com.example.dincerkizildere.fixmovie.database.ViewHolder;
import com.example.dincerkizildere.fixmovie.detail.ItemResult;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FilmAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<ItemResult> list = new ArrayList<>();

    public FilmAdapter() {

    }
    public void clearAll() {
        list.clear();
        notifyDataSetChanged();
    }

    public void repleaceAll(List<ItemResult> items) {
        list.clear();
        list = items;
        notifyDataSetChanged();
    }

    public void updateData(List<ItemResult> items) {
        list.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.activity_detail_item,parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));

    }

    @Override
    public int getItemCount() {
       return list.size();
    }


}