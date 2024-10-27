package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.UserDTO;
import hu.kvcspt.ctreportingtoolbackend.model.User;
import hu.kvcspt.ctreportingtoolbackend.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToDTO).toList();
    }
    public UserDTO getUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User ID does not exist!"));
        return convertToDTO(user);
    }
    public UserDTO updateUser(UserDTO user){
        if(userRepository.existsById(user.getId())){
            return convertToDTO(userRepository.save(fromUserDTO(user)));
        }
        throw new IllegalArgumentException("User not found!");
    }

    public UserDTO createUser(UserDTO user){
        return convertToDTO(userRepository.save(fromUserDTO(user)));
    }

    public void deleteUser(UserDTO userDTO){
        userRepository.delete(fromUserDTO(userDTO));
        log.debug("User is deleted successfully");
    }
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .password(user.getPassword())
                .userName(user.getUsername())
                .title(user.getTitle())
                .role(user.getRole())
                .build();
    }

    // Method to convert UserDTO to User entity
    private User fromUserDTO(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .userName(userDTO.getUserName())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .title(userDTO.getTitle())
                .build();
    }
}
