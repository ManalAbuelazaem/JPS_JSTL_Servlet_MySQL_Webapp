/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manalsoft.web.jdbc;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author ManalAbuelazaem
 */
public class StudentControllerServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

	private StudentDbUtil studentDbUtil;
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		// create our student db util ... and pass in the conn pool / datasource
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
		}
		catch (Exception exc) {
			throw new ServletException(exc);
		}
	}

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String theCommand = request.getParameter("command");
            
            if(theCommand == null){
                theCommand = "LIST";
            }
            
            switch (theCommand){
                case "LIST":
                    listStudents(request, response);
                    break;
                case "ADD":
                    addStudent(request, response);
                    break;
                case "LOAD":
                    loadStudent(request, response);
                    break;
                case "UPDATE":
                    updateStudent(request, response);
                    break;
                case "DELETE":
                    deleteStudent(request, response);
                    break;
                default:
                    listStudents(request, response);
            }
            
        } catch (Exception exc) {
            throw new ServletException(exc);
        }
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {

		// get students from db util
		List<Student> students = studentDbUtil.getStudents();
		
		// add students to the request
		request.setAttribute("STUDENT_LIST", students);
				
		// send to JSP page (view)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}

    private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //reade student info from data
        String firstName = request.getParameter("firstName"); 
        String lastName = request.getParameter("lastName"); 
        String email = request.getParameter("email"); 
        
        // create a new student object
        Student theStudent = new Student(firstName, lastName, email);
        
        // add the student to the database
        studentDbUtil.addStudent(theStudent);
        
        // send back to database (the student list )
        listStudents(request, response);
        
    }

    private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // read the student id from form data
        String studentId = request.getParameter("studentId");
        
        // get student from database (db util)
        Student theStudent = studentDbUtil.getStudent(studentId);
        
        // place student in the request attribute
        request.setAttribute("THE_STUDENT", theStudent);
        
        // send to jsp page: update-student-form.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
        dispatcher.forward(request, response);
    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // read info from form data
        int id = Integer.parseInt(request.getParameter("studentId"));
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        
        // create a new student object
        Student theStudent = new Student(id, firstName, lastName, email);
        
        // perform update on database
        studentDbUtil.updateStudent(theStudent);
        
        // send them back to the list student page
        listStudents(request, response);
        
    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // read student info from the form data
        String theStudentId = request.getParameter("studentId");
        
        // delete student from the database
        studentDbUtil.deleteStudent(theStudentId);
        
        // send them back to list student page
        listStudents(request, response);
    }
    

}
