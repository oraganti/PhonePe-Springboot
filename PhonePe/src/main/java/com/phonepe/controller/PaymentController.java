package com.phonepe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.phonepe.service.PaymentService;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/pay")
    public RedirectView pay() throws NoSuchAlgorithmException {
        return paymentService.initiatePayment();
    }
//this is phonepe formparams
//one more
    @PostMapping("/paymentsuccess")
    public String paymentReturn(@RequestParam Map<String, String> formParams) throws NoSuchAlgorithmException {
    	System.out.println("msg  ---------------------"+formParams);
        return paymentService.handlePaymentReturn(formParams);
    }
}
