package com.tsv.implementation.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.twilio.Twilio;
import com.twilio.rest.chat.v1.service.channel.Message;
import com.twilio.rest.messaging.v1.service.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tsv.implementation.dao.RoleRepository;
import com.tsv.implementation.dao.UserRepository;
import com.tsv.implementation.dto.UserRegisteredDTO;
import com.tsv.implementation.model.Role;
import com.tsv.implementation.model.User;


@Service
public  class DefaultUserServiceImpl implements DefaultUserService{
	
	static final String Account_Sid = "AC442ef409280482bdc91f984fbb1a6dad";
	static final String Auth_Token = "060b504c5d8dc9741c2a2013a085adef";
	
   @Autowired
	private UserRepository userRepo;
	
   @Autowired
  	private RoleRepository roleRepo;
  	
   @Autowired
	 JavaMailSender javaMailSender;
   
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	
		User user = userRepo.findByEmail(email);
		if(user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRole()));		
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
	}


	 @Override public User save(UserRegisteredDTO userRegisteredDTO) { Role role =
	 roleRepo.findByRole("USER");

	  User user = new User(); user.setEmail(userRegisteredDTO.getEmail_id());
	  user.setName(userRegisteredDTO.getName());
	  user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
	  user.setRole(role);

	  return userRepo.save(user); }

	@Override
	public String genrateOTPAndSendOnMobile(User user) {
		return null;
	}

	@Override public String generateOtp(User user) { try { int randomPIN = (int)
	  (Math.random() * 9000) + 1000; user.setOtp(randomPIN);
	  userRepo.save(user);
	  SimpleMailMessage msg = new SimpleMailMessage();
	  msg.setFrom("agarwalvikash768@gmail.com");
	  msg.setTo(user.getEmail());

	  msg.setSubject("Welcome To My Channel");
	  msg.setText("Hello  \n\n"
	  +"Your Login OTP :" + randomPIN + ".Please Verify. \n\n"+"Regards \n"+"Vikash");

	  javaMailSender.send(msg);

	 return "success";
	}catch (Exception e) { e.printStackTrace();
		return "error";
	  } }

	
	/*@Override
	public User save(UserRegisteredDTO userRegisteredDTO) {
		Role role = roleRepo.findByRole("USER");
		
		User user = new User();
		user.setEmail(userRegisteredDTO.getEmail_id());
		user.setName(userRegisteredDTO.getName());
		user.setRole(role);
		user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
		user.setMobileNo(userRegisteredDTO.getMobileNo());
		userRepo.save(user);
		return user;
	}
	@Override
	public String genrateOTPAndSendOnMobile(User userdata) {
		User user = userRepo.findByEmail(userdata.getEmail());
		int otp =  (int) (Math.random() * 9000) + 1000;
		user.setOtp(otp);
		userRepo.save(user);
		Twilio.init(Account_Sid, Auth_Token);
		Message message = Message.creator(new PhoneNumber("+91"+userdata.getMobileNo()), new PhoneNumber("+14849788085"), "This is the OTP sent for verification"+" "+ otp+ ".Please verify!")
                .create();
		if(message.getErrorCode() == null)
		return "success";
		else
		return "error";
		
	}*/

}
