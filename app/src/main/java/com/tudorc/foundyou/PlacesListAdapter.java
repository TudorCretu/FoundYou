package com.tudorc.foundyou;

import android.content.Context;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Tudor C on 9/19/2017.
 */


public class PlacesListAdapter extends ArrayAdapter<Place> implements View.OnClickListener{

    private ArrayList<Place> dataSet;
    private Map<String,Integer> imge;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageView icon;
        ImageView info;
    }

    public PlacesListAdapter(ArrayList<Place> data, Context context, Map<String,Integer> imagesIds) {
        super(context, R.layout.place_item, data);
        this.dataSet = data;
        this.mContext=context;
        this.imge=imagesIds;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Place place=(Place) object;

        switch (v.getId())
        {
            case R.id.item_info:
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Place place = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.place_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(place.getName());
        viewHolder.icon.setImageResource(imge.get(place.getIcon()));
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);


        // Return the completed view to render on screen
        return convertView;
    }
}