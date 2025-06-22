package com.example.demo.bot;

import com.example.demo.service.GeminiService;
import com.example.demo.service.LlmService;
import com.example.demo.service.UserSessionService;
import com.example.demo.util.Base64Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LongPollingBot extends TelegramLongPollingBot {

    private final LlmService llmService;
    private final UserSessionService sessionService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GeminiService geminiService;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = null;
        String response = null;

        try {
            if (update.hasMessage() && update.getMessage().hasPhoto()) {
                String text = null;
                if (update.getMessage().getCaption() != null) {
                    text = update.getMessage().getCaption();
                }
                List<PhotoSize> photos = update.getMessage().getPhoto();
                String fileId = photos.get(photos.size() - 1).getFileId();
                chatId = update.getMessage().getChatId().toString();

                handlePhoto(text, fileId, chatId);
            }
            if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().hasPhoto()) {
                String text = update.getMessage().getText();
                chatId = update.getMessage().getChatId().toString();

                if (text.equals("/models")) {
                    execute(sessionService.getModelButtons(chatId));
                    return;
                }

                String model = sessionService.getUserModel(chatId);
                response = llmService.queryModel(model, text);
            }

            else if (update.hasCallbackQuery()) {
                String data = update.getCallbackQuery().getData();
                chatId = update.getCallbackQuery().getMessage().getChatId().toString();
                response = sessionService.changeModel(chatId, data);
                execute(sessionService.getModelButtons(chatId));
            }

            if (response != null) {
                execute(new SendMessage(chatId, response));
            }

        } catch (TelegramApiException e) {
            log.error("Error while requesting telegram API", e);
        }
    }

    private void handlePhoto(String text, String fileId, String chatId) {
        try {
            GetFile getFile = new GetFile(fileId);
            File file = execute(getFile);
            String filePath = file.getFilePath();

            String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;
            byte[] imageBytes = restTemplate.getForObject(fileUrl, byte[].class);

            if (text == null) {
                text = "Что изображено на картинке?";
            }

            String response = geminiService.sendImageAndText(Base64Util.convertImageToBase64(imageBytes), text).block();
            SendMessage sendMessage = new SendMessage(chatId, response);
            execute(sendMessage);

        } catch (Exception e) {
            log.error("Ошибка при обработке фото", e);
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
