package com.geotrip.authservice.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    @NotBlank(message = "Please enter valid email")
    private String email;


    @NotBlank(message = "Please enter valid password")
    private String password;

}
