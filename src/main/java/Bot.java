import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private SessionFactory sessionFactory = null;
    private Session session = null;
    private Transaction transaction = null;

    private WorkWithCustomerDB workWithCustomerDB;
    // Конструктор с параметрами
    public Bot(WorkWithCustomerDB workWithCustomerDB) {
        this.workWithCustomerDB = workWithCustomerDB;
    }

    private String name;
    private Long phone;
    private String city;
    private String type;
    private LocalDateTime timeToContact;

    private Boolean isTimeYourself = false;
    private Boolean isCreateUser = false;

    //TODO Состояния ввода данных о клиенте (и не только)
    GetMessageState getMessageState = new GetMessageState();

    //TODO Кнопка для создания пользователя
    private final InlineKeyboardButton buttonForCreateCustomer = InlineKeyboardButton.builder()
            .text("➕ Создать клиента")
            .callbackData("create_customer")
            .build();
    //TODO Кнопка для клиентов под вопросом
    private final InlineKeyboardButton buttonForQuestionableCustomers = InlineKeyboardButton.builder()
            .text("❓ Клиенты на рассмотрении")
            .callbackData("questionable_customers")
            .build();
    //TODO Кнопка для создания пользователя
    private final InlineKeyboardButton buttonForArchiveCustomers = InlineKeyboardButton.builder()
            .text("\uD83D\uDCC1 Архив клиентов")
            .callbackData("create_customer")
            .build();
    //TODO Кнопка для создания пользователя
    private final InlineKeyboardButton buttonForRedactCustomer = InlineKeyboardButton.builder()
            .text("✏\uFE0F Редактировать клиента")
            .callbackData("redact_customer")
            .build();


    //TODO Клавиатура для главного меню
    private final InlineKeyboardMarkup keyboardForMainMenu = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForCreateCustomer))
            .keyboardRow(List.of(buttonForRedactCustomer))
            .keyboardRow(List.of(buttonForQuestionableCustomers))
            .keyboardRow(List.of(buttonForArchiveCustomers))
            .build();


    private final InlineKeyboardButton buttonForChangeName = InlineKeyboardButton.builder()
            .text("\uD83D\uDCDD  Имя")
            .callbackData("change_name")
            .build();

    private final InlineKeyboardButton buttonForChangeNumber = InlineKeyboardButton.builder()
            .text("\uD83D\uDCDE  Номер телефона")
            .callbackData("change_number")
            .build();

    private final InlineKeyboardButton buttonForChangeEstate = InlineKeyboardButton.builder()
            .text("\uD83C\uDFE0  Тип недвижимости")
            .callbackData("change_estate")
            .build();

    private final InlineKeyboardButton buttonForChangeCity = InlineKeyboardButton.builder()
            .text("\uD83D\uDCCD  Город")
            .callbackData("change_city")
            .build();

    private final InlineKeyboardButton buttonForChangeRealtor = InlineKeyboardButton.builder()
            .text("\uD83D\uDC64  Передать агенту")
            .callbackData("change_realtor")
            .build();

    private final InlineKeyboardButton buttonForChangeTime = InlineKeyboardButton.builder()
            .text("⏳  Время контакта")
            .callbackData("change_time")
            .build();

    private final InlineKeyboardMarkup keyboardForEdit = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(buttonForChangeName, buttonForChangeNumber))
            .keyboardRow(List.of(buttonForChangeEstate, buttonForChangeCity))
            .keyboardRow(List.of(buttonForChangeRealtor, buttonForChangeTime))
            .build();

    //TODO Чтобы не писать каждый раз блок try/catch
    public void tryCatch(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void forWorkWithText(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String textMessage = update.getMessage().getText();

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("")
                    .build();

            System.out.println("Текст от пользователя " + chatId + ": " + textMessage);
            if (textMessage.equals("/start")) {
                isTimeYourself = false;
                sendMessage.setText("\uD83C\uDFE0 Вас приветствует чат-бот для агентов по недвижимости!\n\nБот предоставляет возможности:\n        • по созданию/редактированию клиентов в базе" +
                        "\n        • по автоматическому созданию напоминаний о звонках\n        • по передаче клиентов другому агенту\n        • по управлению базой клиентов" +
                        "\n______________________________________\nВыберите действие из меню ниже:");
                sendMessage.setReplyMarkup(keyboardForMainMenu);

                //TODO Получение имени и переход в получение номера
            } else if (getMessageState.isCreateCustomer() && getMessageState.isWaitingName()) {
                name = textMessage;
                getMessageState.setWaitingName(false);
                getMessageState.setWaitingPhone(true);
                sendMessage.setText("\uD83D\uDCF1 Телефон:\n\nНачинайте с \"8\", затем 10 цифр.  Пример: 89181234567\n\uD83D\uDC47Введите номер телефона: ");

                //TODO Получение номера и переход в получение города
            } else if (getMessageState.isCreateCustomer() && getMessageState.isWaitingPhone()) {
                try {
                    phone = Long.parseLong(textMessage);
                    if (textMessage.length() != 11 || !textMessage.startsWith("8")) {
                        sendMessage.setText("Номер должен состоять из 11 цифр и начинаться с 8.\nПример: 89181234567\nПожалуйста, введите номер еще раз:");
                        getMessageState.setWaitingPhone(true);
                    } else {
                        getMessageState.setWaitingPhone(false);
                        getMessageState.setWaitingCity(true);
                        sendMessage.setText("\uD83D\uDCCD Город:\n\nВведите город, в котором клиент хочет купить недвижимость:");
                    }
                } catch (NumberFormatException e) {
                    sendMessage.setText("Некорректный номер телефона. Пожалуйста, введите только цифры:\nПример: 89181234567");
                    getMessageState.setWaitingPhone(true);
                }

                //TODO Получение города и переход в получение типа недвижимости
            } else if (getMessageState.isCreateCustomer() && getMessageState.isWaitingCity()) {
                city = textMessage;
                getMessageState.setWaitingCity(false);
                getMessageState.setWaitingType(true);
                InlineKeyboardButton atelier = InlineKeyboardButton.builder()
                        .text("\uD83D\uDECB\uFE0F Студия")
                        .callbackData("Студия")
                        .build();
                InlineKeyboardButton one_room = InlineKeyboardButton.builder()
                        .text("1\uFE0F⃣  1-комнатная")
                        .callbackData("1-комнатная")
                        .build();
                InlineKeyboardButton two_room = InlineKeyboardButton.builder()
                        .text("2\uFE0F⃣  2-комнатная")
                        .callbackData("2-комнатная")
                        .build();
                InlineKeyboardButton three_room = InlineKeyboardButton.builder()
                        .text("3\uFE0F⃣  3-комнатная")
                        .callbackData("3-комнатная")
                        .build();
                InlineKeyboardButton house = InlineKeyboardButton.builder()
                        .text("\uD83C\uDFE1 Дом")
                        .callbackData("Дом")
                        .build();
                InlineKeyboardMarkup keyboardForChooseType = InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(atelier, one_room))
                        .keyboardRow(List.of(two_room, three_room))
                        .keyboardRow(List.of(house))
                        .build();

                sendMessage.setReplyMarkup(keyboardForChooseType);
                sendMessage.setText("\uD83C\uDFD8\uFE0F Тип недвижимости:\n\nВыберите тип недвижимости, которую ищет клиент:");

                //TODO Если пользователь решил выбрать время сам - получаем время в часах
            } else if (isTimeYourself && getMessageState.isWaitingTime()) {
                try {
                    int hours = Integer.parseInt(textMessage);
                    timeToContact = LocalDateTime.now().plusHours(hours);

                    // TODO Создаем пользователя
                    isCreateUser = true;
                    Customer customer = new Customer(name, phone, city, type, timeToContact);
                    sendMessage.setText("✅Клиент создан!\n_____________________________\n\n" +
                            customer + "\n_____________________________\n\nВыберите следующее действие:");
                    sendMessage.setReplyMarkup(keyboardForMainMenu);

                    // TODO Сбрасываем состояния
                    getMessageState.setCreateCustomer(false);
                    getMessageState.setWaitingTime(false);
                    isTimeYourself = false;

                } catch (NumberFormatException e) {
                    sendMessage.setText("Некорректный формат числа. Пожалуйста, введите количество часов (только цифры):");
                }
            }
            tryCatch(sendMessage);
        }
    }

    public void forWorkWithButtons(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            EditMessageText editMessageText = EditMessageText.builder()
                    .text("")
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();

            try {
                if (callbackData.equals(buttonForRedactCustomer.getCallbackData())) {
                    editMessageText.setText("✏\uFE0F Редактирование клиента\n\nВыберите что хотите изменить:");
                    editMessageText.setReplyMarkup(keyboardForEdit);
                    try {
                        execute(editMessageText);
                    }catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                } else if (callbackData.equals("create_customer")) {
                    //TODO Начало создания пользователя
                    getMessageState.setCreateCustomer(true);
                    getMessageState.setWaitingName(true);
                    editMessageText.setText("✨ СОЗДАНИЕ КЛИЕНTA ✨\n\nСейчас зададим вам несколько вопросов, чтобы всё было точно." +
                            "\n\uD83D\uDC47 Введите имя клиента:");
                    execute(editMessageText);

                } else if (getMessageState.isCreateCustomer() &&
                        (callbackData.equals("Студия") ||
                                callbackData.equals("1-комнатная") ||
                                callbackData.equals("2-комнатная") ||
                                callbackData.equals("3-комнатная") ||
                                callbackData.equals("Дом"))) {

                    //TODO Обработка выбора типа недвижимости
                    type = callbackData;
                    getMessageState.setWaitingType(false);
                    getMessageState.setWaitingTime(true);

                    if (!isTimeYourself) {
                        //TODO Создаем клавиатуру для выбора времени
                        InlineKeyboardButton buttonFor1h = InlineKeyboardButton.builder()
                                .text("⏱\uFE0F1 час")
                                .callbackData("1_hour")
                                .build();
                        InlineKeyboardButton buttonFor2h = InlineKeyboardButton.builder()
                                .text("⏱\uFE0F2 часа")
                                .callbackData("2_hours")
                                .build();
                        InlineKeyboardButton buttonFor3h = InlineKeyboardButton.builder()
                                .text("⏱\uFE0F8 часов")
                                .callbackData("8_hours")
                                .build();
                        InlineKeyboardButton buttonFor24h = InlineKeyboardButton.builder()
                                .text("⏱\uFE0F24 часа")
                                .callbackData("24_hours")
                                .build();
                        InlineKeyboardButton buttonFor2Days = InlineKeyboardButton.builder()
                                .text("⏱\uFE0F2 дня")
                                .callbackData("2_days")
                                .build();
                        InlineKeyboardButton buttonForChooseForYourself = InlineKeyboardButton.builder()
                                .text("✏\uFE0F Указать свое время")
                                .callbackData("choose_yourself")
                                .build();

                        InlineKeyboardMarkup keyboardForChooseTime = InlineKeyboardMarkup.builder()
                                .keyboardRow(List.of(buttonFor1h, buttonFor2h))
                                .keyboardRow(List.of(buttonFor3h, buttonFor24h))
                                .keyboardRow(List.of(buttonFor2Days))
                                .keyboardRow(List.of(buttonForChooseForYourself))
                                .build();

                        editMessageText.setText("⏳ Время для напоминания:\n\nЧерез сколько планируете связаться с клиентом?");
                        editMessageText.setReplyMarkup(keyboardForChooseTime);
                        execute(editMessageText);
                    }

                } else if (getMessageState.isCreateCustomer() && getMessageState.isWaitingTime()) {
                    //TODO Обработка выбора времени
                    if (!callbackData.equals("choose_yourself")) {
                        timeToContact = LocalDateTime.now();

                        if (callbackData.equals("1_hour")) {
                            timeToContact = timeToContact.plusHours(1);
                        } else if (callbackData.equals("2_hours")) {
                            timeToContact = timeToContact.plusHours(2);
                        } else if (callbackData.equals("8_hours")) {
                            timeToContact = timeToContact.plusHours(8);
                        } else if (callbackData.equals("24_hours")) {
                            timeToContact = timeToContact.plusHours(24);
                        } else if (callbackData.equals("2_days")) {
                            timeToContact = timeToContact.plusHours(48);
                        }

                        //TODO Создаем пользователя
                        Customer customer = new Customer(name, phone, city, type, timeToContact);

                        isCreateUser = true;

                        getMessageState.setCreateCustomer(false);
                        getMessageState.setWaitingTime(false);
                        isTimeYourself = false;

                        editMessageText.setText("✅Клиент создан!\n_____________________________\n\n" +
                                customer + "\n_____________________________\n\nВыберите следующее действие: ");
                        editMessageText.setReplyMarkup(keyboardForMainMenu);
                        try {
                            execute(editMessageText);
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else {
                        isTimeYourself = true;
                        editMessageText.setText("Введите количество часов, через которое нужно обратиться к клиенту:");
                        editMessageText.setReplyMarkup(null);
                        try {
                            execute(editMessageText);
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "@MatosyanTGBot";
    }

    @Override
    public String getBotToken() {
        return "8004012680:AAEfvyYY8R44wFfIGunrWkTFaowWxH5-zbE";
    }

    @Override
    public void onUpdateReceived(Update update) {
        forWorkWithText(update);
        forWorkWithButtons(update);
        if(isCreateUser){
            workWithCustomerDB.createCustomer(name, phone, city, type, timeToContact);
            isCreateUser = false;
        }
    }
}