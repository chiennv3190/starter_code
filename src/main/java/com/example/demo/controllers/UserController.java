package com.example.demo.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.BusinessLogicException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.JwtRequest;
import com.example.demo.model.response.JwtResponse;
import com.example.demo.security.JwtTokenUtil;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private static final Logger logger = LogManager.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(token));
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
	
	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		
		logger.info("Start create user");
		if (!StringUtils.hasText(createUserRequest.getUsername())) {
			throw new BusinessLogicException("Error create user: Username is mandatory");
		} else if (!StringUtils.hasText(createUserRequest.getPassword())) {
			throw new BusinessLogicException("Error create user: Password is mandatory");
		} else if (createUserRequest.getPassword().getBytes().length < 8 || createUserRequest.getPassword().getBytes().length > 16) {
			throw new BusinessLogicException("Error create user: Password has a length of 8 to 16 characters");
		} else if (!StringUtils.hasText(createUserRequest.getConfirmPassword()) 
				|| !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			throw new BusinessLogicException("Error create user: Confirm Password doesn't match Password");
		}
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		userRepository.save(user);
		logger.info("Create user successfully!");
		
		return ResponseEntity.ok(user);
	}
	
}
