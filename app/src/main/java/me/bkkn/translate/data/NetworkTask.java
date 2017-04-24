package me.bkkn.translate.data;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class NetworkTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        BufferedReader br = null;
        try {
            url = new URL("https://translate.yandex.net/api/v1.5/tr/translate?" +
                    "key=" + "trnsl.1.1.20170323T083956Z.a11cd4e0063e145c.cb685107a6ef29225d4e809944b0cc4bf7942f53" +
                    "&text=" + "\"" + URLEncoder.encode(params[0], "UTF-8") + "\"" +
                    "&lang="  + params[1]
            );

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Error";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            final StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return url.toString();
        }
    }
}
