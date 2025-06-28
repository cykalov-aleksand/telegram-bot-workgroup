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

@Component
public class TextMessage {

    OkHttpClient client = new OkHttpClient();
    ObjectMapper objectUserParameterMapper = new ObjectMapper();
    ObjectMapper objectOutputDataMapper = new ObjectMapper();
    ObjectMapper objectInfoBuilderMapper = new ObjectMapper();

    public String textHelp(){
       return "Команды отрабатываемые нашим ботом: \n/start  - бот приветствует пользователя и печатает справку;\n"+
                "/recomend <имя пользователя>  - команда возвращает рекомендации для пользователя;\n" +
                "/management/clear-caches  - команда очищает все закешированные результаты;\n\n"+
                "/management/info/changeService - команда выводит имя сервиса ChangeService и версию сервиса;\n\n"+
                "/management/info/clientService - команда выводит имя сервиса ClientService и версию сервиса;\n\n"+
                "/management/info/dinamicClientService - команда выводит имя сервиса DinamicClientService и версию сервиса;\n\n"+
                "/management/info/request/dinamicService - команда выводит имя сервиса DinamicService и версию сервиса;\n\n"+
                "/management/info/request/ruleService - команда выводит имя сервиса RuleService и версию сервиса;\n\n"+
                "/management/info/logic/logic - команда выводит имя компонента Logic и версию сервиса;\n\n"+
                "/management/info/logic/logicDinamic - команда выводит имя компонента LogicDinamic и версию сервиса;\n\n";
    }
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

    public String infoMessage(String service) throws IOException {
        System.out.println(service);
        String jsonStringInfoService = request(service.substring(1));
        InfoBuild infoBuild = objectInfoBuilderMapper.readValue(jsonStringInfoService, InfoBuild.class);
        return "Имя сервиса: " + infoBuild.getName() + "\n" + "№ версии: " + infoBuild.getVersion();
    }

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
                StringBuilder textProductParameters = new StringBuilder();
                for (OutputData variable : outputData) {
                    textProductParameters.append("Продукт № (ID) - ").append(variable.getId()).append(" ;\n\n")
                            .append(" Название продукта: ").append(variable.getName()).append(" ;\n\n")
                            .append("Описание продукта : ").append(variable.getText()).append("\n\n");
                }
                comments = "Здравствуйте " + userParameter.getFirstName() + "  " + userParameter.getLastName() + "\n\n" +
                        "новые продукты для Вас:\n" + textProductParameters;
            } catch (Exception e) {
                comments = "Пользователь не найден";
            }
        }
        return comments;
    }

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
