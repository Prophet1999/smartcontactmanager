package com.smart.Controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.Entities.Contact;
import com.smart.Entities.User;
import com.smart.Helper.Message;
import com.smart.Service.ContactService;
import com.smart.Service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class ContactController {
	
	@Autowired
	private UserService userSer;
	
	@Autowired
	private ContactService conSer;
	
	@ModelAttribute
	public void addCommonData(Model m,Principal p)
	{
		m.addAttribute("user",userSer.fetchUser(p.getName()));
	}
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable int cid,Model m,Principal p,HttpSession session) throws IOException
	{
		Contact contact=conSer.fetchById(cid);
		User user=userSer.fetchUser(p.getName());
		try {
		if(contact==null)
		session.setAttribute("message5",new Message("No such contact found!","alert-warning"));
		else if(user.getUid()!=contact.getUser().getUid())
		{
			session.setAttribute("message5",new Message("Insufficient rights to delete this contact","alert-danger"));
		}
		else {
			if(!contact.getImage().equals("default.webp")) {
			File file=new ClassPathResource("static/images").getFile();
			Path path=Paths.get(file.getAbsolutePath()+File.separator+contact.getImage());
			Files.delete(path);
			}
			contact.setUser(null);
			conSer.deleteContact(contact);
		session.setAttribute("message5",new Message("Contact successfully deleted!","alert-success"));

		}
		}
		catch(Exception e)
		{
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace();
			session.setAttribute("message5",new Message("Something went wrong, please try again!","alert-danger"));
		}
		return "redirect:/user/view-contacts/0";
	}
	
	@PostMapping("/update/{cid}")
	public String updateContact(@PathVariable int cid,Model m)
	{
		m.addAttribute("title","Update Contact - Smart Contacts Manager");
		m.addAttribute("contact",conSer.fetchById(cid));
		return "Normal/update_contact";
	}
	
	@RequestMapping(value="/updateContact",method=RequestMethod.POST)
	public String processUpdate(@Valid @ModelAttribute Contact contact,
			BindingResult result,@RequestParam("profileImage") MultipartFile file,
			Principal p,HttpSession session,Model m)
	{
		Contact pastContact=conSer.fetchById(contact.getCid());
		String pastEmail=pastContact.getEmail();
		String Email=contact.getEmail();
		String pastImage=pastContact.getImage();
		
		try {
			
			
			if(result.hasErrors())
			{
				System.out.println("ERROR "+result.toString());
				contact.setImage(pastImage);
				return "Normal/update_contact";
			}
			
			if(!file.isEmpty())
			{
				File FileLoc=new ClassPathResource("static/images").getFile();
				Path deletePath=Paths.get(FileLoc.getAbsolutePath()+
						File.separator+pastImage);
				Path savePath=Paths.get(FileLoc.getAbsolutePath()+
						File.separator+Email+file.getOriginalFilename());
				if(pastContact.getImage()!=null)
				Files.delete(deletePath);
				Files.copy(file.getInputStream(),savePath,StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(Email+file.getOriginalFilename());
			}
			else if(!Email.equals(pastEmail)) {
				String imgName=pastImage.substring(pastEmail.length(),pastImage.length());
				ClassPathResource customFile=new ClassPathResource("static/images/"+pastImage);
				File FileLoc=new ClassPathResource("static/images").getFile();
				Path deletePath=Paths.get(FileLoc.getAbsolutePath()+
						File.separator+pastImage);
				Path savePath=Paths.get(FileLoc.getAbsolutePath()+
						File.separator+Email+imgName);
				 try (InputStream inputStream = customFile.getInputStream()) {
		                Files.copy(inputStream, savePath, StandardCopyOption.REPLACE_EXISTING);
		            }
				if(!pastImage.equals("default.webp"))
					Files.delete(deletePath);
				
				contact.setImage(Email+imgName);
			}
			else contact.setImage(pastImage);
			
			contact.setUser(userSer.fetchUser(p.getName()));
			conSer.insertContact(contact);
			session.setAttribute("message6",new Message("Contact successfully updated!","alert-success"));
		}
		catch(Exception e)
		{
			System.out.println("Exception Message: "+e.getMessage());
			e.printStackTrace();
			session.setAttribute("message6",new Message("Unknown error occurred, Please try again!","alert-danger"));
		}
		
		return "redirect:/user/contact/"+contact.getCid();
	}
	
	@GetMapping("/search/{query}")
	@ResponseBody
	public List<Contact> doSearch(@PathVariable String query,Principal p)
	{
		User user=userSer.fetchUser(p.getName());
		List<Contact> contacts = conSer.fetchSearchResult(query,user);
		return contacts;
	}
}
