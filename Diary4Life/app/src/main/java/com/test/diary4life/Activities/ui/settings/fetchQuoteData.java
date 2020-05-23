package com.test.diary4life.Activities.ui.settings;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class fetchQuoteData extends AsyncTask<Void, Void, Void> {

    String data = "";
    String dataParsed = "";
    String singleParsed = "";
    String quote = "";

    @Override
    protected Void doInBackground(Void ... voids) {

        try {
            URL urlQuote = new URL("https://api.jsonbin.io/b/5ea08fe25fa47104cea53173");

            HttpURLConnection httpURLConnection = (HttpURLConnection) urlQuote.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while (line != null)
            {
                line = bufferedReader.readLine();
                data = data + line;
            }

            JSONArray JA = new JSONArray(data);
            Random random = new Random();
            int selection = random.nextInt(JA.length());
            JSONObject JO = (JSONObject) JA.get(selection);

            dataParsed = (String) JO.get("content");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        SettingsFragment.txtQuote.setText(this.dataParsed);
    }
}
