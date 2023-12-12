package com.example.vivaaidemo.demo.presentation.demo.face.detail.checkin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vivaaidemo.databinding.ItemCheckinHistoryBinding;
import com.example.vivaaidemo.demo.common.Utility;

import java.util.List;

import vcc.viva.ai.bin.entity.face.CheckIn;

public class CheckinAdapter extends RecyclerView.Adapter<CheckinAdapter.ViewHolder> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private List<CheckIn.CheckinDetail> data;
    private final LayoutInflater inflater;
    private final Callback callback;
    private final Context context;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public CheckinAdapter(Context context, Callback callback) {
        this.callback = callback;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public int getItemCount() {
        try {
            return data.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCheckinHistoryBinding itemBinding = ItemCheckinHistoryBinding.inflate(inflater, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckIn.CheckinDetail item = data.get(position);
        String date = Utility.millisecondToString(item.time);

        Glide.with(context).load(item.link).into(holder.binding.avatar);

        holder.binding.code.setText(item.employeeCode);
        holder.binding.name.setText(item.name);
        holder.binding.time.setText(date);
        holder.binding.getRoot().setOnClickListener(view -> {
            if (callback != null) {
                callback.onItemClick(item, position);
            }
        });
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<CheckIn.CheckinDetail> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    /* **********************************************************************
     * Inner Class
     ********************************************************************** */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCheckinHistoryBinding binding;

        public ViewHolder(ItemCheckinHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface Callback {
        void onItemClick(CheckIn.CheckinDetail item, int position);
    }
}