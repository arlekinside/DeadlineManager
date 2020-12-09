package com.github.arlekinside;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
public class UpdateReader implements RequestStreamHandler{

    ObjectMapper objectMapper = new ObjectMapper();
    private Commands1 commands;
    private Bot bot = new Bot();

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
        //Create a writer to send responses to server
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("US-ASCII"))));
        Update update = null;
        //Transform byte array of InputStream to org.telegram.telegrambots.meta.api.objects.Update object
        try {
            update = objectMapper.readValue(new JSONObject(new String(inputStream.readAllBytes(), StandardCharsets.US_ASCII)).getString("body"), Update.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Calling telegram updates` handler
        handleUpdate(update);
        // Send HTTP 200 OK request to server
        writer.write("{\"isBase64Encoded\": true," +
                "    \"statusCode\": 200" +
                "}");
        writer.close();
    }
    public void handleUpdate(Update update) {
        if (update.hasMessage()) {
            //Handle update in case it was a text message
            Commands cmd = new Commands(bot, update.getMessage().getChatId(), update.getMessage().getText());
            if (update.getMessage().hasText()) {
                cmd.run();
            }
        } else if (update.hasCallbackQuery()) {
            //Handle update in case it was CallBackQuery
            Commands cmd = new Commands(bot, update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getText());
            cmd.run();
            try {
                //Send a "Button clicked" response to user, to be sure, it was clicked for real XD
                bot.execute(new AnswerCallbackQuery()
                        .setCallbackQueryId(update.getCallbackQuery().getId())
                        .setText("Button clicked"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
