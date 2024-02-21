package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.repository.MathGradeDao;
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
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUpDatabase(){
        jdbcTemplate.execute("insert into student(id, firstname, lastname, email_address) values (1, 'Eric', 'Foreman', 'eric@wp.pl')");
    }


    @Test
    public void createStudentService(){
        studentService.createStudent("Chad","Foreman","eric@wp.pl");

        CollegeStudent student = studentDao.findByEmailAddress("eric@wp.pl");

        assertEquals("eric@wp.pl",student.getEmailAddress(),"find by email");
    }

    @Test
    public void isStudentBullCheck(){
        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService(){
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);
        assertTrue(deletedCollegeStudent.isPresent(),"Return true");
        studentService.deleteStudent(1);
        deletedCollegeStudent = studentDao.findById(1);
        assertFalse(deletedCollegeStudent.isPresent(),"Return false");
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
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        assertTrue(mathGrades.iterator().hasNext(),"Student has math grades");
    }

    @AfterEach
    public void setUpAfterTransactions(){
        jdbcTemplate.execute("DELETE from student");
    }

}
