package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.Entities.User;
import com.smart.Service.UserService;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserService service;
    
//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		
//		User user=service.fetchUser(username);
//		
//		if(user==null)
//			throw new UsernameNotFoundException("Could not locate user");
//		
//		return new CustomUserDetails(user);
//	}
//	@Override
    public UserDetails loadUserByUsername(String usernameOrUserId) throws UsernameNotFoundException {

        User user = null;
        try {
            // Try to parse the input as an integer, assuming it is a user ID
            int userId = Integer.parseInt(usernameOrUserId);
            user = service.fetchById(userId);
        } catch (NumberFormatException e) {
            // The input is not a valid integer, assume it is a username
            user = service.fetchUser(usernameOrUserId);
        }

        if (user == null)
            throw new UsernameNotFoundException("Could not locate user");

        // Create a new CustomUserDetails object with the fetched User object
        CustomUserDetails userDetails = new CustomUserDetails(user);

        return userDetails;
    }
}
