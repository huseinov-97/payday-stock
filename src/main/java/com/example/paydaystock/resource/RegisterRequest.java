package com.example.paydaystock.resource;

import com.example.paydaystock.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String username;
    private String name;
    private String phoneNumber;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @ValidPassword(message = "invalid")
    @NotNull
    @NotBlank
    private String password;

    private List<String> authority;
}
