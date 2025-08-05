package com.insurance.app.auth.dto;

import com.insurance.app.auth.Entity.Roles;

//lombok
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
	private String name;
    private String email;
    private String password;
    private Roles role;
    private String panNumber;
}
