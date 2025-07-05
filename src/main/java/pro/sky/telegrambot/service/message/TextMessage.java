package pro.sky.telegrambot.service.message;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.model.InfoBuild;
import pro.sky.telegrambot.model.OutputData;
import pro.sky.telegrambot.model.Statistic;
import pro.sky.telegrambot.model.UserParameter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Класс, методы которого позволяют сформировать текстовую информацию по полученной информации от приложения homeworkgroup,
 * и вернуть ее объекту класса TelegramBotService.
 */
@Component
public class TextMessage {

    OkHttpClient client = new OkHttpClient();
    ObjectMapper objectUserParameterMapper = new ObjectMapper();
    ObjectMapper objectOutputDataMapper = new ObjectMapper();
    ObjectMapper objectInfoBuilderMapper = new ObjectMapper();

    /**
     * Метод позволяющий сформировать текстовое сообщение при получении команды "/help".
     */
    public String textHelp() {
        return "Команды отрабатываемые нашим ботом: \n/start  - бот приветствует пользователя и печатает справку;\n" +
                "/recomend <имя пользователя>  - команда возвращает рекомендации для пользователя;\n" +
                "/management/clear-caches  - команда очищает все закешированные результаты;\n\n" +
                "/management/info - команда выводит имя сервиса и версию сервиса;\n";
    }

    /**
     * Метод организующий формирование запроса в приложение "homeworkgroup" по адресу "rule/stats" и получение ответа от него с последующей
     * передачей текстовой информации в объект класса TelegramBotService.
     */
    public String messageStart() throws IOException {
        String jsonStringReference = request("rule/stats");
        List<Statistic> statistics = objectOutputDataMapper.readValue(jsonStringReference, new TypeReference<>() {
        });
        StringBuilder text = new StringBuilder();
        for (Statistic variable : statistics) {
            text.append(variable.toString()).append("\n");
        }
        return String.valueOf(text);
    }

    /**
     * Метод организующий отправку запросов в приложение "homeworkgroup".
     */
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

    /**
     * Метод организующий формирование запроса в приложение "homeworkgroup" для получения
     * информации о версии приложения.
     */
    public String infoMessage(String service) throws IOException {
        String jsonStringInfoService = request(service.substring(1));
        InfoBuild infoBuild = objectInfoBuilderMapper.readValue(jsonStringInfoService, InfoBuild.class);
        return infoBuild.toString();
    }

    /**
     * Метод организующий формирование запроса в приложение "homeworkgroup" по адресу "recommendation/username/"
     * и получения информации от него с последующим преобразованием к строковому типу и выдачи потребителю.
     */
    public String messageRecommendations(String messageText) {
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
                if (!outputData.isEmpty()) {
                    StringBuilder textProductParameters = new StringBuilder();
                    for (OutputData variable : outputData) {
                        textProductParameters.append("Продукт № (ID) - ").append(variable.getId()).append(" ;\n\n")
                                .append(" Название продукта: ").append(variable.getName()).append(" ;\n\n")
                                .append("Описание продукта : ").append(variable.getText()).append("\n\n");
                    }
                    comments = "Здравствуйте " + userParameter.getFirstName() + "  " + userParameter.getLastName() + "\n\n" +
                            "новые продукты для Вас:\n" + textProductParameters;
                } else {
                    comments = "Здравствуйте " + userParameter.getFirstName() + "  " + userParameter.getLastName() + "\n\n" +
                            "рекомендованных продуктов для Вас нет\n";
                }
            } catch (Exception e) {
                comments = "Пользователь не найден";
            }
        }
        return comments;
    }

    /**
     * Метод для отправки POST запроса на очистку кеш-памяти, и получения ответа от него с последующей выдачей потребителю
     * и получения информации от него с последующим преобразованием к строковому типу и выдачи потребителю.
     */
    public String requestPost(String way, String stringJson) throws IOException {
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
                if (response.code() == 200) {
                    result = "запрос выполнен удачно кеш очищен";
                } else {
                    result = "Ошибка: " + response.code();
                }
            }
        }
        return result;
    }

}
