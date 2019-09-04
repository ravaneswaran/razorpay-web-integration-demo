package com.demo.razorpay.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController extends RazorPayController{

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @Override
    protected void doProcess(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("=============================>>>>>>>>>");
        try {
            response.getWriter().print("success");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            toErrorPage500(request, response);
            return;
        }
    }
}