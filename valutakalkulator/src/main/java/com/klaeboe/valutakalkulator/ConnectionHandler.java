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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by klaboe on 11/11/14.
 */
public class ConnectionHandler {
    private Context context;

    public ConnectionHandler(Context context) {
        super();
        this.context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if(info!= null) {
                for(int i = 0; i< info.length;i++) {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getCurrencies() {
        String jsonCurrencies = "";
        ConnectionHandler cd = new ConnectionHandler(context);
        Log.v(this.getClass().getName(), "isConnectingToInternet(): " + cd.isConnectingToInternet());

        if(cd.isConnectingToInternet()) {
            jsonCurrencies = getCurrenciesWithHttp();
            if(jsonCurrencies.isEmpty()) {
                jsonCurrencies = readFromFile();
            }
        }
        return jsonCurrencies;
    }

    public String getCurrenciesWithHttp() {
        String jsonCurrencies = "";
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            //HttpGet httpGet = new HttpGet("http://www.iheartquotes.com/api/v1/random.json");
            HttpGet httpGet = new HttpGet("http://www.getexchangerates.com/api/latest.json");
            //http://www.getexchangerates.com/api/latest.json
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream is = httpEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            is.close();

            jsonCurrencies = sb.toString();
            writeToFile(jsonCurrencies);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

        return jsonCurrencies;
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

    private String readFromFile() {
        String ret = null;
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
}
