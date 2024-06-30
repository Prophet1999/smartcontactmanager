package com.smart.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.smart.Entities.LocalOrder;
import com.smart.Entities.User;
import com.smart.Repository.OrderRepository;
import com.smart.Repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired 
	private OrderRepository orderRepo;
	
	public User insertUser(User user)
	{
		return userRepo.save(user);
	}
	
	public User fetchUser(String email)
	{
		return userRepo.getUserByUserName(email);
	}

	public User fetchById(int cid) {
		User user=userRepo.getById(cid).get();
		if(user==null)
			return null;
		return user;
	}

	public void deleteUser(User user) {
		userRepo.delete(user);
	}
	
	public LocalOrder insertOrder(LocalOrder order)
	{
		return orderRepo.save(order);
	}
	
	public LocalOrder fetchOrder(String orderId)
	{
		return orderRepo.findByOrderId(orderId);
	}
}
