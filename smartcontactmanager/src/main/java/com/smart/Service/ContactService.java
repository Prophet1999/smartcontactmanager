package com.smart.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.smart.Entities.Contact;
import com.smart.Entities.User;
import com.smart.Repository.ContactRepository;

@Service
public class ContactService {
	
	@Autowired
	private ContactRepository repo;
	
//	public List<Contact>fetchContacts(int uid)
//	{
//		return repo.getContactsByUid(uid);
//	}
	
	public Page<Contact>fetchContacts(String username,Pageable pageable)
	{
		return repo.getContactsByUsername(username,pageable);
	}

	public Contact fetchById(int cid) {
		Optional<Contact>contact=repo.findById(cid);
		if(contact.isPresent())
		return contact.get();
		return null;
	}

	public void deleteContact(Contact contact) {
		repo.delete(contact);
	}

	public void insertContact(Contact contact) {
		repo.save(contact);
	}

	public void eraseUserContacts(int uid) {
		repo.deleteAllById(uid);
	}
	
	public List<Contact> fetchSearchResult(String name,User user) {
		return repo.findByNameContainingAndUser(name,user);
	}

}
