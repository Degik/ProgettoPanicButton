package com.example.progettopanicbutton;

import android.content.Context;
import android.location.GnssAntennaInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<InfoContactHolder> {
    private ArrayList<InfoContact> contactArrayList;
    private Context context;

    public ContactsRecyclerAdapter(ArrayList<InfoContact> contactArrayList, Context context){
        this.contactArrayList = contactArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public InfoContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //
        View photoView = inflater.inflate(R.layout.layout_list_view, parent, false);
        InfoContactHolder infoContactHolder = new InfoContactHolder(photoView);
        return infoContactHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InfoContactHolder holder, int position) {
        final int index = holder.getAdapterPosition();
        // Set Name
        holder.textViewName.setText(contactArrayList.get(position).getName());
        // Set Number
        holder.textViewNumber.setText(contactArrayList.get(position).getNumber());
        // Set Image
        Uri photo = contactArrayList.get(position).getPhoto();
        if(photo != null){
            Glide.with(context)
                    .load(photo)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }
}
