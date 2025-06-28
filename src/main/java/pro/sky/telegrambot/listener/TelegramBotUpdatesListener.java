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
            String messageText;
            Long chatId = update.message().chat().id();
            if (update.message() != null && update.message().text() != null) {
                messageText = update.message().text();
                try {
                    if (messageText.startsWith("/start")) {
                        telegramBotService.sendingMessage(chatId);
                        if (messageText.startsWith("/recommend")) {
                            telegramBotService.receivingId(chatId, messageText);
                        }
                        if (messageText.startsWith("/management/clear-caches")) {
                            telegramBotService.requestClearCache(chatId);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);

                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
