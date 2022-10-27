package com.marjanepromotion.controllers;

import com.marjanepromotion.dao.AdminDao;
import com.marjanepromotion.models.Admin;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "AdminLogin", urlPatterns = {"/login/admin"})
public class AdminLogin extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession(false) != null){
            response.getWriter().println("Already logged in as " + request.getSession().getAttribute("userType"));
            return;
        }
        request.setAttribute("userType", "admin");
        request.getRequestDispatcher("/login/Login.jsp").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getSession(false) != null){
            response.getWriter().println("Already logged in as " + request.getSession().getAttribute("userType"));
            return;
        }
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if(email == null || password == null){
            response.setStatus(404);
            response.getWriter().println("Incomplete login data");
            return;
        }

        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPassword(password);

        AdminDao adminDao = new AdminDao();
        Integer adminID = adminDao.login(admin);
        if(adminID != null) {
            HttpSession session = request.getSession();
            session.setAttribute("userType", "admin");
            Admin logged = adminDao.findOne(adminID);
            System.out.println("admin : " + logged.getId());
            session.setAttribute("user", logged);
            response.getWriter().println("Session Created with " + session.getId());
        }else
            response.getWriter().println("Session is not created");

    }



}