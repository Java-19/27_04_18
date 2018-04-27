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
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpProvider {
    private static final String BASE_URL = "https://telranstudentsproject.appspot.com/_ah/api/contactsApi/v1";
    private static final HttpProvider ourInstance = new HttpProvider();
    private Gson gson;
    private OkHttpClient client;
    private MediaType JSON;

    public static HttpProvider getInstance() {
        return ourInstance;
    }

    private HttpProvider() {
        gson = new Gson();
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15,TimeUnit.SECONDS)
                .build();
        JSON = MediaType.parse("application/json; charset=utf-8;");
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

    public String login(String email, String password) throws Exception {
        Auth auth = new Auth(email,password);
        String jBody = gson.toJson(auth);

        RequestBody requestBody = RequestBody.create(JSON,jBody);

        Request request = new Request.Builder()
                .url(BASE_URL + "/login")
                .post(requestBody)
                .addHeader("Authorization","token")
                .build();

        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            ResponseBody responseBody = response.body();
            String token = "";
            if (responseBody != null) {
                String jsonResponse = responseBody.string();
                AuthToken authToken = gson.fromJson(jsonResponse,AuthToken.class);
                token = authToken.getToken();
            }
            return token;
        }else if (response.code() == 401){
            throw new Exception("Wrong email or password");
        }else{
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                Log.d("MY_TAG", "login: error:" + responseBody.string());
            }
            throw new Exception("Server error! Call to support!");
        }
    }


}
