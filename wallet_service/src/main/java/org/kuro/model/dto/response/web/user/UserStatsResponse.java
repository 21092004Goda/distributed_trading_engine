package org.kuro.model.dto.response.web.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsResponse {

    private int totalUsers;

    private int activeUsers;

    private int pendingUsers;

    private int suspendedUsers;

    private int usersCreatedToday;

}
