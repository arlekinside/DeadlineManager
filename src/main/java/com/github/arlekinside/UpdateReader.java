package com.github.arlekinside;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
public class UpdateReader implements RequestStreamHandler{
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {

        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("US-ASCII"))));
        Update update = null;
        try {
            update = objectMapper.readValue(new JSONObject(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)).getString("body"), Update.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bot bot = new Bot();
        try {
            bot.execute(new SendMessage()
                            .setChatId(update.getMessage().getChatId())
                            .setText("Your message: " + update.getMessage().getText()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        writer.write("{\"isBase64Encoded\": true," +
                "    \"statusCode\": 200" +
                "}");
        writer.close();
    }
}
