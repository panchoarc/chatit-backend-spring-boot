package com.devit.chatapp.dto.request;

import com.devit.chatapp.annotations.ValidEmail;
import com.devit.chatapp.annotations.ValidPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
//@Schema(name = "UserRegisterDTO", description = "Request body used for user registration.")
public class UserRegisterDTO {

    @NotBlank(message = "firstname cannot be blank")
    @Size(max = 200, message = "First name cannot have more than 200 letters")
    @JsonProperty("firstname")
    //@Schema(description = "User's first name", example = "John", maxLength = 200)
    private String firstName;

    @NotBlank(message = "lastname cannot be blank")
    @Size(max = 200, message = "lastname cannot have more than 200 characters.")
    @JsonProperty("lastname")
    //@Schema(description = "User's last name", example = "Doe", maxLength = 200)
    private String lastName;

    @NotBlank(message = "email cannot be blank")
    @Size(max = 200, message = "email cannot have more than 200 characters.")
    @ValidEmail
    @JsonProperty("email")
    //@Schema(description = "User's email address", example = "johndoe@example.com", maxLength = 200)
    private String email;

    @NotBlank(message = "username cannot be blank")
    @Size(min = 6, max = 50, message = "username must have between 6 and 50 characters.")
    @JsonProperty("username")
    //@Schema(description = "Username chosen by the user", example = "johndoe123", minLength = 6, maxLength = 50)
    private String userName;

    @NotBlank(message = "password cannot be blank")
    @Size(min = 8, max = 50, message = "Your password must have between 8 and 20 characters")
    @ValidPassword
    @JsonProperty("password")
    //@Schema(description = "Password chosen by the user. Must meet complexity requirements.", example = "MyS3cretPass!", minLength = 8, maxLength = 50)
    private String password;

    //@Schema(hidden = true)
    @JsonProperty("role")
    private String role;
}
