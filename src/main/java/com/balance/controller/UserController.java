package com.balance.controller;

import com.balance.model.Band;
import com.balance.model.Terminal;
import com.balance.model.User;
import com.balance.repository.TerminalRepository;
import com.balance.service.BandService;
import com.balance.service.TerminalService;
import com.balance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Iterator;

@Controller
public class UserController {

    private UserService userService;
    private TerminalService terminalService;
    private BandService bandService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTerminalService(TerminalService terminalService){
        this.terminalService=terminalService;
    }

    @Autowired
    public void setBandService(BandService bandService){
        this.bandService=bandService;
    }




    @RequestMapping(value = "/admin/user/{id}", method = RequestMethod.GET)
    public String showUser(@PathVariable Integer id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "admin/user";
    }

    @RequestMapping(value = "/admin/user/delete/{id}", method = RequestMethod.GET)
    public String deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "redirect:/admin/home";
    }

    @RequestMapping(value = "/admin/user/edit/{id}",method = RequestMethod.GET)
    public String editUser(@PathVariable Integer id,Model model) {
        model.addAttribute("user",userService.getUserById(id));
        return "admin/userForm";
    }

    @RequestMapping(value = "/admin/user", method = RequestMethod.POST)
    public String saveUser(@Valid User user) {
        userService.saveUserEdited(user);
        return "redirect:/admin/home";
    }

    //LimitedUser Controller--------------------------------------------------------

    @RequestMapping(value = "/user/profile",method = RequestMethod.GET)
    public String viewProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addAttribute("user", user);
        return "limited/profile";
    }

    @RequestMapping(value = "/user/edit",method = RequestMethod.GET)
    public String editProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        model.addAttribute("user",userService.getUserById(user.getId()));


/*
        for (Band t: listbands){
            if(!t.isActive() || t==userService.getUserById((user.getId())).getTerminal()){
                resp.add(t);
            }
        }*/
        /*Terminal ant=terminalService.getTerminalById(userService.getUserById(user.getId()).getTerminal().getId());
        ant.setActive(false);
        terminalService.deleteTerminal(userService.getUserById(user.getId()).getTerminal().getId());
        terminalService.saveTerminal(ant);
        model.addAttribute("terminals",resp);
        */
        Terminal ant=terminalService.getTerminalById(userService.getUserById(user.getId()).getTerminal().getId());
        ant.setActive(false);
        terminalService.deleteTerminal(userService.getUserById(user.getId()).getTerminal().getId());
        terminalService.saveTerminal(ant);
        model.addAttribute("bands",bandService.listAllBands());




        return "limited/editProfile";




    }

    @RequestMapping(value = "/user",method = RequestMethod.POST)
    public String saveLimitedUser(@Valid User user, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            return "limited/editProfile";
        }

        userService.saveUserEdited(user);
        return "redirect:/user/profile/";
    }
}
