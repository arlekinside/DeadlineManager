package com.github.arlekinside;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


public class Bot extends TelegramWebhookBot {

    protected Bot(){
    }
    @Override
    public SendMessage onWebhookUpdateReceived(Update update) {
        return null;
    }
    @Override
    public String getBotUsername() {
        return BotConfig.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.BOT_TOKEN;
    }

    @Override
    public String getBotPath() {
        return BotConfig.WEBHOOK_URL;
    }
}