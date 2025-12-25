import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Long phone;
    private String city;
    private String type;
    private LocalDateTime timeToContact;

    public Customer() {
    }

    public Customer(String name, Long phone, String city, String type, LocalDateTime timeToContact) {
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.type = type;
        this.timeToContact = timeToContact;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimeToContact() {
        return timeToContact;
    }

    public void setTimeToContact(LocalDateTime timeToContact) {
        this.timeToContact = timeToContact;
    }

    @Override
    public String toString() {
        return
                "\uD83D\uDCDD Основная информация:\n" +
                        "•     Имя: " + name +
                        "\n•     Телефон: " + phone +
                        "\n•     Город: " + city +
                        "\n•     Тип: " + type +
                        "\n\n⏰ Время следующего контакта: " +
                        "\n        \uD83D\uDCC5 " + timeToContact.getDayOfMonth() + "." + timeToContact.getMonth().getValue() + "." + timeToContact.getYear() +
                        "\n        \uD83D\uDD52 " + timeToContact.getHour() + ":" + timeToContact.getMinute();
    }
}