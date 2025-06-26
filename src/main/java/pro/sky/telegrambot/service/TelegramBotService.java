package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
public class TelegramBotService {
    private final TelegramBot telegramBot;


    public TelegramBotService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    private Logger logger = LoggerFactory.getLogger(TelegramBotService.class);


    public void sendingMessage(Long chatId, String string) throws IOException {
        SendMessage message = new SendMessage(String.valueOf(chatId), "Привет" + "\n" +
                " справка по статистике срабатывания правил рекомендаций:\n" + request());
        controlSendingControl(telegramBot.execute(message));
    }

    public void receivingId(Long chatId, String messageText) {
        String[] stringArray = messageText.split(" ");
        String comments = "";
        if (stringArray.length != 2) {
            comments = "Не верно введена имя пользователя";
        }
        SendMessage message = new SendMessage(String.valueOf(chatId), comments);
        controlSendingControl(telegramBot.execute(message));

    }

    private String request() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://localhost:8081/rule/stats")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return (Objects.requireNonNull(response.body()).string());
            } else {
                return "Ошибка: " + response.code();
            }
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

