package com.klaeboe.valutakalkulator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by klaboe on 11/11/14.
 */
public class ConnectionHandler {
    private Context context;
    private String DATE_KEY = "DateTime";
    private Date date;

    public ConnectionHandler(Context context) {
        super();
        this.context = context;
    }

    public Date getDate() {
        return date;
    }

    public Map getCurrencyMap() {
        Map<String, String> currencyMap = new HashMap<String, String>();
        String jsonCurrencies = getCurrencies();

        try {
            JSONObject jsonObj;
            if(jsonCurrencies != null && !jsonCurrencies.isEmpty()) {
                jsonObj = new JSONObject(jsonCurrencies);
            } else {
                Log.v(this.getClass().getName(), "Currencies not available");
                return null;
            }
            Log.v(this.getClass().getName(), "Json string: " + jsonObj);

            JSONArray currArray = jsonObj.names();

            for(int i = 0; i < currArray.length(); i++) {
                String key = currArray.getString(i);
                String value = jsonObj.getString(key);
                //Log.v(this.getClass().getName(), "Json key: " + key);
                //Log.v(this.getClass().getName(), "Json value: " + value);

                if(key.equals(DATE_KEY)) {
                    date = new Date(Long.parseLong(value) * 1000);
                    Log.v(this.getClass().getName(), "Date: " + date);
                } else {
                    currencyMap.put(key, value);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return currencyMap;
    }

    private String getCurrencies() {
        String jsonCurrencies = "";
        Log.v(this.getClass().getName(), "isConnectingToInternet(): " + isConnectedToInternet());

        if(isConnectedToInternet()) {
            jsonCurrencies = getCurrenciesWithHttp();
        }

        if(jsonCurrencies.isEmpty()) {
            jsonCurrencies = readFromFile();
        }
        return jsonCurrencies;
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null) {
            if(connectivity.getAllNetworkInfo()!= null) {
                for(NetworkInfo info: connectivity.getAllNetworkInfo()) {
                    if(info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getCurrenciesWithHttp() {
        String jsonCurrencies = "";
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://www.getexchangerates.com/api/latest.json");
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream is = httpEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            is.close();

            jsonCurrencies = sb.toString();

            if(!jsonCurrencies.isEmpty()) {
                writeToFile(jsonCurrencies);
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonCurrencies;
    }

    private String readFromFile() {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("Currency.json");
            if(inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                Log.v("Message:", "reading..");
                while((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e("Message:", "File not found: " + e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("Message:", "Can not read file: " + e.toString());
        }
        return ret;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("Currency.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("Message: ", "File write failed: " + e.toString());
        }
    }
}
