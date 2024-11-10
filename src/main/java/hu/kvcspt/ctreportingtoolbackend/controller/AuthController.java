package hu.kvcspt.ctreportingtoolbackend.controller;

import hu.kvcspt.ctreportingtoolbackend.dto.UserDTO;
import hu.kvcspt.ctreportingtoolbackend.logic.UserService;
import hu.kvcspt.ctreportingtoolbackend.logic.auth.AuthService;
import hu.kvcspt.ctreportingtoolbackend.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public final class AuthController {

    private AuthService authService;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserDetails> loginUser(@RequestBody User user) {
        return new ResponseEntity<>(authService.loadUserByUsername(user.getUsername()), HttpStatus.OK) ;
    }

    @PostMapping("/register")
    public UserDTO addUser(@RequestBody UserDTO user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.createUser(user);
    }
}
