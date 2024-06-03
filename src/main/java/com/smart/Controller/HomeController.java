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
	public String forgotPassword()
	{
		return "forgot_password";
	}
	
	@PostMapping("/send-OTP")
	public String sendOTP(@RequestParam String email, HttpSession session)
	{
		int OTP = randomOTP.nextInt(999999);
		System.out.println(email);
		System.out.println(OTP);
		String subject = "OTP from Smart Contacts Manager";
		String message = "<h2>"+OTP+"</h2>";
		boolean emailFlag = emailService.sendEmail(subject, message, email);
		
		if(emailFlag) {
			session.setAttribute("OTP",OTP);
			session.setAttribute("message10","OTP sent successfully!");
			return "verify_OTP";
		}
		
		session.setAttribute("message10","Invalid Email ID, Please retry again!");
		return "redirect:/forgot-password";
	}
	
	@PostMapping("/verify-OTP")
	public String verifyOTP()
	{
		return "";
	}
}