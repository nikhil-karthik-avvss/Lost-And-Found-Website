package com.ssn.backend.Controller;

import com.ssn.backend.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private MailService mailService;
    
    @GetMapping("/otp/{email}")
    public int sendOtp(@PathVariable("email") String toEmail)
    {
    	Random random = new Random();
    	int min = 111111;
    	int max = 999999;
    	int otp = random.nextInt((max-min)+1)+min;
    	
    	String emailBody = "Hello,\n"
    	                 + "\n"
    	                 + "Thank you for starting your registration with Lost & Found Website!\n"
    	                 + "\n"
    	                 + "To complete your registration and ensure the security of your account, please use the following One-Time Password (OTP):\n"
    	                 + "\n"
    	                 + "Your OTP Code: " + otp + "\n"
    	                 + "\n"
    	                 + "This code is valid for the next 10 minutes. Please do not share this code with anyone.\n"
    	                 + "\n"
    	                 + "If you did not attempt to register for an account, please disregard this email.\n"
    	                 + "\n"
    	                 + "Welcome to our community!\n"
    	                 + "The Lost & Found Website Team";
    	
    	System.out.println("Sending OTP to " + toEmail+" and OTP is "+otp);
        mailService.sendMail(toEmail, "OTP for Registration", emailBody);
                
    	return otp;
    }

    @GetMapping("/send/{to}")
    public String sendMail(@PathVariable("to") String to) 
    {
    	System.out.println("Sending mail to: " + to);
        mailService.sendMail(to, "Lost & Found Notification", 
                "Your item has been found! Please check the platform.");
        return "Mail sent to " + to;
    }
    
    @PostMapping("/test")
    public String test()
    {
    	return "Working";
    }
}