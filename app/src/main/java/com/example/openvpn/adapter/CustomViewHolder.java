package com.example.openvpn.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.openvpn.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {

    LinearLayout serverItemLayout;
    ImageView serverIcon;
    TextView serverCountry;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);

        serverItemLayout = itemView.findViewById(R.id.serverItemLayout);
        serverIcon = itemView.findViewById(R.id.iconImg);
        serverCountry = itemView.findViewById(R.id.countryTv);
    }
}
