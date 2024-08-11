package com.smart.Controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.Entities.User;
import com.smart.Helper.Message;
import com.smart.Service.EmailService;
import com.smart.Service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserService ser;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	Random randomOTP = new Random(1000);
	
	@GetMapping("/")
	public String defaulter()
	{
		return "redirect:/home";
	}
	
	@GetMapping("/login-fail")
	public String loginError(Model m)
	{
		m.addAttribute("title","Unknown error occurred - Smart Contacts Manager");
		return "loginerror";
	}
	
	@GetMapping("/home")
	public String home(Model m)
	{
		m.addAttribute("title","Home - Smart Contacts Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("title","About - Smart Contacts Manager");
		return "about";
	}
	
	@GetMapping("/signin")
	public String login(Model m)
	{
		m.addAttribute("title","SignIn - Smart Contacts Manager");
		return "login";
	}
	
	@GetMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("title","Register - Smart Contacts Manager");
		m.addAttribute("user",new User());
		return "signup";
	}
	
	// code for registering user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute User user,BindingResult result,
			@RequestParam(defaultValue="false") boolean agreement,
			Model m,HttpSession session)
	{
		m.addAttribute("title","Register - Smart Contacts Manager");
		user.setEnabled(true);
		user.setRole("ROLE_USER");
		user.setImageUrl("default.png");
		user.setPassword(encoder.encode(user.getPassword()));
		//m.addAttribute("session",session);
		try {
			if(result.hasErrors())
			{
				System.out.println("ERROR "+result.toString());
				m.addAttribute("user",user);
				return "signup";
			}
			else if(!agreement)
			throw new Exception("You must accept all terms and conditions");
			
			ser.insertUser(user);
			session.setAttribute("message",
					new Message("Successfully registered!","alert-success"));
			m.addAttribute("user",new User());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			m.addAttribute("user",user);
			session.setAttribute("message",
					new Message("Something went wrong! "+e.getMessage(),
							"alert-danger"));
		}
		//session.removeAttribute("message");
		return "signup";
	}
	
	@GetMapping("/forgot-password")
	public String forgotPassword(Model m)
	{
		m.addAttribute("title","Forgot Password - Smart Contacts Manager");
		return "forgot_password";
	}
	
	@PostMapping("/send-OTP")
	public String sendOTP(@RequestParam String email, HttpSession session)
	{
		try {
		int OTP = randomOTP.nextInt(999999);
		System.out.println(email.trim());
		System.out.println(OTP);
		String message = "<div style='border:1px solid #e2e2e2; padding:20px;'>"
				+ "<h2>OTP is <b>"+OTP+"</b></h2></div>";
		boolean emailFlag = emailService.sendEmail(message, email.trim());
		
		if(emailFlag) {
			session.setAttribute("sessionOTP",OTP);
			session.setAttribute("sessionEmail",email.trim());
			if(email.contains(" "))
				session.setAttribute("message11",new Message("OTP was resent!","alert-success"));
			else session.setAttribute("message11",new Message("OTP sent successfully!","alert-success"));
			return "redirect:/verify-OTP";
		}
		
		session.setAttribute("message10","Invalid Email ID, Please retry again!");
		}catch(Exception e)
		{
			e.printStackTrace();
			session.setAttribute("message10","OOPS! Something went wrong, please try again later");
		}
		return "redirect:/forgot-password";
	}
	
	@GetMapping("/verify-OTP")
	public String verifyOTP(HttpSession session, Model m)
	{
		m.addAttribute("title","Verify OTP - Smart Contacts Manager");
		return "verify_OTP";
	}
	
	@PostMapping("/verify-OTP")
	public String verifyOTP(@RequestParam int OTP, HttpSession session)
	{
		try {
		int sessionOTP = (int)session.getAttribute("sessionOTP");
		String sessionEmail = (String)session.getAttribute("sessionEmail");
		
		if(sessionOTP==OTP) {
			if(ser.fetchUser(sessionEmail)==null)
				return "redirect:/signin?message2=No user found, please try with different email!";
			else return "redirect:/password-change";
		}
		else session.setAttribute("message11",new Message("Invalid OTP entered, Authentication failed!","alert-danger"));	
		return "redirect:/verify-OTP";  
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			session.setAttribute("message11",new Message("Something went wrong!","alert-danger"));				
		}
		return "verify_OTP";
	}
	
	@GetMapping("/password-change")
	public String changePassword(Model m)
	{
		m.addAttribute("title","Password Change - Smart Contacts Manager");
		return "password_change_form";
	}
	
	@PostMapping("/password-change")
	public String changePassword(@RequestParam String newPassword, 
			HttpSession session)
	{
		String sessionEmail=(String)session.getAttribute("sessionEmail");
		User user = ser.fetchUser(sessionEmail);
		user.setPassword(bCryptPasswordEncoder.encode(newPassword));
		ser.insertUser(user);
		return "redirect:/signin?message=Password changed successfully!";
	}
}