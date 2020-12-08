package com.github.arlekinside;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Bot extends TelegramWebhookBot {

    private static Bot bot;

    protected Bot(){

    }

    @Override
    public SendMessage onWebhookUpdateReceived(Update update) {
        return null;
    }

    public static Bot getBot(){
        return bot;
    }

    public static void setBot(Bot bot) {
        Bot.bot = bot;
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
        return BotConfig.WEBHOOK_URL; //arbitrary path to deliver updates on, username is an example.
    }
}