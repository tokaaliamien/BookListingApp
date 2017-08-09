package com.example.android.booklistingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.ArrayList;

import static com.example.android.booklistingapp.R.id.auther;

public class MainActivity extends AppCompatActivity {


    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private ProgressBar progressBar;
    private ListView listView = null;
    private TextView messageTextView;

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        messageTextView = (TextView) findViewById(R.id.message_textview);

        listView = (ListView) findViewById(R.id.listview);

        Button search = (Button) findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageTextView.setText("");
                listView.setEmptyView(messageTextView);
                if (isOnline()) {
                    progressBar.setVisibility(View.VISIBLE);
                    EditText keyWord = (EditText) findViewById(R.id.editText);

                    BookAsyncTask asyncTask = new BookAsyncTask();
                    asyncTask.execute(keyWord.getText().toString());

                } else {
                    Log.e(LOG_TAG, "Not online");

                    messageTextView.setText(getResources().getString(R.string.offline_message));
                    listView.setEmptyView(messageTextView);
                }
            }
        });

    }


    private class BookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(String... params) {
            URL url = null;

            try {
                String temp = BASE_URL + params[0];
                url = new URL(temp);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Can not get the base url");
            }

            String jsonResponse = "";

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    Log.e(LOG_TAG, "Connected");
                    inputStream = urlConnection.getInputStream();
                    StringBuilder output = new StringBuilder();
                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        String line = reader.readLine();
                        while (line != null) {
                            output.append(line);
                            line = reader.readLine();
                        }
                    }

                    jsonResponse = output.toString();
                    Log.e(LOG_TAG, "JSON:\n" + jsonResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "can't close inputStream");
                    }
                }
            }

            ArrayList<Book> booksArrayList = extractBooksFromJson(jsonResponse);

            return booksArrayList;
        }

        private ArrayList<Book> extractBooksFromJson(String jsonResponse) {
            ArrayList<Book> booksArrayList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray items = jsonObject.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {
                    JSONObject temp = items.getJSONObject(i);
                    JSONObject volumeInfo = temp.getJSONObject("volumeInfo");
                    String title = volumeInfo.getString("title");

                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    String auther = "";
                    for (int j = 0; j < authors.length(); j++) {
                        auther += authors.getString(j);
                    }

                    booksArrayList.add(new Book(title, auther));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return booksArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            //super.onPostExecute(books);
            if (books.isEmpty()) {
                messageTextView.setText(getResources().getString(R.string.empty_message));
                listView.setEmptyView(messageTextView);
            } else {
                BookAdaptor adaptor = new BookAdaptor(MainActivity.this, books);
                listView.setAdapter(adaptor);

            }
            progressBar.setVisibility(View.GONE);
        }
    }


}
