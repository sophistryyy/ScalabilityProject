package authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.user.models.entity.User;

@Service
public interface UserService extends UserDetailsService {
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException;
    public void saveUser(User user);
}
