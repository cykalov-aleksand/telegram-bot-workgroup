package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.message.TextMessage;

import java.io.IOException;

/**
 * Класс, методы которого позволяют организовать взаимодействие с классом TelegramBotUpdatesListener.
        */

@Service
public class TelegramBotService {
    private final TelegramBot telegramBot;
    private final TextMessage textMessage;


    public TelegramBotService(TelegramBot telegramBot, TextMessage textMessage) {
        this.telegramBot = telegramBot;
        this.textMessage = textMessage;
    }

    private Logger logger = LoggerFactory.getLogger(TelegramBotService.class);
    /**
     * Метод позволяющий отправить информацию в телеграм при получении запроса "management/clear-caches".
     */
    public void requestClearCache(Long chatId) throws IOException {
        SendMessage message = new SendMessage(String.valueOf(chatId),
                textMessage.requestPost("management/clear-caches", ""));
        controlSendingControl(telegramBot.execute(message));
    }
    /**
     * Метод позволяющий отправить информацию в телеграм при получении запроса "/start".
     */
    public void sendingMessage(Long chatId) throws IOException {
        SendMessage message = new SendMessage(String.valueOf(chatId), "Привет" + "\n" +
                " справка по статистике срабатывания правил рекомендаций:\n\n" + textMessage.messageStart() +
                "\nдля ознакомления с командами бота введите команду\n /help ");
        controlSendingControl(telegramBot.execute(message));
    }
    /**
     * Метод позволяющий отправить информацию в телеграм при получении запроса "/recommend username".
     */
    public void receivingId(Long chatId, String messageText) throws IOException {
        SendMessage message = new SendMessage(String.valueOf(chatId), textMessage.messageRecommendations(messageText));
        controlSendingControl(telegramBot.execute(message));

    }
    /**
     * Метод позволяющий провести анализ по полученному запросу "/management", в случае получения запроса
     * "/management/clear-caches" передать управление методу "requestClearCache" в остальных случаях передать
     * управление классу "TextMessage".
     */
    public void infoMessage(Long chatId, String string) throws IOException {
        String[] string1 = string.split("/");
        if (string1[2].equals("clear-caches")) {
            requestClearCache(chatId);
        } else {
            SendMessage message = new SendMessage(String.valueOf(chatId), textMessage.infoMessage(string));
            controlSendingControl(telegramBot.execute(message));
        }
    }
    /**
     * Метод контроля отправки сообщений.
     */
    private void controlSendingControl(SendResponse sendResponse) {
        if (sendResponse.isOk()) {
            logger.info("Сообщение отправлено");
            return;
        }
        logger.info("Ошибка, сообщение не отправлено");
    }
    /**
     * Метод позволяющий отправить информацию в телеграм при получении запроса "/help".
     */
    public void help(Long chatId) {
        SendMessage message = new SendMessage(String.valueOf(chatId), textMessage.textHelp());
        controlSendingControl(telegramBot.execute(message));
    }
}

