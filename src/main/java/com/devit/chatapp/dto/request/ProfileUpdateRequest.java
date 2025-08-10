package com.devit.chatapp.dto.request;

import com.devit.chatapp.annotations.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProfileUpdateRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank
    @ValidEmail
    private String email;
}
