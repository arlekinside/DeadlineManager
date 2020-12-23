package com.github.arlekinside;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;

public class CalendarManager {
    private Users user = null;
    private Bot bot = null;
    private String auth_code = null;
    private Calendar service = null;
    public CalendarManager(Bot bot, Users user, String auth_code){
        this.bot = bot;
        this.user = user;
        this.auth_code = auth_code;
    }
    public void addEvent(){
        checkToken();
        if(user.getAccess_token() == null){
            errorMessage();
            return;
        }
        service = getService();
        try {
            service.events().quickAdd("primary", "EVENT").execute();
            try {
                bot.execute(new SendMessage()
                        .setChatId(user.getId())
                        .setText("Event added")
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Calendar getService(){
        Credential credentials = new GoogleCredential().setAccessToken(user.getAccess_token());
        NetHttpTransport HTTP_TRANSPORT = null;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Calendar.Builder(HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), credentials).build();
    }
    private String refreshToken() throws Exception{
        HashMap<String, String> values = new HashMap<String, String>() {{
            put("client_id", BotConfig.CLIENT_ID);
            put("client_secret", BotConfig.CLIENT_SECRET);
            put("refresh_token", user.getRefresh_token());
            put("grant_type", "refresh_token");
        }};

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        if(response.body().contains("Bad Request") || response.body().contains("Not Found")) throw new Exception("Refresh token couldn't be used");
        return new JSONObject(response.body()).getString("access_token");
    }
    private String getTokenResponse() throws Exception{
        HashMap<String, String> values = new HashMap<String, String>() {{
            put("code", auth_code);
            put("client_id", BotConfig.CLIENT_ID);
            put("client_secret", BotConfig.CLIENT_SECRET);
            put("redirect_uri", BotConfig.REDIRECT_URL);
            put("grant_type", "authorization_code");
        }};

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        System.out.println(response.body());
        if(response.body().contains("Bad Request") || response.body().contains("Not Found")) throw new Exception("Access token couldn't be used");
        return response.body();
    }
    public CalendarManager updateUser(){
        JSONObject response = null;
        try {
            response = new JSONObject(getTokenResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setAccess_token(response.getString("access_token"));
        user.setScope(response.getString("scope"));
        if(!response.getString("refresh_token").isEmpty()) {
            user.setRefresh_token(response.getString("refresh_token"));
        }else{
            System.out.println("refresh_token is empty");
        }
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.SECOND, calendar.get(java.util.Calendar.SECOND) + response.getInt("expires_in"));
        user.setExpirationDate(calendar.getTime());
        user.writeUser();
        return this;
    }
    private void checkToken(){
        if(user.getExpirationDate() != null){
            if(user.getExpirationDate().before(java.util.Calendar.getInstance().getTime())){
                try {
                    user.setAccess_token(refreshToken()).writeUser();
                } catch (Exception e) {
                    errorMessage();
                    e.printStackTrace();
                }
            }
        }
    }
    private void errorMessage(){
        try {
            bot.execute(new SendMessage().setChatId(user.getId())
                    .setText("Register via /start please"));
        } catch (TelegramApiException telegramApiException) {
            telegramApiException.printStackTrace();
        }
    }
}