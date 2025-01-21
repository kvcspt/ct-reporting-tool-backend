package hu.kvcspt.ctreportingtoolbackend.logic;

import hu.kvcspt.ctreportingtoolbackend.dto.UserDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.auth.JwtService;
import hu.kvcspt.ctreportingtoolbackend.mapper.UserMapper;
import hu.kvcspt.ctreportingtoolbackend.model.User;
import hu.kvcspt.ctreportingtoolbackend.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream().map(UserMapper.INSTANCE::fromEntity).collect(Collectors.toList());
    }
    public UserDTO getUserById(@NonNull Long id){
        return userRepository.findById(id).map(UserMapper.INSTANCE::fromEntity).orElseThrow(() -> new NoSuchElementException("User with " + id + "id does not exist!"));
    }

    public UserDTO getUserFromContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User with " + username + "username does not exist!"));
        return UserMapper.INSTANCE.fromEntity(user);
    }
    public UserDTO updateUser(@NonNull UserDTO userDTO){
        User existingUser = userRepository
                .findById(userDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        User newUser = UserMapper.INSTANCE.toEntity(userDTO);

        existingUser.setName(newUser.getName());
        existingUser.setPassword(newUser.getPassword());
        existingUser.setRole(newUser.getRole());
        existingUser.setTitle(newUser.getTitle());

        userRepository.save(existingUser);
        return UserMapper.INSTANCE.fromEntity(existingUser);
    }

    public UserDTO createUser(@NonNull UserDTO userDTO){
        User user = UserMapper.INSTANCE.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return UserMapper.INSTANCE.fromEntity(savedUser);
    }

    public void deleteUser(@NonNull Long id){
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            log.debug("User is deleted successfully");

        } else throw new NoSuchElementException("User not found with id: " + id);
    }

    public String verify(UserDTO userDTO){
        User user = UserMapper.INSTANCE.toEntity(userDTO);
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(authentication.isAuthenticated()){
            User dbUser = userRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            String role = dbUser.getRole().toString();
            return jwtService.generateToken(user.getUsername(), role, dbUser.getId());
        }
        return "Failed";
    }
}
