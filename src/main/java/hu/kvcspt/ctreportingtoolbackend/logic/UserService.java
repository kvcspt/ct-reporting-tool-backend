package hu.kvcspt.ctreportingtoolbackend.logic;

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
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public User getUserById(Long id){
        return userRepository.getReferenceById(id);
    }
    public User updateUser(User user){
        if(userRepository.existsById(user.getId())){
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("Section not found!");
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public void deleteUser(User user){
        userRepository.delete(user);
        log.debug("Section is deleted successfully");
    }
}
