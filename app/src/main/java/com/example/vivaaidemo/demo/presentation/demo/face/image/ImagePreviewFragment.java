package com.example.vivaaidemo.demo.presentation.demo.face.image;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.vivaaidemo.databinding.FragmentImagePreviewBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.google.android.material.snackbar.Snackbar;


public class ImagePreviewFragment extends BaseFragment<FragmentImagePreviewBinding> {
    private static final String ARG_IMAGE = "image";

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public ImagePreviewFragment() {
        super(FragmentImagePreviewBinding::inflate);
    }
    public static ImagePreviewFragment newInstance(Uri image) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE, image.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        Bundle bundle = getArguments();
        if (bundle != null) {
            Glide.with(requireContext()).load(bundle.getString(ARG_IMAGE)).into(binding.imagePreview);
        } else {
            Snackbar.make(binding.getRoot(), "Cannot show image", Snackbar.LENGTH_SHORT).show();
        }
    }
}