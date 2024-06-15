package com.smart.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.Entities.Contact;
import com.smart.Entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer>{

	@Query("from Contact c where c.user.id=:uid")
	public List<Contact> getContactsByUid(@Param("uid") int uid);

	@Query("from Contact c where c.user.email=:username")
	public Page<Contact> getContactsByUsername(@Param("username") String username,Pageable pageable);
	
	@Query("from Contact c where c.user.uid=:uid")
	public void deleteAllById(@Param("uid") int id);
	
	public List<Contact>findByNameContainingAndUser(String name,User user);
}
