package com.smart.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Entities.Contact;
import com.smart.Helper.FileUploadHelper;
import com.smart.Service.ContactService;
import com.smart.Service.FileUploadService;
import com.smart.Service.UserService;

@RestController
@RequestMapping("/user")
public class FileUploadController {
	
	@Autowired
	private FileUploadService uploadService;
	
	@Autowired
	private UserService userSer;
	
	@Autowired
	private ContactService conSer;
	
	@PostMapping("upload-file")
	public ResponseEntity<?> uploadFileForContacts(@RequestParam("contactsFile") MultipartFile file, Principal p, Model m){
		if (file.isEmpty())
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No file selected...");
	    		
		try {
		if(FileUploadHelper.isFileValid(file)) {
			if(uploadService.insertFileToDB(file, userSer.fetchUser(p.getName())).size()==0)
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No records found in the input file...");			
		}
		else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only excel files are allowed...");
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("OOPS! Something went wrong...");
		}
		
		return ResponseEntity.ok("Bulk Contacts Added Successfully...");
	}
	
	@GetMapping("/all-contacts")
	public List<Contact> getAllFileContacts(Principal p){
		return conSer.fetchAllContacts(p.getName());
	}
}
