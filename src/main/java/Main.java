import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) throws Exception {
        // Создание SessionFactory
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry)
                .getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();

        // Создание WorkWithCustomerDB
        WorkWithCustomerDB workWithCustomerDB = new WorkWithCustomerDB(sessionFactory);

        // Пример использования (закомментируйте, если не нужно тестировать)
        // workWithCustomerDB.createCustomer("Test Name", 1234567890L, "Moscow", "Type1", LocalDateTime.now());

        // Создание и регистрация бота
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot(workWithCustomerDB); // Передаем workWithCustomerDB в конструктор бота
        telegramBotsApi.registerBot(bot);

        // Закрытие SessionFactory при завершении
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }));
    }
}