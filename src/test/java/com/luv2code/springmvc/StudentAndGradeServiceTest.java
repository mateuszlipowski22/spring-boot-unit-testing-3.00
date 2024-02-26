package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUpDatabase(){
        jdbcTemplate.execute("insert into student(id, firstname, lastname, email_address) values (1, 'Eric', 'Foreman', 'eric@wp.pl')");

        jdbcTemplate.execute("insert into math_grade(id, student_id, grade) values (1, 1, 100.00)");
        jdbcTemplate.execute("insert into science_grade(id, student_id, grade) values (1, 1, 90.00)");
        jdbcTemplate.execute("insert into history_grade(id, student_id, grade) values (1, 1, 13.00)");
    }


    @Test
    public void createStudentService(){
        studentService.createStudent("Chad","Foreman","ericforeman@wp.pl");

        CollegeStudent student = studentDao.findByEmailAddress("ericforeman@wp.pl");

        assertEquals("ericforeman@wp.pl",student.getEmailAddress(),"find by email");
    }

    @Test
    public void isStudentBullCheck(){
        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService(){
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);
        Optional<MathGrade> deletedMathGrade = mathGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradeDao.findById(1);

        assertTrue(deletedCollegeStudent.isPresent(),"Return true");
        assertTrue(deletedMathGrade.isPresent(),"Return true");
        assertTrue(deletedHistoryGrade.isPresent(),"Return true");
        assertTrue(deletedScienceGrade.isPresent(),"Return true");

        studentService.deleteStudent(1);

        deletedCollegeStudent = studentDao.findById(1);
        deletedMathGrade= mathGradeDao.findById(1);
        deletedHistoryGrade = historyGradeDao.findById(1);
        deletedScienceGrade = scienceGradeDao.findById(1);

        assertFalse(deletedCollegeStudent.isPresent(),"Return false");
        assertFalse(deletedMathGrade.isPresent(),"Return false");
        assertFalse(deletedHistoryGrade.isPresent(),"Return false");
        assertFalse(deletedScienceGrade.isPresent(),"Return false");
    }

    @Sql("/insertData.sql")
    @Test
    public void getGradebookService(){
        Iterable<CollegeStudent> iterableCollegeStudent = studentService.getGradebook();
        List<CollegeStudent> collegeStudentList = new ArrayList<>();
        for(CollegeStudent collegeStudent : iterableCollegeStudent){
            collegeStudentList.add(collegeStudent);
        }

        assertEquals(5, collegeStudentList.size());
    }

    @Test
    public void creatGradeService(){
        assertTrue(studentService.createGrade(80.5,1,"math"));
        assertTrue(studentService.createGrade(80.5,1,"science"));
        assertTrue(studentService.createGrade(80.5,1,"history"));

        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        assertTrue(((Collection<MathGrade>) mathGrades).size()==2,"Student has math grades");
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size()==2,"Student has science grades");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size()==2,"Student has history grades");
    }


    @Test
    public void creatGradeServiceReturnFalse() {
        assertFalse(studentService.createGrade(105, 1, "math"));
        assertFalse(studentService.createGrade(-5, 1, "math"));
        assertFalse(studentService.createGrade(80, 2, "math"));
        assertFalse(studentService.createGrade(80, 2, "literature"));
    }

    @Test
    public void deleteGradeService() {
        assertEquals(1,studentService.deleteGrade(1,"math"),"Returns student id after delete");
        assertEquals(1,studentService.deleteGrade(1,"science"),"Returns student id after delete");
        assertEquals(1,studentService.deleteGrade(1,"history"),"Returns student id after delete");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero() {
        assertEquals(0,studentService.deleteGrade(0,"science"),"No student should have 0 id");
        assertEquals(0,studentService.deleteGrade(0,"literature"),"No student should have a literature grade");
    }

    @AfterEach
    public void setUpAfterTransactions(){
        jdbcTemplate.execute("DELETE from student");
        jdbcTemplate.execute("DELETE from math_grade");
        jdbcTemplate.execute("DELETE from science_grade");
        jdbcTemplate.execute("DELETE from history_grade");
    }

}
