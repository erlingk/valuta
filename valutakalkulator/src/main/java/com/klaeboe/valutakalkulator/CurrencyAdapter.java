package com.klaeboe.valutakalkulator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by klaboe on 19/11/14.
 */
public class CurrencyAdapter extends ArrayAdapter<String> {
    private final Context context;
    private CurrencyHandler currencyHandler;
    private String NOK_TAG = "NOK";

    public CurrencyAdapter(Context context, CurrencyHandler currencyHandler) {
        super(context, R.layout.rowlayout, currencyHandler.getPopularCurrenciesNotTheLocal());
        this.context = context;
        this.currencyHandler = currencyHandler;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView currencyTextView = (TextView) rowView.findViewById(R.id.listCur);
        TextView ratesTextView = (TextView) rowView.findViewById(R.id.listRate);
        ImageView flagImageView = (ImageView) rowView.findViewById(R.id.listFlagIcon);

        String currencyTag = currencyHandler.getPopularCurrenciesNotTheLocal().get(position);
        currencyTextView.setText(currencyTag);

        float tagValue = Float.parseFloat(currencyHandler.getCurrencyMap().get(currencyTag));
        float nokValue = Float.parseFloat(currencyHandler.getCurrencyMap().get(NOK_TAG));
        String currencyValue = String.format("%.3f", nokValue/tagValue);
        ratesTextView.setText(currencyValue);

        // set flag icon
        int resourceId = context.getResources().getIdentifier(currencyTag.toLowerCase(), "drawable", context.getPackageName());
        if (resourceId != 0) {
            Log.v(context.getClass().getName(), "Adding flag for tag: " + currencyTag);
            flagImageView.setImageResource(resourceId);
        } else {
            Log.v(context.getClass().getName(), "Flag image not found for tag: " + currencyTag);
            flagImageView.setImageResource(R.drawable.eur);
        }

        return rowView;
    }
}
