package com.chromsicle.jsonweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        task.execute("https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22");
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            //convert the string into a URL
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();//creates a URL connection
                InputStream in = urlConnection.getInputStream(); //creates an input stream to collect the data as it comes through
                InputStreamReader reader = new InputStreamReader(in); //reads the data
                int data = reader.read();

                //keep reading data until it's all read
                //convert the int data that the reader is returning into a char and keep adding it to the result string
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        //doInBackground should never touch UI stuff, onPostExecute is where that would go
        //this is where you write code that will execute when doInBackground finishes
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //convert the html to a JSON object
            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");
                Log.i("weather content", weatherInfo);

                //info from the weather object may be an array so we have to handle it appropriately
                JSONArray arr = new JSONArray(weatherInfo);

                for(int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    Log.i("main", jsonPart.getString("main"));
                    Log.i("description", jsonPart.getString("description"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
