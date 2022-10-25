package ru.nikiwhite.telegrambotservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.nikiwhite.telegrambotservice.service.TelegramBotService;

@Configuration
public class TelegramBotConfiguration {

    private final TelegramBotService telegramBotService;

    public TelegramBotConfiguration(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramBotService);
    }
}
