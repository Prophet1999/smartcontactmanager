package com.smart.Service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Entities.Contact;
import com.smart.Entities.User;
import com.smart.Helper.FileUploadHelper;
import com.smart.Repository.ContactRepository;

@Service
public class FileUploadService {
	
	@Autowired
	private ContactRepository conRepo;
	
	public void insertFileToDB(MultipartFile file, User user) {
		try {
			conRepo.saveAll(FileUploadHelper.extractContactListFromFile(file.getInputStream(), user));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Contact> fetchAllFileStoredContacts(){
		return conRepo.findAll();
	}
}
