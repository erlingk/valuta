package com.klaeboe.valutakalkulator;

import android.content.Context;
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
    private final String[] values;
    private final String[] rates;

    public CurrencyAdapter(Context context, String[] values, String[] rates) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
        this.rates = rates;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView currencyTextView = (TextView) rowView.findViewById(R.id.listCur);
        TextView ratesTextView = (TextView) rowView.findViewById(R.id.listRate);
        ImageView flagImageView = (ImageView) rowView.findViewById(R.id.listFlagIcon);
        currencyTextView.setText(values[position]);
        ratesTextView.setText(rates[position]);

        // set flag icon
        String s = values[position];
        if (s.startsWith("EUR")) {
            flagImageView.setImageResource(R.drawable.eur);
        } else if (s.startsWith("NOK")) {
            flagImageView.setImageResource(R.drawable.nok);
        } else if (s.startsWith("SEK")) {
            flagImageView.setImageResource(R.drawable.sek);
        }

        return rowView;
    }
}
