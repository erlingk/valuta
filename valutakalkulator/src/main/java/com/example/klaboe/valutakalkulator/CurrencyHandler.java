package com.example.klaboe.valutakalkulator;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by klaboe on 11/11/14.
 */
public class CurrencyHandler {
    private List<String> availableCurrencies = new ArrayList<String>();
    private List<String> popularCurrencies = new ArrayList<String>(Arrays.asList(
            "USD", "EUR", "GBP", "CHF", "NOK", "SEK", "DKK", "RUB",
            "CNY", "INR", "JPY", "AUD", "CAD"));
    private String DATE_KEY = "DateTime";
    private Date date;

    public List<String> getAvailableCurrencies() {
        return new ArrayList<String>(availableCurrencies);
    }

    public List<String> getPopularCurrencies() {
        return new ArrayList<String>(popularCurrencies);
    }

    public String getDefaultFromCurrency() {
        return "EUR";
    }

    public String getDefaultToCurrency() {
        return "NOK";
    }

    public Date getDate() {
        return date;
    }

    public Map getCurrencyMap(String currencies) {

        Map<String, String> currencyMap = new HashMap<String, String>();

        try {
            JSONArray arr;
            if(currencies != null && !currencies.isEmpty()) {
                arr = new JSONArray(currencies);
            } else {
                Log.e(this.getClass().getName(), "Currencies not available");
                return null;
            }
            Log.v(this.getClass().getName(), "Json string: " + arr.getString(0));

            JSONObject jsonObj = arr.getJSONObject(0);
            JSONArray currArray = jsonObj.names();

            for(int i = 0; i < currArray.length(); i++) {
                String key = currArray.getString(i);
                String value = jsonObj.getString(key);
                Log.v(this.getClass().getName(), "Json key: " + key);
                Log.v(this.getClass().getName(), "Json value: " + value);

                if(key.equals(DATE_KEY)) {
                    //date = new Date(value);
                } else {
                    currencyMap.put(key, value);
                }
            }

            availableCurrencies = new ArrayList<String>(currencyMap.keySet());
            removeUnavailableCurrencies(currencyMap.keySet());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return currencyMap;
    }

    private void removeUnavailableCurrencies(Set<String> availableCurrencies) {
        List<String> toRemove= new ArrayList<String>();
        for(String popularCurrency : popularCurrencies) {
            if(!availableCurrencies.contains(popularCurrency)) {
                toRemove.add(popularCurrency);
            }
        }
        popularCurrencies.removeAll(toRemove);
    }
}
