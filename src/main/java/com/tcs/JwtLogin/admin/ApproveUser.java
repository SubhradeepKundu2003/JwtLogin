package com.tcs.JwtLogin.admin;

import com.tcs.JwtLogin.models.User;
import com.tcs.JwtLogin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class ApproveUser {

    @Autowired
    private UserRepository userRepository;

    @PutMapping("/approve/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok("User approved successfully");
    }

    @DeleteMapping("/reject/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectUser(@PathVariable Long userId) {

        if (!userRepository.existsById(userId)) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<User> u = userRepository.findById(userId);
        if(u.isPresent()){
            User user = u.get();
            if(!user.isEnabled()){
                userRepository.deleteById(userId);
                return ResponseEntity.ok("User rejected and deleted");
            }
            else{
                return ResponseEntity.status(400).body("User already active");
            }
        }else{
           return ResponseEntity.status(400).body("User not found");
        }
    }

}
