package com.klaeboe.valutakalkulator;

import android.content.Context;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by klaboe on 11/11/14.
 */
public class CurrencyHandler {
    private String DEFAULT_FROM_CURRENCY = "EUR";
    private String LOCAL_CURRENCY = "NOK";

    private ConnectionHandler connectionHandler;
    private Map<String, String> currencyMap;
    private List<String> popularCurrencies = new ArrayList<String>(Arrays.asList(
            "USD", "EUR", "GBP", "CHF", "SEK", "DKK", "JPY", "AUD", "CAD", "NOK"));

    public CurrencyHandler(Context context) {
        connectionHandler = new ConnectionHandler(context);
    }

    public Map<String, String> getCurrencyMap () {
        return currencyMap;
    }

    public List<String> getAvailableCurrencies() {
        return new ArrayList<String>(currencyMap.keySet());
    }

    public List<String> getPopularCurrencies() {
        return new ArrayList<String>(popularCurrencies);
    }

    public List<String> getPopularCurrenciesNotTheLocal() {
        List<String> popularCurrencies = getPopularCurrencies();
        popularCurrencies.remove(LOCAL_CURRENCY);
        return popularCurrencies;
    }

    public String getDefaultFromCurrency() {
        if(currencyMap.containsKey(DEFAULT_FROM_CURRENCY)) {
            return DEFAULT_FROM_CURRENCY;
        } else {
            return (String)currencyMap.keySet().toArray()[0];
        }
    }

    public String getDefaultToCurrency() {
        if(currencyMap.containsKey(LOCAL_CURRENCY)) {
            return LOCAL_CURRENCY;
        } else {
            return (String)currencyMap.keySet().toArray()[0];
        }
    }

    public boolean populateCurrencies() {
        currencyMap = connectionHandler.getCurrencyMap();
        removePopularCurrenciesNotAvailable(currencyMap.keySet());
        return !currencyMap.isEmpty();
    }

    public Date getDate() {
        return connectionHandler.getDate();
    }

    private void removePopularCurrenciesNotAvailable(Set<String> availableCurrencies) {
        List<String> toRemove= new ArrayList<String>();
        for(String popularCurrency : popularCurrencies) {
            if(!availableCurrencies.contains(popularCurrency)) {
                toRemove.add(popularCurrency);
            }
        }
        popularCurrencies.removeAll(toRemove);
    }
}
