package com.klaeboe.valutakalkulator;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity {
    private CurrencyLoader currencyLoader;
    CurrencyHandler currencyHandler;

    private EditText currFrom;
    private Spinner spinnerFrom;

    private EditText currTo;
    private Spinner spinnerTo;

    private CheckBox checkBoxAllCurrencies;
    private ArrayAdapter<String> adapter;

    private Map<String, String> currencyMap;
    private List<String> allCurrencies;
    private List<String> popularCurrencies;
    private List<String> usedCurrencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        currFrom = (EditText) findViewById(R.id.editText);
        spinnerFrom = (Spinner) findViewById(R.id.spinner);

        currTo = (EditText) findViewById(R.id.editText2);
        spinnerTo = (Spinner) findViewById(R.id.spinner2);

        checkBoxAllCurrencies = (CheckBox) findViewById(R.id.checkBoxAllCurrencies);

        currencyHandler = new CurrencyHandler();
        currencyLoader = new CurrencyLoader();

        currencyLoader.execute((Void) null);

        currFrom.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(getApplicationContext().getClass().getName(), "onEditorAction id: " + actionId);
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    calculateCurrency();
                    return true;
                }
                return false;
            }
        });


        currFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                Log.v(getApplicationContext().getClass().getName(), "hasFocus: " + hasFocus);
                if(!hasFocus)
                    calculateCurrency();
            }
        });

        /*currFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.v(getApplicationContext().getClass().getName(), "beforeTextChanged: " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v(getApplicationContext().getClass().getName(), "onTextChanged: " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.v(getApplicationContext().getClass().getName(), "afterTextChanged: " + s);
            }
        });*/

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(getApplicationContext().getClass().getName(), "position: " + position);
                TextView textView = (TextView) view;

                String selectedCurrency = ((TextView) view).getText().toString();
                //currViewFrom.setText(currencyMap.get(selectedCurrency));
                calculateCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(getApplicationContext().getClass().getName(), "position: " + position);
                String selectedCurrency = ((TextView) view).getText().toString();
                //currTo.setText(currencyMap.get(selectedCurrency));
                calculateCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        checkBoxAllCurrencies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if(checkBox.isChecked()) {
                    updateCurrencies();
                } else {
                    updateCurrencies();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void calculateCurrency() {
        //String fromCurrencyString = currencyMap.get(((TextView)spinnerFrom.getSelectedItem()).getText());
        //String toCurrencyString = currencyMap.get(((TextView)spinnerTo.getSelectedItem()).getText());

        String fromCurrencyString = currencyMap.get(spinnerFrom.getSelectedItem());
        String toCurrencyString = currencyMap.get(spinnerTo.getSelectedItem());

        float fromCurrency = Float.parseFloat(fromCurrencyString);
        float toCurrency = Float.parseFloat(toCurrencyString);
        float fromValue = Float.parseFloat(currFrom.getText().toString());
        currTo.setText(Float.toString(fromValue * toCurrency / fromCurrency));
    }

    private void updateCurrencies() {
        usedCurrencies.clear();
        if(checkBoxAllCurrencies.isChecked()) {
            usedCurrencies.addAll(currencyHandler.getAvailableCurrencies());
        } else {
            usedCurrencies.addAll(currencyHandler.getPopularCurrencies());
        }

        Collections.sort(usedCurrencies);
        Log.v(getApplicationContext().getClass().getName(), "updateCurrencies: " + usedCurrencies);
        adapter.notifyDataSetChanged();

        spinnerFrom.setSelection(usedCurrencies.indexOf(currencyHandler.getDefaultFromCurrency()));
        spinnerTo.setSelection(usedCurrencies.indexOf(currencyHandler.getDefaultToCurrency()));
    }

    private class CurrencyLoader extends AsyncTask<Void, Void, String> {
        ConnectionHandler connectionHandler;
        //CurrencyHandler currencyHandler;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            this.connectionHandler = new ConnectionHandler(getApplicationContext());
            //this.currencyHandler = new CurrencyHandler();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Log.v(this.getClass().getName(), "isConnectingToInternet(): " + connectionHandler.isConnectingToInternet());
            if(connectionHandler.isConnectingToInternet()) {
                return connectionHandler.getCurrencies();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String currencies) {
            // TODO Auto-generated method stub
            super.onPostExecute(currencies);
            currencyMap = currencyHandler.getCurrencyMap(currencies);
            popularCurrencies = currencyHandler.getPopularCurrencies();
            allCurrencies = currencyHandler.getAvailableCurrencies();

            if(checkBoxAllCurrencies.isChecked()) {
                usedCurrencies = allCurrencies;
            } else {
                usedCurrencies = popularCurrencies;
            }

            Collections.sort(usedCurrencies);
            adapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1, usedCurrencies);

            spinnerFrom.setAdapter(adapter);
            spinnerTo.setAdapter(adapter);

            spinnerFrom.setSelection(usedCurrencies.indexOf(currencyHandler.getDefaultFromCurrency()));
            spinnerTo.setSelection(usedCurrencies.indexOf(currencyHandler.getDefaultToCurrency()));
        }
    }
}
