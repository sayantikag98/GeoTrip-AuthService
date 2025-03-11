package com.geotrip.authservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDriverRequestDto {

    @NotBlank(message = "Please enter valid name")
    private String name;


    @NotBlank(message = "Please enter valid email")
    private String email;


    @NotBlank(message = "Please enter valid password")
    private String password;


    @NotBlank(message = "Please enter valid phone number")
    private String phoneNumber;


    @NotBlank(message = "Please enter valid license number")
    private String licenseNumber;

}
