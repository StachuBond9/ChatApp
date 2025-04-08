package com.staislawwojcik.forum.infrastructure.security;

import com.staislawwojcik.forum.infrastructure.database.user.User;
import com.staislawwojcik.forum.infrastructure.database.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceJPA implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceJPA(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).orElseThrow(() ->  new UsernameNotFoundException("Not found : " +username));
        return new org.springframework.security.core.userdetails.User(user.getLogin(),user.getPassword(), new ArrayList<>());
    }
}
