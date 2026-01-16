package org.example.chatbotservice.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private final RagService ragService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    public TelegramBotService(RagService ragService) {
        this.ragService = ragService;
    }

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
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            System.out.println("Received message from " + chatId + ": " + messageText);

            if (messageText.equalsIgnoreCase("/start")) {
                sendResponse(chatId, "Hello! I am your AI Assistant. Ask me anything about the documents.");
                return;
            }

            if (messageText.equalsIgnoreCase("/ping")) {
                sendResponse(chatId, "Pong! I am alive.");
                return;
            }

            // Ask RAG Service
            try {
                String response = ragService.ask(messageText);
                sendResponse(chatId, response);
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(chatId, "Sorry, I had trouble answering that. detailed error: " + e.getMessage());
            }
        }
    }

    private void sendResponse(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @org.springframework.context.annotation.Bean
    public org.telegram.telegrambots.meta.TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        org.telegram.telegrambots.meta.TelegramBotsApi botsApi = new org.telegram.telegrambots.meta.TelegramBotsApi(
                org.telegram.telegrambots.updatesreceivers.DefaultBotSession.class);
        botsApi.registerBot(this);
        System.out.println("Telegram Bot Registered Successfully!");
        return botsApi;
    }
}
