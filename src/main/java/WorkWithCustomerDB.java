import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class WorkWithCustomerDB {

    private SessionFactory sessionFactory;

    public WorkWithCustomerDB(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // TODO Создание клиента
    public void createCustomer(String name, Long phone, String city, String type, LocalDateTime timeToContact) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Customer user = new Customer(name, phone, city, type, timeToContact);
            session.save(user);

            transaction.commit();
            System.out.println("Клиент сохранен!");

        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Ошибка сохранения: " + ex.getMessage());
            System.out.println(ex.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // TODO Удаление клиента
    public void deleteCustomer(Integer id) {
        Session session = null;
        Transaction transaction = null;

        try {
            Customer customer = session.get(Customer.class, id);
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            if(customer != null){
                session.delete(customer);
                transaction.commit();
                System.out.println("Клиент с id: " + id + " удален");
            }else {
                System.out.println("Клиент с id: " + id + " не найден");
            }
        }catch (Exception ex){
            if(transaction != null){
                transaction.rollback();
                System.out.println("Ошибка удаления клиента");
                System.out.println(ex.getMessage());
            }
        } finally{
            if(session != null){
                session.close();
            }
        }
    }
}