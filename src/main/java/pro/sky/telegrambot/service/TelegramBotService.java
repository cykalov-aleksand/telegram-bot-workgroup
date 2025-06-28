package pro.sky.telegrambot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.OutputData;
import pro.sky.telegrambot.model.Statistic;
import pro.sky.telegrambot.model.UserParameter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Objects;

@Service
public class TelegramBotService {
    private final TelegramBot telegramBot;


    public TelegramBotService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    OkHttpClient client = new OkHttpClient();
    ObjectMapper objectUserParameterMapper = new ObjectMapper();
    ObjectMapper objectOutputDataMapper = new ObjectMapper();


    private Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

public void requestClearCache(Long chatId) throws IOException {
    SendMessage message = new SendMessage(String.valueOf(chatId), requestPost("management/clear-caches",""));
    controlSendingControl(telegramBot.execute(message));
}
    public void sendingMessage(Long chatId) throws IOException {

        String jsonStringReference = request("rule/stats");
        List<Statistic> statistics = objectOutputDataMapper.readValue(jsonStringReference, new TypeReference<>() {
        });
        StringBuilder text = new StringBuilder();
        for (Statistic variable : statistics) {
            text.append(variable.toString()).append("\n");
        }
        SendMessage message = new SendMessage(String.valueOf(chatId), "Привет" + "\n" +
                " справка по статистике срабатывания правил рекомендаций:\n\n" + text);
        controlSendingControl(telegramBot.execute(message));
    }

    public void receivingId(Long chatId, String messageText) throws IOException {

        String[] stringArray = messageText.split(" ");
        String comments;
        if (stringArray.length != 2) {
            comments = "Не верно введено имя пользователя";
        } else {
            try {
                String jsonStringFirstLastName = request("recommendation/username/" + stringArray[1]);
                UserParameter userParameter = objectUserParameterMapper.readValue(jsonStringFirstLastName,
                        UserParameter.class);
                String jsonStringRecommendedProducts = request("recommendation/dynamic/" + userParameter.getId());
                List<OutputData> outputData = objectOutputDataMapper.readValue(jsonStringRecommendedProducts,
                        new TypeReference<>() {
                        });
                StringBuilder textProductParameters = new StringBuilder();
                for (OutputData variable : outputData) {
                    textProductParameters.append("Продукт № (ID) - ").append(variable.getId()).append(" ;\n\n")
                            .append(" Название продукта: ").append(variable.getName()).append(" ;\n\n")
                            .append("Описание продукта : ").append(variable.getText()).append("\n\n");
                }
                comments = "Здравствуйте "+userParameter.getFirstName() + "  " + userParameter.getLastName() + "\n\n" +
                        "новые продукты для Вас:\n"+textProductParameters;
            } catch (Exception e) {
                comments = "Пользователь не найден";
            }
        }
        SendMessage message = new SendMessage(String.valueOf(chatId), comments);
        controlSendingControl(telegramBot.execute(message));

    }
    private String requestPost(String way,String stringJson)throws IOException {
        String result = "";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), stringJson);
// Создаём запрос на основе URL и RequestBody
        Request request = new Request.Builder()
                .url("http://localhost:8081/" + way)
                .post(body)
                .build();
// Выполняем запрос и получаем ответ
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                if(response.code()==200){
 result="запрос выполнен удачно кеш очищен";
                }
             else {
                result= "Ошибка: " + response.code();
            }}}
    return result;}



    private String request(String way) throws IOException {
        Request request = new Request.Builder().url("http://localhost:8081/" + way).build();
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

