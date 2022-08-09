package com.example.progettopanicbutton;

import android.app.Activity;
import android.content.Context;
import android.icu.text.IDNA;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ContactsAdapter extends ArrayAdapter<InfoContact> {
    private Context context;
    private int resources;
    private ImageLoader imageLoader;

    public ContactsAdapter(Context context, int resources, ArrayList<InfoContact> contactArrayList){
        super(context, resources, contactArrayList);
        this.context = context;
        this.resources = resources;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textViewName;
        TextView textViewNumber;
        ImageView imageView;

        String name = getItem(position).getName();
        String number = getItem(position).getNumber();
        Uri photo = getItem(position).getPhoto();

        InfoContact infoContact = new InfoContact(name,number,photo);
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resources, parent, false);

        textViewName = (TextView) convertView.findViewById(R.id.nameView);
        textViewNumber = (TextView) convertView.findViewById(R.id.numberView);
        imageView = (ImageView) convertView.findViewById(R.id.avatarImageView);

        textViewName.setText(infoContact.getName());
        textViewNumber.setText(infoContact.getNumber());
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        if(photo != null){
            imageLoader.displayImage(String.valueOf(photo), imageView);
        }

        return convertView;
    }
}
