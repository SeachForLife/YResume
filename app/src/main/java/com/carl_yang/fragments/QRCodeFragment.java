package com.carl_yang.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.carl_yang.resume.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QRCodeFragment extends Fragment {

    private ImageView qrcode_image;

    public QRCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_qrcode, container, false);
        qrcode_image= (ImageView) v.findViewById(R.id.qrcode_image);
        qrcode_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Click me");
            }
        });
        return v;
    }

    public static QRCodeFragment newInstance() {
        return new QRCodeFragment();
    }
}
