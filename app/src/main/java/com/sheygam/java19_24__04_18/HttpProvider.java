package com.sheygam.java19_24__04_18;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpProvider {
    private static final String BASE_URL = "https://telranstudentsproject.appspot.com/_ah/api/contactsApi/v1";
    private static final HttpProvider ourInstance = new HttpProvider();
    private Gson gson;

    public static HttpProvider getInstance() {
        return ourInstance;
    }

    private HttpProvider() {
        gson = new Gson();
    }

    public String registration(String email, String password) throws Exception {
        Auth auth = new Auth(email, password);
        String requestJson = gson.toJson(auth);

        URL url = new URL(BASE_URL + "/registration");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.addRequestProperty("Content-Type","application/json");

//        connection.connect(); //For GET request
        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        bw.write(requestJson);
        bw.flush();
        bw.close();

        if(connection.getResponseCode() < 400){
            String line;
            String responseJson = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = br.readLine())!=null){
                responseJson += line;
            }
            AuthToken authToken = gson.fromJson(responseJson,AuthToken.class);
            return authToken.getToken();
        }else if(connection.getResponseCode() == 409){
            throw new Exception("User already exist");
        }else{
            String line;
            String responseJson = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            while ((line = br.readLine())!=null){
                responseJson += line;
            }
            Log.d("MY_TAG", "registration error: " + responseJson);
            throw new Exception("Server error! Call to support!");
        }
    }
}
