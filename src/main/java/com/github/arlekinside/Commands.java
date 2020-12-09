package com.github.arlekinside;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Commands {
    long chatID;
    String userInput;
    private Bot bot;

    protected Commands(Bot bot, long chatID, String userInput){
        this.bot = bot;
        this.chatID = chatID;
        this.userInput = userInput;
    }

    public void run(){
        if(userInput.startsWith("/")) {
            switch (commandCutter()){
                case "/start":
                    start();
                    break;
                case "/help":
                    help();
                    break;
            }
        }else{

        }
    }

    private void start(){
        InlineKeyboard kb = new InlineKeyboard()
                .setChatID(chatID)
                .setMessage("So lets make a new deadline!")
                .button("Add an event", "/newEvent")
                .row()
                .button("My events", "/myEvents")
                .row()
                .button("Cancel","/cancel")
                .row()
                .setInlineKeyboardMarkup();
        //startKeyboard = kb;
        try {
            bot.execute(kb.inlineMessage());
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
    private void help(){
        try {
            bot.execute(new SendMessage()
                    .setChatId(chatID)
                    .setText("There's gonna be smt useful")
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private String commandCutter(){
        if(userInput.contains(BotConfig.BOT_NAME)){
            return userInput.replaceAll(BotConfig.BOT_NAME, "");
        }else{
            return userInput;
        }
    }
}
