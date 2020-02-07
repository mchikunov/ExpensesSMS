package com.me.expensessms;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<SMS> objects;
    int report;
    Map<String, String > banksMap = new HashMap<>();

    BoxAdapter(Context context, ArrayList<SMS> items, int report) {
        this.report = report;
        ctx = context;
        objects = items;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loadBanks();

    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    Map<String, String > loadBanks(){

        if (!banksMap.isEmpty()) return banksMap;
        Set<String> bankSet = new HashSet<>();
     SharedPreferences sharedPreferences = getDefaultSharedPreferences(ctx);
        bankSet = sharedPreferences.getStringSet("BANKS", bankSet);
        if ((bankSet==null)) return banksMap;
        for (String bank: bankSet
             ) {
            String arr[] = bank.split(" ", 2);

            if (arr.length<2) banksMap.put(arr[0], "");
            else banksMap.put(arr[0], arr[1]);


        }
        return banksMap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }


        SMS s = getSMS(position);
        String am = s.getAmountString();
        if (report==1)
        ((TextView) view.findViewById(R.id.tvDescr)).setText(s.getDateString()+"        "+s.getBank()+" "+ banksMap.get(s.getBank()));

        else if (s.getBank().equals("TOTAL")) {((TextView) view.findViewById(R.id.tvDescr)).setText(s.getDateString());}

          else  ((TextView) view.findViewById(R.id.tvDescr)).setText(s.getDateString());

        TextView amountText =  ((TextView) view.findViewById(R.id.tvPrice));


        amountText.setText(am);

        ImageView signImage = view.findViewById(R.id.icon);

        if (am.contains("-") || am.contains("(")){
            signImage.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
            amountText.setTextColor( ContextCompat.getColor(ctx, R.color.pink_color));
        }
        else {
            signImage.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
            amountText.setTextColor( ContextCompat.getColor(ctx, R.color.green_color));
        }


        return view;

    }

    SMS getSMS(int position) {
        return ((SMS) getItem(position));
    }

}

