package com.webexert.digitaleye.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.webexert.digitaleye.R;


public class BottomSheetPhotoPickerDialog extends BottomSheetDialogFragment {

    private ImageView ivCamera,ivGallery;
    private final BottomSheetListener bottomSheetListener;

    public BottomSheetPhotoPickerDialog(BottomSheetListener bottomSheetListener) {
        this.bottomSheetListener = bottomSheetListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(requireContext()).inflate(R.layout.dialog_bottom_sheet_photo_picker,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialization(view);

        listener();
    }

    private void listener() {

        ivCamera.setOnClickListener(v -> {
            bottomSheetListener.OnCameraClickListener(v);
            getDialog().dismiss();
        });


        ivGallery.setOnClickListener(v -> {
            bottomSheetListener.OnGalleryClickListener(v);
            getDialog().dismiss();
        });

    }

    private void initialization(View view) {

        ivCamera = view.findViewById(R.id.ivCamera);

        ivGallery = view.findViewById(R.id.ivGallery);

    }



    public interface BottomSheetListener{

        void OnCameraClickListener(View view);

        void OnGalleryClickListener(View view);

    }

}
