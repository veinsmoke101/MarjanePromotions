package com.marjanepromotion.controllers;

import com.marjanepromotion.dao.AdminDao;
import com.marjanepromotion.dao.PromotionDao;
import com.marjanepromotion.models.Admin;
import com.marjanepromotion.models.DepartmentManager;
import com.marjanepromotion.models.Director;
import com.marjanepromotion.models.Promotion;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "DirectorDashboard", value = "/dashboard/director/*")
public class DirectorDashboard extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // check if the route is correct

        String[] queryParams = request.getRequestURI().split("/");

        //      - check if url parameters is valid
        int pageNumber = 1;
        int rowsPerPage = 8;

        if(queryParams.length > 5 || queryParams.length == 3){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // check if the forth argument of the request is valid
        if(queryParams.length < 4 || !queryParams[3].equals("accepted") && !queryParams[3].equals("not-accepted") && !queryParams[3].equals("pending") && !queryParams[3].equals("admins")){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //      - check if pageNumber parameter is an int
        try{
            if(queryParams.length == 5){
                pageNumber = Integer.parseInt(queryParams[4]);
                if(pageNumber <= 0)
                    throw new NumberFormatException();
            }
        }catch(NumberFormatException exception){
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }


        if(request.getSession().getAttribute("userType") == null){
            response.sendRedirect("/login/director");
            return;
        }

        if(!request.getSession().getAttribute("userType").equals("director")){
            response.sendRedirect("/dashboard/" + request.getSession().getAttribute("userType"));
            return;
        }


        ArrayList<String> links = new ArrayList<>();
        links.add("accepted");
        links.add("pending");
        links.add("not-accepted");
        links.add("admins");

        request.setAttribute("links", links);
        request.setAttribute("dashboardType", "director");


        Director logged = (Director) request.getSession().getAttribute("user");
        PromotionDao promotionDao = new PromotionDao();
        AdminDao adminDao = new AdminDao();

        List<Promotion> promotions = promotionDao.findAll();


        switch(queryParams[3]){

            case "admins" -> {
                List<Admin> admins = adminDao.findAll();
                request.setAttribute("admins", admins);
                request.setAttribute("dataType", "admins");
            }

            case "accepted" -> {
                promotions = promotions.stream().filter(promotion -> promotion.getStatus().equals("accepted")).collect(Collectors.toList());
                request.setAttribute("dataType", "accepted");
            }

            case "not-accepted" -> {
                promotions = promotions.stream().filter(promotion -> promotion.getStatus().equals("refused") || promotion.getStatus().equals("not-checked")).collect(Collectors.toList());
                request.setAttribute("dataType", "not-accepted");
            }

            case "pending" -> {
                promotions = promotions.stream().filter(promotion -> promotion.getStatus().equals("pending")).collect(Collectors.toList());
                request.setAttribute("dataType", "pending");
            }
        }

        if(!queryParams[3].equals("admins")){
            request.setAttribute("promotions",promotions);
        }

        request.getRequestDispatcher("/dashboard/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}