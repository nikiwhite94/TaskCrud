package ru.nikiwhite.telegrambotservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.nikiwhite.telegrambotservice.models.Employee;
import ru.nikiwhite.telegrambotservice.service.EmployeeService;
import ru.nikiwhite.telegrambotservice.service.TelegramBotService;

@Component
@EnableRabbit
public class RabbitMQListener {

    @Value("${telegram.chatId}")
    private Long CHAT_ID;

    private final EmployeeService employeeService;
    private final TelegramBotService telegramBotService;

    Logger logger = LoggerFactory.getLogger(RabbitMQListener.class);

    public RabbitMQListener(EmployeeService employeeService, TelegramBotService telegramBotService) {
        this.employeeService = employeeService;
        this.telegramBotService = telegramBotService;
    }

    @RabbitListener(queues = "myQueue1")
    public void saveEmployee(Employee employee) {
        employeeService.saveEmployee(employee);
        logger.info("Пользователь {} сохранен в базу данных", employee);
        telegramBotService.sendMessage(employee.getHeadId(),
                "Пользователь " + employee.getName() + " " + employee.getSurname() + " сохранен в базу данных");
    }

    @RabbitListener(queues = "myQueue2")
    public void errorMessages(String message) {
        telegramBotService.sendMessage(CHAT_ID, message);
    }

    @RabbitListener(queues = "myQueue3")
    public void messageForUser(String message) {
        telegramBotService.sendMessage(CHAT_ID, message);
    }

    @RabbitListener(queues = "myQueue4")
    public void avgRequest(Employee employee) {
        telegramBotService.sendMessage(employee.getHeadId(),
                "Был производен поиск средней зарплаты по пользователю " +
                        employee.getName() + " " + employee.getMiddleName() + " " + employee.getSurname());
    }
}