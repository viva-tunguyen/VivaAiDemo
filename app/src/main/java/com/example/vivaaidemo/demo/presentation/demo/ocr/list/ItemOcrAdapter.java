package com.example.vivaaidemo.demo.presentation.demo.ocr.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vivaaidemo.databinding.ItemOcrBinding;

import java.util.List;

public class ItemOcrAdapter extends RecyclerView.Adapter<ItemOcrAdapter.ViewHolder> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private List<OcrViewModel.OcrType> data;
    private final LayoutInflater inflater;
    private final Callback callback;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public ItemOcrAdapter(Context context, Callback callback) {
        this.callback = callback;
        this.inflater = LayoutInflater.from(context);
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
        ItemOcrBinding itemBinding = ItemOcrBinding.inflate(inflater, parent, false);
        return new ViewHolder(itemBinding);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OcrViewModel.OcrType item = data.get(position);
        holder.binding.text.setText(item.title);
        holder.binding.getRoot().setOnClickListener(view -> {
            if (callback != null) {
                callback.onItemClick(item, position);
            }
        });
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    public void setTabs(List<OcrViewModel.OcrType> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    /* **********************************************************************
     * Inner Class
     ********************************************************************** */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOcrBinding binding;

        public ViewHolder(ItemOcrBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface Callback {
        void onItemClick(OcrViewModel.OcrType item, int position);
    }
}