package com.example.openvpn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.openvpn.R;
import com.example.openvpn.interfaces.ServerListItemClickListener;
import com.example.openvpn.model.Server;

import java.util.ArrayList;

public class ServerListRVAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    private final ArrayList<Server> serverLists;
    private final Context mContext;
    private final ServerListItemClickListener listener;

    public ServerListRVAdapter(ArrayList<Server> serverLists, Context context) {
        this.serverLists = serverLists;
        this.mContext = context;
        listener = (ServerListItemClickListener) context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.server_list_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.serverCountry.setText(serverLists.get(position).getCountryName());
        Glide.with(mContext)
                .load(serverLists.get(position).getFlagUrl())
                .into(holder.serverIcon);

        holder.serverItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.clickedItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serverLists.size();
    }
}
