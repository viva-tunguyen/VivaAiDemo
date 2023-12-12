package com.example.vivaaidemo.demo.presentation.demo.face.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vivaaidemo.databinding.ItemOcrBinding;

import java.util.List;

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.ViewHolder> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private List<FaceViewModel.FaceDetail> data;
    private final LayoutInflater inflater;
    private final Callback callback;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public FaceAdapter(Context context, Callback callback) {
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
        try {
            FaceViewModel.FaceDetail item = data.get(position);
            Fragment fragment = item.getFragment();
            if (fragment == null) {
                fragment = (Fragment) item.getClazz().getConstructor().newInstance();
                item.setFragment(fragment);
                data.set(position, item);
                holder.binding.text.setText(item.getTitle());
                holder.binding.icon.setImageResource(item.getIcon());
                holder.binding.getRoot().setOnClickListener(view -> {
                    if (callback != null) {
                        callback.onItemClick(item, position);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    public void setTabs(List<FaceViewModel.FaceDetail> data) {
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
        void onItemClick(FaceViewModel.FaceDetail item, int position);
    }
}