package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.message.TextMessage;

import java.io.IOException;


@Service
public class TelegramBotService {
    private final TelegramBot telegramBot;
    private final TextMessage textMessage;


    public TelegramBotService(TelegramBot telegramBot, TextMessage textMessage) {
        this.telegramBot = telegramBot;
        this.textMessage = textMessage;
    }

    private Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    public void requestClearCache(Long chatId) throws IOException {
        SendMessage message = new SendMessage(String.valueOf(chatId),
                textMessage.requestPost("management/clear-caches", ""));
        controlSendingControl(telegramBot.execute(message));
    }

    public void sendingMessage(Long chatId) throws IOException {
        SendMessage message = new SendMessage(String.valueOf(chatId), "Привет" + "\n" +
                " справка по статистике срабатывания правил рекомендаций:\n\n" + textMessage.messageStart());
        controlSendingControl(telegramBot.execute(message));
    }

    public void receivingId(Long chatId, String messageText) throws IOException {
        SendMessage message = new SendMessage(String.valueOf(chatId), textMessage.messageRecommendations(messageText));
        controlSendingControl(telegramBot.execute(message));

    }
    public void infoMessage(Long chatId, String string) throws IOException {
        String[] string1 = string.split("/");
        if (string1[2].equals("clear-caches")) {
            requestClearCache(chatId);
        } else {
            SendMessage message = new SendMessage(String.valueOf(chatId), textMessage.infoMessage(string));
            controlSendingControl(telegramBot.execute(message));
        }
    }

    private void controlSendingControl(SendResponse sendResponse) {
        if (sendResponse.isOk()) {
            logger.info("Сообщение отправлено");
            return;
        }
        logger.info("Ошибка, сообщение не отправлено");
    }
}

