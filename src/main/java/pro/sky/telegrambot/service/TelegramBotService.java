package pro.sky.telegrambot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.OutputData;
import pro.sky.telegrambot.model.UserParameter;

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
                " справка по статистике срабатывания правил рекомендаций:\n" + request("rule/stats"));
        controlSendingControl(telegramBot.execute(message));
    }

    public void receivingId(Long chatId, String messageText) throws IOException {
        String stringJoin="";
        ObjectMapper objectMapper=new ObjectMapper();
        ObjectMapper objectMapper1=new ObjectMapper();
        String[] stringArray = messageText.split(" ");
        String comments = "";
        if (stringArray.length != 2) {
            comments = "Не верно введено имя пользователя";
        } else {
           stringJoin = request("recommendation/username/" + stringArray[1]);
            UserParameter userParameter=objectMapper.readValue(stringJoin, UserParameter.class);
String stringJoin2=request("recommendation/dynamic/"+userParameter.getId());
//            ObjectMapper objectMapper1=new ObjectMapper();
//OutputData outputData=objectMapper1.readValue(stringJoin2,OutputData.class);
            comments=userParameter.getFirstName()+"  "+userParameter.getLastName()+"\n"+stringJoin2;
        }
        SendMessage message = new SendMessage(String.valueOf(chatId), comments);
        controlSendingControl(telegramBot.execute(message));

    }

    private String request(String way) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://localhost:8081/" + way)
                .build();
        try (Response response = client.newCall(request).execute()) {
           if (response.isSuccessful()) {
                if (response.code() == 200) {
                    return (Objects.requireNonNull(response.body()).string());
                } else {
               return "Пользователь не найден";
                    }
               }else{
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

