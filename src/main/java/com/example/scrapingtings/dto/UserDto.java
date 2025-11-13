package com.example.scrapingtings.dto;

import com.example.scrapingtings.model.User;

/**
 * DTO used to safely expose user data to the frontend without exposing sensitive fields
 * like password or roles.
 */
public class UserDto {
    private int id;
    private String username;
    private String personalName;
    private String personalEmail;
    private String personalAddress;
    private String profileText;

    public UserDto() {
    }

    /**
     * Factory method to map the JPA entity to the safe DTO.
     */
    public static UserDto fromEntity(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());

        // Assuming your User entity has these getters:
        dto.setPersonalName(user.getPersonalName());
        dto.setPersonalEmail(user.getPersonalEmail());
        dto.setPersonalAddress(user.getPersonalAddress());
        dto.setProfileText(user.getProfileText());
        return dto;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getPersonalAddress() {
        return personalAddress;
    }

    public void setPersonalAddress(String personalAddress) {
        this.personalAddress = personalAddress;
    }

    public String getProfileText() {
        return profileText;
    }

    public void setProfileText(String profileText) {
        this.profileText = profileText;
    }
}