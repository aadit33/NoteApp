package com.noteapp.Widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noteapp.Model.CropingOption;
import com.noteapp.R;

import java.util.ArrayList;

public class CropingOptionAdapter extends ArrayAdapter {
    private ArrayList<CropingOption> mOptions;
    private LayoutInflater mInflater;

    public CropingOptionAdapter(Context context, ArrayList<CropingOption> options) {
        super(context, R.layout.croping_selecter, options);

        mOptions  = options;

        mInflater = LayoutInflater.from(context);

    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup group) {
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.croping_selecter, null);

        CropingOption item = mOptions.get(position);

        if (item != null) {
            ((ImageView) convertView.findViewById(R.id.img_icon)).setImageDrawable(item.icon);
            ((TextView) convertView.findViewById(R.id.txt_name)).setText(item.title);

            return convertView;
        }

        return null;
    }
}

