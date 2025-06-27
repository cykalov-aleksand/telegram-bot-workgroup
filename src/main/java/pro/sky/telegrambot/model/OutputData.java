package pro.sky.telegrambot.model;

import java.util.UUID;

public class OutputData {
    private UUID id;
    private String name;
    private String text;
    public OutputData(){}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "OutputData{" +
                "id=" + id +"\n"+
                ", Название продукта ='" + name + '\n' +
                ", Текст продукта='" + text + '\n' +
                '}';
    }
}
