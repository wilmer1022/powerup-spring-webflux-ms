package co.com.wdgg.r2dbc.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChiperPasswordService {
    
    private final PasswordEncoder passwordEncoder;

    public ChiperPasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean passwordsMatch(String password, String encryptedPassword) {
        return passwordEncoder.matches(password, encryptedPassword);
    }
}
