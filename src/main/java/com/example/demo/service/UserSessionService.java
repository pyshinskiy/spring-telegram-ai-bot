package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserSessionService {

    private final Map<String, String> userModelMap = new HashMap<>();

    private final List<String> models = List.of(
            "openai/gpt-3.5-turbo",
            "openai/gpt-4",
            "mistralai/mixtral-8x7b-instruct",
            "meta-llama/llama-3-8b-instruct"
    );

    public String getUserModel(String userId) {
        return userModelMap.getOrDefault(userId, models.get(0));
    }

    public String changeModel(String userId, String model) {
        if (!models.contains(model)) {
            return "❌ Model wasn't found.";
        }
        userModelMap.put(userId, model);
        return "✅ Model has set: " + model;
    }

    public SendMessage getModelButtons(String userId) {
        String currentModel = getUserModel(userId);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String model : models) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            String label = model.equals(currentModel) ? "✅ " + model : model;
            button.setText(label);
            button.setCallbackData(model);
            rows.add(List.of(button));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        SendMessage msg = new SendMessage();
        msg.setChatId(userId.toString());
        msg.setText("Choose model:\nCurrent: " + currentModel);
        msg.setReplyMarkup(markup);
        return msg;
    }
}

