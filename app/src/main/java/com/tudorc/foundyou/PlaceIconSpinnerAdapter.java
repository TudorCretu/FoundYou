package com.tudorc.foundyou;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Tudor C on 9/21/2017.
 */


public class PlaceIconSpinnerAdapter extends ArrayAdapter<String> {

    private Context ctx;
    private String[] contentArray;
    private Integer[] imageArray;

    public PlaceIconSpinnerAdapter(Context context, int resource, String[] objects,
                          Integer[] imageArray) {
        super(context,  R.layout.place_icon_spinner, R.id.placeType, objects);
        this.ctx = context;
        this.contentArray = objects;
        this.imageArray = imageArray;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.place_icon_spinner, null);

        }

        TextView textView = (TextView) convertView.findViewById(R.id.placeType);
        textView.setText(contentArray[position]);

        ImageView imageView = (ImageView)convertView.findViewById(R.id.placeIcon);
        imageView.setImageResource(imageArray[position]);

        return convertView;

    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.place_icon_spinner, null);

        }

        TextView textView = (TextView) convertView.findViewById(R.id.placeType);
        textView.setText(contentArray[position]);

        ImageView imageView = (ImageView)convertView.findViewById(R.id.placeIcon);
        imageView.setImageResource(imageArray[position]);

        return convertView;
    }

    @Override
    public String getItem(int position){
        return contentArray[position];
    }

}
