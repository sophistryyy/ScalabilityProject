package seng468.scalability.authentication;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRespositry;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRespositry = userRepository;
    }

    // Used by UserDetailsService to compare JWT user and a possible DB Repository user
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRespositry.findByUsername(username);

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
    public void saveUser(User user) {
        if (user != null) {
            userRespositry.save(user);
        }
    }
}
