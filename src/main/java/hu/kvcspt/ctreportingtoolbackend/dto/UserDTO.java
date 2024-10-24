package hu.kvcspt.ctreportingtoolbackend.dto;

import hu.kvcspt.ctreportingtoolbackend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String userName;
    private String password;
    private String name;
    private String title;
    private Role role;
}
