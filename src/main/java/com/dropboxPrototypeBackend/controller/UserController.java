package com.dropboxPrototypeBackend.controller;

import com.dropboxPrototypeBackend.DropboxPrototypeBackEndApplication;
import com.dropboxPrototypeBackend.GlobalConstants;
import com.dropboxPrototypeBackend.entity.ErrorResponse;
import com.dropboxPrototypeBackend.entity.SuccessResponse;
import com.dropboxPrototypeBackend.entity.User;
import com.dropboxPrototypeBackend.entity.UserAccount;
import com.dropboxPrototypeBackend.repository.UserAccountRepository;
import com.dropboxPrototypeBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    private User user;
    private List<User> users;

    @Autowired
    private UserAccountRepository userAccountRepository;
    private UserAccount userAccount;

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public Object signin(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (userRepository.findByEmail(email) != null) {
            user = userRepository.findByEmail(email);
            if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                String token = "";
                return new SuccessResponse(200, "Successfully signed in.", token, email);
            } else {
                return new ErrorResponse(401, "Signing in failed.", "Invalid credentials.");
            }
        } else {
            return new ErrorResponse(401, "Signing in failed.", "Invalid credentials.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public Object signup(@RequestParam(value = "email") String email, @RequestParam(value = "password") String password, @RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName) {
        if (userRepository.findByEmail(email) != null) {
            return new ErrorResponse(400, "Signing up failed.", "Invalid Data.");
        } else {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            try {
                userRepository.save(new User(email, firstName, lastName, bCryptPasswordEncoder.encode(password)));
                userAccountRepository.save(new UserAccount(email, firstName, lastName));
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorResponse(401, "Signing up failed.", "Invalid Data.");
            }
            return new SuccessResponse(201, "Successfully signed up.", email);
        }
    }


    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Object getUser(@RequestParam(value = "userId") String email) {
        user = userRepository.findByEmail(email);
        if (user != null) {
            return new SuccessResponse(200, "Successfully retrieved user information.", user);
        } else {
            return new ErrorResponse(404, "Cannot retrieve user information.", "User not found.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Object getUsers(@RequestParam(value = "searchString") String searchString) {
        if (searchString.trim().isEmpty()) {
            return new SuccessResponse(200, "No search string.", new ArrayList<User>());
        }
        users = userRepository.findAllByEmailLikeOrFirstNameLikeOrLastNameLike(searchString, searchString, searchString);
        if (users != null) {
            return new SuccessResponse(200, "Successfully retrieved user information.", users);
        } else {
            return new ErrorResponse(500, "Cannot retrieve users.", "Internal Server Error.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public Object getUserAccount(@RequestParam(value = "userId") String email) {
        userAccount = userAccountRepository.findByEmail(email);
        if (userAccount != null) {
            return new SuccessResponse(200, "User account successfully retrieved.", userAccount);
        } else {
            return new ErrorResponse(404, "Cannot retrieve user information.", "User account not found.");
        }
    }

    @CrossOrigin(origins = GlobalConstants.origin)
    @RequestMapping(value = "/account", method = RequestMethod.PATCH)
    public Object updateUserAccount(@RequestParam(value = "email") String email, @RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName, @RequestParam(value = "work") String work, @RequestParam(value = "education") String education, @RequestParam(value = "address") String address, @RequestParam(value = "country") String country, @RequestParam(value = "city") String city, @RequestParam(value = "zipcode") String zipcode, @RequestParam(value = "interests") String interests) {
        userAccount = userAccountRepository.findByEmail(email);
        userAccount.setFirstName(firstName);
        userAccount.setLastName(lastName);
        userAccount.setWork(work);
        userAccount.setEducation(education);
        userAccount.setAddress(address);
        userAccount.setCountry(country);
        userAccount.setCity(city);
        userAccount.setZipcode(zipcode);
        userAccount.setInterests(interests);
        userAccountRepository.save(userAccount);
        if (userAccount != null) {
            return new SuccessResponse(200, "User account successfully updated.", userAccount);
        } else {
            return new ErrorResponse(404, "Cannot update user information.", "User account not found.");
        }
    }
}
