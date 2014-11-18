package com.klaeboe.valutakalkulator;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private CurrencyLoader currencyLoader;
    CurrencyHandler currencyHandler;

    private EditText currFrom;
    private Spinner spinnerFrom;
    private EditText currTo;
    private Spinner spinnerTo;
    private CheckBox checkBoxAllCurrencies;
    private TextView syncDate;
    private Button btnGetRate;

    private ArrayAdapter<String> adapter;
    private List<String> usedCurrencies;
    private Date lastDate;

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
        syncDate = (TextView) findViewById(R.id.syncDate);
        btnGetRate = (Button) findViewById(R.id.btnGetRate);

        currencyHandler = new CurrencyHandler(getApplicationContext());
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

        btnGetRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date oldDate = currencyHandler.getDate();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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

            if(checkBoxAllCurrencies.isChecked()) {
                usedCurrencies = currencyHandler.getAvailableCurrencies();
            } else {
                usedCurrencies = currencyHandler.getPopularCurrencies();
            }


            Collections.sort(usedCurrencies);
            adapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1, usedCurrencies);

            spinnerFrom.setAdapter(adapter);
            spinnerTo.setAdapter(adapter);

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
