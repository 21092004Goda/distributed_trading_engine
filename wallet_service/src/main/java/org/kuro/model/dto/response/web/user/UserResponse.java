package org.kuro.model.dto.response.web.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    UUID userId;

    String username;

    String email;

    String PasswordHash;

    String status;

    Timestamp createAt;

    Timestamp updateAt;

}
