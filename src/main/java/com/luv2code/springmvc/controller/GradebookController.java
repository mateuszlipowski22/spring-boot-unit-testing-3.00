package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

	@Autowired
	private StudentAndGradeService studentAndGradeService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {
		Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
		m.addAttribute("students", collegeStudents);
        return "index";
    }


    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        if(!studentAndGradeService.checkIfStudentIsNull(id)){
            return "error";
        }

        GradebookCollegeStudent gradebookCollegeStudent = studentAndGradeService.studentInformation(id);

        m.addAttribute("student", gradebookCollegeStudent);

        if(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size()>0){
            m.addAttribute("mathAverage", gradebookCollegeStudent.getStudentGrades()
                    .findGradePointAverage(gradebookCollegeStudent.getStudentGrades().getMathGradeResults()));
        }else {
            m.addAttribute("mathAverage","N/A");
        }

        if(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size()>0){
            m.addAttribute("scienceAverage", gradebookCollegeStudent.getStudentGrades()
                    .findGradePointAverage(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults()));
        }else {
            m.addAttribute("scienceAverage","N/A");
        }

        if(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size()>0){
            m.addAttribute("historyAverage", gradebookCollegeStudent.getStudentGrades()
                    .findGradePointAverage(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults()));
        }else {
            m.addAttribute("historyAverage","N/A");
        }

        return "studentInformation";
    }

    @PostMapping("/")
    public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m) {
        studentAndGradeService.createStudent(student.getFirstname(), student.getLastname(), student.getEmailAddress());
        Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradebook();
        m.addAttribute("students", collegeStudents);
        return "index";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable int id, Model m) {

        if(!studentAndGradeService.checkIfStudentIsNull(id)){
            return "error";
        }

        studentAndGradeService.deleteStudent(id);
        m.addAttribute("students", studentAndGradeService.getGradebook());
        return "index";
    }
}
