package com.smart.Controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Value("${API_KEY_ID:#{null}}")
	private String apiKeyId;
	
	@Value("${API_SECRET_KEY:#{null}}")
	private String apiSecretKey;
	
	@Autowired
	private UserService userSer;
	
	@Autowired
	private ContactService conSer;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@ModelAttribute
	public void addCommonData(Model m, Principal p)
	{
		m.addAttribute("user",userSer.fetchUser(p.getName()));
	}
	
	@GetMapping("/index")
	public String dashboard(Model m,Principal p)
	{
		m.addAttribute("title","UserHome - Smart Contacts Manager");
		return "Normal/userHome";
	}
	
	@GetMapping("/add-contact")
	public String addContactForm(Model m)
	{
		m.addAttribute("title","Add Contact - Smart Contacts Manager");
		m.addAttribute("contact",new Contact());
		return "Normal/add_contact_form";
	}
	
	@GetMapping("/about")
	public String userAbout(Model m, Principal p)
	{
		m.addAttribute("title","About User - Smart Contacts Manager");
		return "Normal/userAbout";
	}
	
	@PostMapping("/processContact")
	public String processContact(@Valid @ModelAttribute Contact contact,
			BindingResult result,@RequestParam("profileImage") MultipartFile file,
			Model m, Principal p,HttpSession session)
	{
		m.addAttribute("title","Add Contact - Smart Contacts Manager");
		
		try {
			if(result.hasErrors())
			{
				m.addAttribute("contact",contact);
				m.addAttribute("file",file);
				System.out.println("RESULT "+result.toString());
				return "Normal/add_contact_form";
			}
			
			try {
			if(file.isEmpty())
					contact.setImage("default.webp");
			else{
				contact.setImage(contact.getEmail()+file.getOriginalFilename());
				File saveFile=new ClassPathResource("static/images").getFile();
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+contact.getEmail()+file.getOriginalFilename());
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				}
			}
			catch(NoSuchFileException e)
			{
			   e.printStackTrace();
			   System.out.println("ERROR: Cannot process contact profile image...");
			}
				
		User user=userSer.fetchUser(p.getName());
		contact.setUser(user);
		user.getContacts().add(contact);
		userSer.insertUser(user);
		m.addAttribute("contact",new Contact());
		
		if(file.isEmpty()) 
		session.setAttribute("message2",new Message
					("Contact successfully saved without profile image!","alert-warning"));
		else
		session.setAttribute("message2",new Message
				("Contact successfully saved! ","alert-success"));
		}
		catch(Exception e)
		{
			m.addAttribute("contact",contact);
			m.addAttribute("file",file);
			e.printStackTrace();
			session.setAttribute("message2",new Message
					("Something went wrong! "+e.getMessage(),"alert-danger"));
		}
		return "Normal/add_contact_form";
	}
	
	@GetMapping("/view-contacts/{page}")
	public String showContacts(@PathVariable int page, HttpSession session,Model m,Principal p)
	{
		m.addAttribute("title","View Contacts -Smart Contacts Manager");
		
		// fetching by UserRepository
		User user = userSer.fetchUser(p.getName());
//		m.addAttribute("contacts",user.getContacts());
		
		// fetching by ContactRepository
		int recordsPerPage=3;
		Pageable pageable = PageRequest.of(page,recordsPerPage);
		Page<Contact>contacts=conSer.fetchContacts(p.getName(),pageable);
		
		m.addAttribute("totalRecords",user.getContacts().size());
		session.setAttribute("message3",
					new Message("No Records Found!","alert-danger"));
		
		m.addAttribute("currentPage",page);
		m.addAttribute("recordsPerPage",recordsPerPage);
		m.addAttribute("totalPages",contacts.getTotalPages());
		m.addAttribute("contacts",contacts);
		
		return "Normal/view_contacts";
	}
	
	@GetMapping("/contact/{cid}")
	public String showContactDetails(@PathVariable int cid,Model m,
			Principal p,HttpSession session)
	{
		Contact contact = conSer.fetchById(cid);
		User user=userSer.fetchUser(p.getName());
		m.addAttribute("title",contact.getName()+"("+contact.getCid()+")"+" - Smart Contact Manager");

		if(user.getUid()==contact.getUser().getUid())
			m.addAttribute("contact",contact);
		else
			m.addAttribute("title","Permission Denied - Smart Contact Manager");

		session.setAttribute("message4",new Message
					("You do not have permission to view this contact!","alert-danger"));
		
		return "Normal/contact_details";
	}
	
	@GetMapping("/{uid}/profile")
	public String showProfile(@PathVariable int uid,Model m,Principal p)
	{
		m.addAttribute("title","User Profile - Smart Contacts Manager");
		User user = userSer.fetchById(uid);
		m.addAttribute("user",user);
		return "Normal/user_profile";
	}
	
	@PostMapping("/{uid}/update")
	public String updateUser(@PathVariable int uid,Model m)
	{
		m.addAttribute("title","Update User - Smart Contacts Manager");
		User user = userSer.fetchById(uid);
		m.addAttribute("user",user);
		return "Normal/update_user";
	}
	
	@PostMapping("/updateUser")
	public String processUpdate(@Valid @ModelAttribute User user,
			BindingResult result,@RequestParam("profileImage") MultipartFile file,
			HttpSession session,Principal p)
	{
		User pastUser=userSer.fetchUser(p.getName());
		System.out.println(p.getName());
		String pastEmail=pastUser.getEmail();
		String Email=user.getEmail();
		String pastImage=pastUser.getImageUrl();
		
		try {
			
			
			if(result.hasErrors())
			{
				System.out.println("ERROR "+result.toString());
				user.setImageUrl(pastImage);
				return "Normal/update_user";
			}
			
			try {
			if(!file.isEmpty())
			{
				File FileLoc=new ClassPathResource("static/images").getFile();
				Path deletePath=Paths.get(FileLoc.getAbsolutePath()+
						File.separator+pastImage);
				Path savePath=Paths.get(FileLoc.getAbsolutePath()+
						File.separator+Email+file.getOriginalFilename());
				if(!pastImage.equals("default.png") )
				Files.delete(deletePath);
				Files.copy(file.getInputStream(),savePath,StandardCopyOption.REPLACE_EXISTING);
				user.setImageUrl(Email+file.getOriginalFilename());
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
				if(!pastImage.equals("default.png"))
					Files.delete(deletePath);
				user.setImageUrl(Email+imgName);
			}
			else user.setImageUrl(pastImage);
			}
			catch(NoSuchFileException e)
			{
			   e.printStackTrace();
			   System.out.println("ERROR: Cannot update user profile image...");
			}
			
			userSer.insertUser(user);
			session.setAttribute("message7",new Message("User successfully updated!","alert-success"));
		}
		catch(Exception e)
		{
			System.out.println("Exception Message: "+e.getMessage());
			e.printStackTrace();
			session.setAttribute("message7",new Message("Unknown error occurred, Please try again!","alert-danger"));
		}
		
		if(!Email.equals(p.getName()))
			return "redirect:/logout";
			
		return "redirect:/user/"+user.getUid()+"/profile";
	}
	
	@GetMapping("/{uid}/delete")
	public String deleteUser(@PathVariable int uid,Model m,
			Principal p,HttpSession session) throws IOException
	{
		User user=userSer.fetchById(uid);
		//User user=userSer.fetchUser(p.getName());
		try {
		if(user==null)
		session.setAttribute("message8",new Message("No such User found!","alert-warning"));
		else {
			if(!user.getImageUrl().equals("default.png")) {
			try {
			File file=new ClassPathResource("static/images").getFile();
			Path path=Paths.get(file.getAbsolutePath()+File.separator+user.getImageUrl());
			Files.delete(path);
			}
			catch(NoSuchFileException e)
			{
				e.printStackTrace();
				System.out.println("Error: User profile image not found...");
			}
		}
		
		conSer.eraseUserContacts(uid);
		userSer.deleteUser(user);
		session.setAttribute("message8",new Message("Contact successfully deleted!","alert-success"));
		}
	}
		catch(Exception e)
		{
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace();
			session.setAttribute("message8",new Message("Something went wrong, please try again!","alert-danger"));
		}
		return "redirect:/logout";
	}
	
	@GetMapping("/{uid}/settings")
	public String settings(Model m)
	{
		m.addAttribute("title","Settings - Smart Contacts Manager");
		return "Normal/userSettings";
	}
	
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam String oldPassword, 
			@RequestParam String newPassword, Principal principal, HttpSession session)
	{
		System.out.println("old password:"+oldPassword);
		System.out.println("new password:"+newPassword);
		User user=userSer.fetchUser(principal.getName());
		System.out.println("old encrypted password:"+user.getPassword());
		
		if(oldPassword==null || oldPassword.isEmpty() ||
				newPassword==null || newPassword.isEmpty()) {
			session.setAttribute("message9",new Message("Old and new passwords can't be empty...","alert-warning"));
			System.out.println("Old and new passwords can't be empty...");
			return "redirect:/user/"+user.getUid()+"/settings";
		}

		else if(passwordEncoder.matches(oldPassword,user.getPassword()))
		{
			//change the password
			user.setPassword(passwordEncoder.encode(newPassword));
			userSer.insertUser(user);
			System.out.println("Password changed successfully...");
			session.setAttribute("message9",new Message("Password changed successfully...","alert-success"));
		}
		else {
			System.out.println("Wrong password entered...");
			session.setAttribute("message9",new Message("Wrong password entered...","alert-danger"));
			return "redirect:/user/"+user.getUid()+"/settings";
		}
		
		return "redirect:/user/index";
	}
	
	// create order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String,Object>data) throws RazorpayException
	{
		System.out.println("Order function executed");
		System.out.println(data);
		
		if(apiKeyId==null || apiKeyId.isEmpty() || 
				apiSecretKey==null || apiSecretKey.isEmpty())
			return "\"404_Not_Found\":\"Payment-API-key\"";
		
		int amount = Integer.parseInt(data.get("amount").toString().trim());
		var client = new RazorpayClient(apiKeyId,apiSecretKey);
		
		JSONObject options = new JSONObject();
		options.put("amount",amount*100);
		options.put("currency","INR");
		options.put("receipt","txn_235425");

		//creating new order
		Order order = client.Orders.create(options);
		System.out.println(order);
		return "{\"apiKeyId\":\""+apiKeyId+"\","+order.toString().substring(1);
	}
}
