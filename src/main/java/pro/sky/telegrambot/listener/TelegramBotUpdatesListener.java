package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.TelegramBotService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final TelegramBotService telegramBotService;
    private final TelegramBot telegramBot;
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    public TelegramBotUpdatesListener(TelegramBotService telegramBotService, TelegramBot telegramBot) {
        this.telegramBotService = telegramBotService;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            //logger.info("Processing update: {}", update);
            //logger.debug("Processing update: {}", update);
            String messageText;
            Long chatId = update.message().chat().id();
            if (update.message() != null && update.message().text() != null) {
                messageText = update.message().text();
                if (messageText.startsWith("/start")) {
                    try {
                        telegramBotService.sendingMessage(chatId, messageText);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
                if (messageText.startsWith("/recommend")){
                    try {
                        telegramBotService.receivingId(chatId,messageText);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }// Process your updates here
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
