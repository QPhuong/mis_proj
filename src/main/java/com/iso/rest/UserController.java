package com.iso.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.iso.domain.User;
import com.iso.exception.BusinessException;
import com.iso.service.UserService;

/*@RestController
@RequestMapping("api/user")*/
final public class UserController {

	/*private final IUserService userService;
	 
    @Autowired
    UserController(IUserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User save(@RequestBody @Validated User userData) throws BusinessException {
        return userService.saveUserProfile(userData);
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") String id) {
        userService.delete(id);
    }
 
    @RequestMapping(value = "{username}", method = RequestMethod.GET)
    public User getUserByUserName(@PathVariable("username") String username) {
        return userService.getUserByUserName(username);
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable("id") String id) {
        return userService.getUserById(id);
    }*/
    
    
}
