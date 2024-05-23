package advanced.algorithms.programming.tailoredsocialnetwork.dto.mapper;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.user.UserDTO;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;

public class UserMapper {
    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .username(user.getUsername())
            .picture(user.getPicture())
            .role(user.getRole())
            .build();
    }
}

