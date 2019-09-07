package com.demo.razorpay.controller;

import com.demo.razorpay.controller.helper.LoginControllerHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JspController extends RazorPayController{

    private static final Logger LOGGER = Logger.getLogger(JspController.class.getName());

    @Override
    protected void doProcess(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession(false);
        if(null == httpSession){
            try {
                response.sendRedirect("../pages/login.jsp");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                toErrorPage500(request, response);
                return;
            }
        }
    }
}
