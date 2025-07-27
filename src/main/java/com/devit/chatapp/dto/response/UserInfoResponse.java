package com.devit.chatapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private String email;
    private String firstName;
    private String lastName;
    private String userName;
    private String keycloakId;

    private String roles;

}
