package com.smart.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.smart.Entities.User;
import com.smart.Repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repo;
	
	public User insertUser(User user)
	{
		return repo.save(user);
	}
	
	public User fetchUser(String email)
	{
		return repo.getUserByUserName(email);
	}

	public User fetchById(int cid) {
		User user=repo.getById(cid).get();
		if(user==null)
			return null;
		return user;
	}

	public void deleteUser(User user) {
		repo.delete(user);
	}
	
}
