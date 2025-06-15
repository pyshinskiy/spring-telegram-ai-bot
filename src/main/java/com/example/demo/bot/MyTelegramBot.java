package com.example.demo.bot;

import com.example.demo.config.OpenRouterClient;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class MyTelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final OpenRouterClient openRouterClient;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            String reply = "Извини, AI пока не доступен.";

            if (openRouterClient != null) {
                try {
                    reply = openRouterClient.chat(userText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(reply);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
