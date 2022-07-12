package com.example.my_budget.customSpinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.my_budget.R;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter {
    public CustomAdapter(@NonNull Context context, ArrayList<CustomItem> customList) {
        super(context, 0 , customList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }
        CustomItem item =(CustomItem) getItem(position);
        ImageView spinnerIv = convertView.findViewById(R.id.ivSpinnerLayout);
        TextView spinnerTv = convertView.findViewById(R.id.tvSpinnerLayout);
        if (item != null) {
            spinnerIv.setImageResource(item.getSpinnerItemImage());
            spinnerTv.setText(item.getSpinnerItemName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dropdown_layout, parent, false);
        }
        CustomItem item =(CustomItem) getItem(position);
        ImageView dropdownIv = convertView.findViewById(R.id.ivDropdownLayout);
        TextView dropDownTv = convertView.findViewById(R.id.tvDropdownLayout);
        if (item != null) {
            dropdownIv.setImageResource(item.getSpinnerItemImage());
            dropDownTv.setText(item.getSpinnerItemName());
        }
        return convertView;
    }
}
