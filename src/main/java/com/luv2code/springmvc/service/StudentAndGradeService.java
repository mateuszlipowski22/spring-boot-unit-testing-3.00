package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Autowired
    private StudentGrades studentGrades;


    public void createStudent(String firstName, String lastName, String email) {
        CollegeStudent student = new CollegeStudent(firstName, lastName, email);
        student.setId(0);
        studentDao.save(student);
    }

    public boolean checkIfStudentIsNull(int id) {
        Optional<CollegeStudent> student = studentDao.findById(id);
        return student.isPresent();
    }

    public void deleteStudent(int id) {
        if (checkIfStudentIsNull(id)) {
            studentDao.deleteById(id);
            mathGradeDao.deleteByStudentId(id);
            historyGradeDao.deleteByStudentId(id);
            scienceGradeDao.deleteByStudentId(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook() {
        return studentDao.findAll();
    }

    public boolean createGrade(double grade, int studentId, String gradeType) {

        if (!checkIfStudentIsNull(studentId)) {
            return false;
        }

        if (grade >= 0 && grade <= 100) {
            if (gradeType.equals("math")) {
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentId);
                mathGradeDao.save(mathGrade);
                return true;
            }

            if (gradeType.equals("science")) {
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(studentId);
                scienceGradeDao.save(scienceGrade);
                return true;
            }

            if (gradeType.equals("history")) {
                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(studentId);
                historyGradeDao.save(historyGrade);
                return true;
            }
        }

        return false;
    }

    public int deleteGrade(int id, String gradeType) {

        int studentId = 0;

        if (gradeType.equals("math")) {
            Optional<MathGrade> grade = mathGradeDao.findById(id);
            if (!grade.isPresent()) {
                return studentId;
            }
            studentId = grade.get().getStudentId();
            mathGradeDao.deleteById(id);
        }

        if (gradeType.equals("science")) {
            Optional<ScienceGrade> grade = scienceGradeDao.findById(id);
            if (!grade.isPresent()) {
                return studentId;
            }
            studentId = grade.get().getStudentId();
            scienceGradeDao.deleteById(id);
        }

        if (gradeType.equals("history")) {
            Optional<HistoryGrade> grade = historyGradeDao.findById(id);
            if (!grade.isPresent()) {
                return studentId;
            }
            studentId = grade.get().getStudentId();
            historyGradeDao.deleteById(id);
        }

        return studentId;
    }

    public GradebookCollegeStudent studentInformation(int studentId) {

        if(!checkIfStudentIsNull(studentId)){
            return null;
        }

        Optional<CollegeStudent> collegeStudent = studentDao.findById(studentId);

        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(studentId);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(studentId);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(studentId);

        List<Grade> mathGradeList = new ArrayList<>();
        mathGrades.forEach(mathGradeList::add);

        List<Grade> historyGradeList = new ArrayList<>();
        historyGrades.forEach(historyGradeList::add);

        List<Grade> scienceGradeList = new ArrayList<>();
        scienceGrades.forEach(scienceGradeList::add);

        studentGrades.setHistoryGradeResults(historyGradeList);
        studentGrades.setMathGradeResults(mathGradeList);
        studentGrades.setScienceGradeResults(scienceGradeList);

        GradebookCollegeStudent gradebookCollegeStudent = new GradebookCollegeStudent(
                collegeStudent.get().getId(),
                collegeStudent.get().getFirstname(),
                collegeStudent.get().getLastname(),
                collegeStudent.get().getEmailAddress(),
                studentGrades);

        return gradebookCollegeStudent;

    }

    public void configureStudentInformationModel(int id, Model model){
        GradebookCollegeStudent gradebookCollegeStudent = studentInformation(id);

        model.addAttribute("student", gradebookCollegeStudent);

        if(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size()>0){
            model.addAttribute("mathAverage", gradebookCollegeStudent.getStudentGrades()
                    .findGradePointAverage(gradebookCollegeStudent.getStudentGrades().getMathGradeResults()));
        }else {
            model.addAttribute("mathAverage","N/A");
        }

        if(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size()>0){
            model.addAttribute("scienceAverage", gradebookCollegeStudent.getStudentGrades()
                    .findGradePointAverage(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults()));
        }else {
            model.addAttribute("scienceAverage","N/A");
        }

        if(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size()>0){
            model.addAttribute("historyAverage", gradebookCollegeStudent.getStudentGrades()
                    .findGradePointAverage(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults()));
        }else {
            model.addAttribute("historyAverage","N/A");
        }
    }
}
