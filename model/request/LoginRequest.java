package com.bridge.herofincorp.model.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginRequest {
    @NotNull
    @NotBlank
    @Pattern(regexp = "(^$|[0-9]{10})",message = "Phone no. must be of 10 digits")
    private String phone;
    @NotNull
    @NotBlank
    private String appName;
}