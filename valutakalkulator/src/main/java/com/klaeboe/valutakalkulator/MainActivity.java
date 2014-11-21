package com.klaeboe.valutakalkulator;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private CurrencyLoader currencyLoader;
    CurrencyHandler currencyHandler;

    private EditText currFrom;
    private Spinner spinnerFrom;
    private ImageButton switchRateButton;
    private TextView currTo;
    private Spinner spinnerTo;
    private ListView currencyListView;
    private TextView syncDate;
    private ImageButton syncRateButton;

    private ArrayAdapter<String> currencyTagAdapter;
    private CurrencyAdapter currencyAdapter;
    private List<String> usedCurrencies;
    private Date lastDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        currFrom = (EditText) findViewById(R.id.editText);
        spinnerFrom = (Spinner) findViewById(R.id.spinner);
        switchRateButton = (ImageButton) findViewById(R.id.switchRateButton);
        currTo = (TextView) findViewById(R.id.editText2);
        spinnerTo = (Spinner) findViewById(R.id.spinner2);
        currencyListView = (ListView) findViewById(R.id.currencyListView);
        syncDate = (TextView) findViewById(R.id.syncDate);
        syncRateButton = (ImageButton) findViewById(R.id.syncRateButton);

        currencyHandler = new CurrencyHandler(getApplicationContext());
        currencyLoader = new CurrencyLoader();

        // Hide keyboard when app opens (due to focus on edit text)
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        currencyLoader.execute((Void) null);

        currFrom.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(getApplicationContext().getClass().getName(), "onEditorAction id: " + actionId);
                trimValue(v); // Check if content is empty, contains leading zeroes etc
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    calculateCurrency();
                }
                return false; // Hide keyboard when done
            }
        });


        currFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                Log.v(getApplicationContext().getClass().getName(), "hasFocus: " + hasFocus);
                if (!hasFocus)
                    calculateCurrency();
            }
        });

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(getApplicationContext().getClass().getName(), "position: " + position);
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
                calculateCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        switchRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldFromSpinnerPos = spinnerFrom.getSelectedItemPosition();
                spinnerFrom.setSelection(spinnerTo.getSelectedItemPosition());
                spinnerTo.setSelection(oldFromSpinnerPos);
            }
        });

        syncRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencyLoader = new CurrencyLoader();
                currencyLoader.execute((Void) null);
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

        switch (id) {
            case R.id.action_settings:
                Log.v(getApplicationContext().getClass().getName(), "Starting settings activity");
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            /*case R.id.exit:
                finish();
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    private void calculateCurrency() {
        String fromCurrencyString = currencyHandler.getCurrencyMap().get(spinnerFrom.getSelectedItem());
        String toCurrencyString = currencyHandler.getCurrencyMap().get(spinnerTo.getSelectedItem());

        float fromCurrency = Float.parseFloat(fromCurrencyString);
        float toCurrency = Float.parseFloat(toCurrencyString);
        float fromValue = Float.parseFloat(currFrom.getText().toString());
        currTo.setText(Float.toString(fromValue * toCurrency / fromCurrency));
    }

    /*
    private void updateCurrencies() {
        usedCurrencies.clear();
        if(checkBoxAllCurrencies.isChecked()) {
            usedCurrencies.addAll(currencyHandler.getAvailableCurrencies());
        } else {
            usedCurrencies.addAll(currencyHandler.getPopularCurrencies());
        }

        Collections.sort(usedCurrencies);
        Log.v(getApplicationContext().getClass().getName(), "updateCurrencies: " + usedCurrencies);
        currencyTagAdapter.notifyDataSetChanged();

        spinnerFrom.setSelection(usedCurrencies.indexOf(currencyHandler.getDefaultFromCurrency()));
        spinnerTo.setSelection(usedCurrencies.indexOf(currencyHandler.getDefaultToCurrency()));
    }
    */

    private void trimValue(final TextView v) {
        String trimValue = v.getText().toString();

        //Remove leading zeroes
        trimValue = trimValue.replaceFirst("^0+(?!$)", "");

        // Replace empty string with 1
        trimValue = trimValue.isEmpty() ? "1" : trimValue;

        Log.v(getApplicationContext().getClass().getName(), "trim'ed currency: " + trimValue);
        v.setText(trimValue);
    }

    private class CurrencyLoader extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Log.v(this.getClass().getName(), "doInBackground()");
            return currencyHandler.populateCurrencies();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            // TODO Auto-generated method stub
            super.onPostExecute(success);

            if(!success) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Currencies not available");
                builder.show();
                return;
            }

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean allCurrencies = sharedPref.getBoolean(SettingsActivity.KEY_ALL_CURRENCIES, true);

            if(allCurrencies) {
                usedCurrencies = currencyHandler.getAvailableCurrencies();
            } else {
                usedCurrencies = currencyHandler.getPopularCurrencies();
            }

            currencyAdapter = new CurrencyAdapter(MainActivity.this, currencyHandler);
            currencyListView.setAdapter(currencyAdapter);

            Collections.sort(usedCurrencies);
            currencyTagAdapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1, usedCurrencies);
            spinnerFrom.setAdapter(currencyTagAdapter);
            spinnerTo.setAdapter(currencyTagAdapter);

            spinnerFrom.setSelection(usedCurrencies.indexOf(currencyHandler.getDefaultFromCurrency()));
            spinnerTo.setSelection(usedCurrencies.indexOf(currencyHandler.getDefaultToCurrency()));

            if(lastDate != null && lastDate.getTime() != currencyHandler.getDate().getTime()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Kurser oppdatert", Toast.LENGTH_LONG);
                toast.show();
            } else if(lastDate != null) {
                Toast toast = Toast.makeText(getApplicationContext(), "Ingen nye kurser tilgjengelig", Toast.LENGTH_LONG);
                toast.show();
            }

            lastDate = currencyHandler.getDate();
            syncDate.setText(lastDate.toString());
        }
    }
}
