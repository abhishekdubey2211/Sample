
package com.springbootjwt.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springbootjwt.exception.*;
import com.springbootjwt.dto.LoginDTO;
import com.springbootjwt.dto.UserDTO;
import com.springbootjwt.model.Role;
import com.springbootjwt.model.User;
import com.springbootjwt.repository.RoleRepository;
import com.springbootjwt.repository.UserRepository;
import com.springbootjwt.service.UserService;
import com.springbootjwt.util.JwtUtil;

import java.util.List;
import java.util.Optional;

//allows to add business functionalities
@Service

//specifies the semantics of the transactions
@Transactional
public class UserServiceImpl implements UserService
{
	//@Autowired used for automatic dependency injection
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; 

    
    @Override
    //creating method for user register
    public void registerUser(UserDTO userDTO) 
    {
    	//For retrieving the role of an login client into the system
    	Role role=roleRepository.findById(2l).get();	
    	User user=User.builder()
      				.name(userDTO.getName())
        			.address(userDTO.getAddress())
                	.emailAddress(userDTO.getEmailAddress())
                    .role(role)
                    .password(passwordEncoder.encode
                    		(userDTO.getPassword()))
                	.build();
        userRepository.save(user);
    }

    
    @Override
    //method for login
    public String login(LoginDTO loginDTO) 
    {
    	//provide method used to check the presence of value for particular variable
        Optional<User> userOptional=
        	userRepository.findByEmailAddress(loginDTO.getEmailAddress());
         
        if(userOptional.isEmpty())
        {
        	//if login credentials not found
            throw new BadRequestException("User Not Found.");
        }
        if(passwordEncoder.matches
        	(loginDTO.getPassword(),
            userOptional.get().getPassword())
          )
        {
            return jwtUtil.generateAccessToken
            		(userOptional.get());
        }
        else
        {
        	//if any one the the required credential matches and another credential detail dosn't matches
           throw new BadRequestException("Invalid UserName Or Password");
        }
    }
}
