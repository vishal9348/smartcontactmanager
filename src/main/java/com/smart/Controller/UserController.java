package com.smart.Controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	
	
	
	
	
	
	
	
	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {

		String userName = principal.getName();

		// get username from database

		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}

	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
//		String userName = principal.getName();
//		
//		//get username from database
//		
//		User user=userRepository.getUserByUserName(userName);
		model.addAttribute("title", "SCM - user-dashboard");

		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	
	// processing Add contact form-----------------------------
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			//@RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// uploading image file---------------------

//			if (file.isEmpty()) {
//
//			} else {
//				contact.setImage(file.getOriginalFilename());
//
//				File saveFile = new ClassPathResource("static/image").getFile();
//
//				java.nio.file.Path path = Paths
//						.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
//				
//				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			
			//success message------------------
			session.setAttribute("message", new message("your contact is added!!", "success"));
			
		} catch (Exception e) {
			System.out.println("Error" + e.getMessage());
			e.printStackTrace();
			
			//error message--------------
			session.setAttribute("message", new message("Something went wrong try again!!", "danger"));
		}
		return "normal/add_contact_form";
	}
	
	// show contacts handler ----------------------
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable Integer page, Model m, Principal principal)
	{
		m.addAttribute("title", "SCM - Show Contact Details");
		
		//get contact list from database
		
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);

		Pageable pageable=PageRequest.of(page, 10);
		Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	//showing details for Each contact
	
	@GetMapping("/{cId}/contact/")
	public String showContactDetail(@PathVariable Integer cId, Model model, Principal principal) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact=contactOptional.get();
		
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		
		
		
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
		}
		
		
		return "normal/contact_detail";
	}
	
	//DELETE contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session, Principal principal)
	{
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		
		Contact contact=contactOptional.get();
		// contact.setUser(null);
		
//		this.contactRepository.delete(contact);
		
		User user=this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		session.setAttribute("message", new message("Contact Deleted Successfully..","success"));
		
		return "redirect:/user/show-contacts/0";
	}
	
	//open update contact handler
	@GetMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cId, Model m)
	{
		m.addAttribute("title","update contacts");
		
		Contact contact=this.contactRepository.findById(cId).get();
		m.addAttribute("contact", contact);
		
		return "normal/update_form";
	}
	
	//update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, Model m, Principal principal) {
		
		Contact oldContact=this.contactRepository.findById(contact.getcId()).get();
		
		try {
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/user/"+contact.getcId()+"/contact/";
	}

	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "profile");
		return "normal/profile";
	}
}
