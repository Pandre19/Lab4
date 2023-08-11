package com.example.studentpractice.web;

import com.example.studentpractice.repositories.StudentRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.studentpractice.entities.Student;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

@SessionAttributes({"a", "e"})
@Controller
@AllArgsConstructor
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    static int num=0;

    @GetMapping(path="/index")
    public String students(Model model, @RequestParam(name="keyword",defaultValue = "") String keyword,
                           @RequestParam(name="seeAll",defaultValue = "false") String seeAllItems,
                           @RequestParam(name="action",defaultValue = "") String action){

        List<Student> students;
        if (seeAllItems.equals("true")) {
            students = studentRepository.findAll();
        } else if (keyword.replace(",", "").trim().isEmpty()) {
            students = studentRepository.findAll();
        } else {
            long key = Long.parseLong(keyword.replace(",", "").trim());
            students = studentRepository.findStudentById(key);
        }
        model.addAttribute("listStudents", students);
        model.addAttribute("action", action);
        return "students";
    }

    @GetMapping("/delete")
    public String delete(Long id){
        studentRepository.deleteById(id);
        return "redirect:/index";
    }

    @GetMapping("/formStudents")
    public String formStudents(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("action", "add");
        return "formStudentsPage";
    }

    @PostMapping(path="/save")
    public String save(Model model, Student student, BindingResult
            bindingResult, ModelMap mm, HttpSession session,
                       @RequestParam(name="action",defaultValue = "") String action) {
        if (bindingResult.hasErrors()) {
            return "formStudentsPage";
        } else {
            studentRepository.save(student);
            if (num == 2) {
                mm.put("e", 2);
                mm.put("a", 0);
            } else {
                mm.put("a", 1);
                mm.put("e", 0);
            }
            return "redirect:/index?action=" + action;
        }
    }

    @GetMapping("/editStudents")
    public String editStudents(Model model, Long id, HttpSession session){
        num = 2;
        session.setAttribute("info", 0);
        Student student = studentRepository.findById(id).orElse(null);
        if(student==null) {
            throw new RuntimeException("Patient does not exist");
        }
        model.addAttribute("student", student);
        model.addAttribute("action", "edit");

        return "editStudentsPage";
    }
}
