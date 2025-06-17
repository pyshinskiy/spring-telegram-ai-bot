package com.example.demo.bot;

import com.example.demo.service.LlmService;
import com.example.demo.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class LongPollingBot extends TelegramLongPollingBot {

    private final LlmService llmService;
    private final UserSessionService sessionService;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = null;
        String response = null;

        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String text = update.getMessage().getText();
                chatId = update.getMessage().getChatId();

                if (text.equals("/models")) {
                    execute(sessionService.getModelButtons(chatId));
                    return;
                }

                String model = sessionService.getUserModel(chatId);
                response = llmService.queryModel(model, text);
            }

            else if (update.hasCallbackQuery()) {
                String data = update.getCallbackQuery().getData();
                chatId = update.getCallbackQuery().getMessage().getChatId();
                response = sessionService.changeModel(chatId, data);
                // можно: удалить или заменить клавиатуру после выбора
                execute(sessionService.getModelButtons(chatId)); // обновим список с галочкой
            }

            if (response != null && chatId != null) {
                execute(new SendMessage(chatId.toString(), response));
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
