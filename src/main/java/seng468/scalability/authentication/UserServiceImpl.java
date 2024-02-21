package seng468.scalability.authentication;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import seng468.scalability.models.entity.User;
import seng468.scalability.models.exceptions.UsernameExistsException;
import seng468.scalability.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRespository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRespository = userRepository;
    }

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
