package com.example.progettopanicbutton;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class InfoContactHolder extends RecyclerView.ViewHolder {
    TextView textViewName;
    TextView textViewNumber;
    ImageView imageView;
    View view;

    InfoContactHolder (View itemView){
        super(itemView);
        textViewName = (TextView) itemView.findViewById(R.id.nameView);
        textViewNumber = (TextView) itemView.findViewById(R.id.numberView);
        imageView = (ImageView) itemView.findViewById(R.id.avatarView);
        view = itemView;
    }
}
