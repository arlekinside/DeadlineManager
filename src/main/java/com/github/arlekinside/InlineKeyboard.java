package com.github.arlekinside;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboard {
    private long chatID;
    private String message;

    List<InlineKeyboardButton> inlineKeyboardButtons;
    List<List<InlineKeyboardButton>> inlineKeyboardRows;

    InlineKeyboardMarkup inlineKeyboardMarkup;
    public InlineKeyboard(){
        inlineKeyboardButtons = new ArrayList<InlineKeyboardButton>();
        inlineKeyboardRows = new ArrayList<List<InlineKeyboardButton>>();
        inlineKeyboardMarkup = new InlineKeyboardMarkup();
    }
    public SendMessage inlineMessage(){
        return new SendMessage()
                .setChatId(chatID)
                .setText(message)
                .setReplyMarkup(inlineKeyboardMarkup);
    }
    public InlineKeyboard setInlineKeyboardMarkup(){
        inlineKeyboardMarkup.setKeyboard(inlineKeyboardRows);
        return this;
    }
    public InlineKeyboard row(){
        if(!inlineKeyboardButtons.isEmpty()){
            inlineKeyboardRows.add(inlineKeyboardButtons);
            inlineKeyboardButtons = new ArrayList<InlineKeyboardButton>();
        }
        return this;
    }
    public InlineKeyboard button (String title, String callBackData){
        inlineKeyboardButtons.add(new InlineKeyboardButton().setText(title).setCallbackData(callBackData));
        return this;
    }
    public InlineKeyboard buttonUrl (String title, String url){
        inlineKeyboardButtons.add(new InlineKeyboardButton().setText(title).setUrl(url));
        return this;
    }
    public InlineKeyboard setChatID(long chatID){
        this.chatID = chatID;
        return this;
    }
    public InlineKeyboard setMessage(String message){
        this.message = message;
        return this;
    }
}
