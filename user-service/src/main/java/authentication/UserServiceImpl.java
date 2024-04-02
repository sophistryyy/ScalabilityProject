package authentication;

import com.user.entity.User;
import com.user.exceptions.UsernameExistsException;
import com.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRespository;

    // Used by UserDetailsService to compare JWT user and a possible DB Repository user
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRespository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid Username");
        }

         return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            new ArrayList<>()
        );
    }

    @Override
    public void saveUser(User user) throws UsernameExistsException {
        if (user == null) {
            return;
        }

        if (userRespository.existsById(user.getUsername())) {
            throw new UsernameExistsException("Username Already Exists");
        }

        userRespository.save(user);
    }
}
