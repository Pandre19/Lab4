package com.example.studentpractice.web;

import com.example.studentpractice.entities.Student;
import com.example.studentpractice.repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

public class StudentControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentController studentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    public void testStudentsWithSeeAllTrue() throws Exception {
        List<Student> students = Arrays.asList(new Student(), new Student());

        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(get("/index?seeAll=true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listStudents", students))
                .andExpect(view().name("students"));

        verify(studentRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Long studentId = 1L;

        mockMvc.perform(get("/delete").param("id", studentId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));

        verify(studentRepository, times(1)).deleteById(studentId);
    }

    @Test
    public void testFormStudents() throws Exception {
        mockMvc.perform(get("/formStudents"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attribute("action", "add"))
                .andExpect(view().name("formStudentsPage"));
    }

    @Test
    public void testSaveStudent() throws Exception {
        Student student = new Student();
        mockMvc.perform(post("/save")
                        .flashAttr("student", student))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index?action="));
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    public void testEditStudents() throws Exception {
        Long studentId = 1L;
        Student student = new Student();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/editStudents").param("id", studentId.toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("student", student))
                .andExpect(model().attribute("action", "edit"))
                .andExpect(view().name("editStudentsPage"));
    }
}