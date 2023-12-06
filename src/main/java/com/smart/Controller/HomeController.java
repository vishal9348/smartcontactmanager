package com.smart.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model)
	{
		model.addAttribute("title", "SignUp - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	//this handler for registration
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute User user,BindingResult result1 , @RequestParam(defaultValue = "false") boolean agreement, Model model, 
			 HttpSession session)
	{
		try {
			if(!agreement) 
			{
				throw new Exception("you have not agrred the term and condition");
			}
//			if(result1.hasErrors()) 
//			{
//				model.addAttribute("user",user);
//				return "signup";
//			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			User result=this.userRepository.save(user);

			model.addAttribute("user",new User());
			session.setAttribute("message", new message("Succssefully Register!!", "alert-success"));
			return "signup";
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new message("Something went wrong!!", "alert-danger"));
			return "signup";
		}

	}
	
	//handler for custom login
	
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title", "Smart Contact Manager - Login");
		return "login";
	}

}
