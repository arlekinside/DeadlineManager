package com.github.arlekinside;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Commands {
    long chatID;
    String userInput;
    private Bot bot;
    private String authToken = null;
    private CalendarManager calendar = null;
    protected Commands(Bot bot, Message message){
        this.bot = bot;
        this.chatID = message.getChatId();
        this.userInput = message.getText();

        if(!Users.userExists(chatID)) {
            new Users(chatID).writeUser();
        }
    }

    public void run() {
        if (Users.getUser(chatID).getBotStatus().equals("read")) {
            if (!userInput.matches("/start\\s\\S*")) {
                switch (commandCutter()) {
                    case "/start":
                        start();
                        break;
                    case "/help":
                        help();
                        break;
                    case "/addEvent":
                        addEvent();
                        break;
                }
            } else {
                userInput = userInput.replaceAll("/start ", "");
                calendar = new CalendarManager(this.bot, Users.getUser(chatID), userInput).updateUser();
                try {
                    bot.execute(new SendMessage().setChatId(chatID).setText("Google calendar was connected\n" +
                                                                                "Use /addEvent to add a deadline"));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (Users.getUser(chatID).getBotStatus().equals("getEvent")) {
            if (userInput.matches("\\S*\\s\\d{2}\\s\\d{2}")) {
                System.out.println(true);
                String[] input = userInput.split(" ");
                if (calendar == null) {
                    calendar = new CalendarManager(this.bot, Users.getUser(chatID), null);
                }
                calendar.addEvent(input[0],input[1],input[2]);
                Users.getUser(chatID).setBotStatus("read").writeUser();
            }else{
                try {
                    bot.execute(new SendMessage().setChatId(chatID)
                                                .setText("Wrong input"));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void start() {
        InlineKeyboard kb = new InlineKeyboard()
                .setChatID(chatID)
                .setMessage("Sign in Google account firstly, please")
                .buttonUrl("Authorization", "https://accounts.google.com/o/oauth2/v2/auth?" +
                        "scope=" + BotConfig.SCOPE + "&" +
                        "&access_type=offline" +
                        "&prompt=consent" +
                        "&include_granted_scopes=true" +
                        "&response_type=code" +
                        "&state=" + chatID +
                        "&redirect_uri=" + BotConfig.REDIRECT_URL +
                        "&client_id=" + BotConfig.CLIENT_ID)
                .row()
                .setInlineKeyboardMarkup();
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
    private void addEvent(){
        Users user = Users.getUser(chatID);
        if(user.getBotStatus().equals("read")){
            user.setBotStatus("getEvent").writeUser();
        }
        try {
            bot.execute(new SendMessage().setChatId(chatID)
                    .setText("Enter an event in the following format\n" +
                            "\"Deadline_Name Month Day\"\n" +
                            "For example:\n" +
                            "Programming 12 23"));
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
