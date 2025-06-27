package pro.sky.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserParameter {
    private UUID id;
    private String firstName;
    private String lastName;

    public UserParameter() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
