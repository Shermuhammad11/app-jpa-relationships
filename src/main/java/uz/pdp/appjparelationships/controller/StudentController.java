package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.payload.SubjectDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam(defaultValue = "0") Integer page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        if (page != 0)
            page--;
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAll(pageable);
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam(defaultValue = "0") Integer page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        if (page != 0)
            page--;
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                              @RequestParam(defaultValue = "0") Integer page){
        if (page != 0)
            page--;
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_Id(facultyId, pageable);
    }


    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer groupId,
                                                @RequestParam(defaultValue = "0") Integer page){
        if (page != 0)
            page--;
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Id(groupId, pageable);
    }

    @PostMapping("/addStudent")
    public String addStudent(@RequestBody StudentDto studentDto) {

        String firstName = studentDto.getFirstName();
        if (firstName == null)
            return "First Name is Empty !";
        String lastName = studentDto.getLastName();
        if (lastName == null)
            return "Last Name is Empty !";

        Integer groupId = studentDto.getGroupId();
        Optional<Group> byId = groupRepository.findById(groupId);
        if (!byId.isPresent())
            return "Group Not Found !";

        Address address = new Address();
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());

        Address saveAddress;

        try {
            saveAddress = addressRepository.save(address);
        }
        catch (Exception e) {
            return "Address Already Exists !";
        }

        Student student = new Student();

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setAddress(saveAddress);
        student.setGroup(byId.get());

        Set<String> subjects = studentDto.getSubjects();

        if (subjects.isEmpty()) {
            studentRepository.save(student);
            return "Student Added, But Subjects are Empty !";
        }

        student = studentRepository.save(student);
        addSubjects(student, subjects);

        studentRepository.save(student);
        return "Student Added Successfully !";
    }

    @PostMapping("/{id}/addSubject")
    public String addSubjectToStudent(@PathVariable Integer id,
                                      @RequestBody SubjectDto subjectDto){

        Optional<Student> byId = studentRepository.findById(id);
        if (!byId.isPresent())
            return "Student Not Found !";

        Set<String> subjects = subjectDto.getSubjects();
        if (subjects.isEmpty())
            return "Subjects Are Empty !";

        Student student = byId.get();
        addSubjects(student, subjects);

        studentRepository.save(student);
        return "Subjects Added To Student !";
    }

    @PutMapping("/{id}")
    public String updateStudent(@PathVariable Integer id,
                                @RequestBody StudentDto studentDto){

        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent())
            return "Student Nor Found !";

        Student student = optionalStudent.get();

        String x = studentDto.getFirstName();
        if (x != null)
            student.setFirstName(x);
        x = studentDto.getLastName();
        if(x != null)
            student.setLastName(x);

        Address address = student.getAddress();
        x = studentDto.getCity();
        if (x != null)
            address.setCity(x);
        x = studentDto.getDistrict();
        if (x != null)
            address.setDistrict(x);
        x = studentDto.getStreet();
        if (x != null)
            address.setStreet(x);

        try {
            addressRepository.save(address);
        }
        catch (Exception e){
            return "Address Already Exists !";
        }

        Integer groupId = studentDto.getGroupId();
        if (groupId != null) {
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (!optionalGroup.isPresent())
                return "Group Not Found !";
            student.setGroup(optionalGroup.get());
        }

        Set<String> subjects = studentDto.getSubjects();

        if (!subjects.isEmpty()){
            student.getSubjects().clear();
            addSubjects(student, subjects);
        }

        studentRepository.save(student);
        return "Student Updated Successfully !";
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id){

        Optional<Student> optionalStudent = studentRepository.findById(id);

        if (optionalStudent.isPresent()){
            Student student = optionalStudent.get();
            student.getSubjects().clear();
            studentRepository.save(student);
            studentRepository.deleteById(id);
            return "Student with id: " + id + " deleted successfully!";
        }

        return "Student Not Found !";
    }

    @DeleteMapping("/{id}/deleteGivenSubjects")
    public String deleteGivenSubjectsFromStudent(@PathVariable Integer id,
                                        @RequestBody SubjectDto subjectDto){

        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent())
            return "Student Not Found !";

        Set<String> subjects = subjectDto.getSubjects();
        if (subjects.isEmpty())
            return "Subjects List is Empty !";

        Student student = optionalStudent.get();

        for (String subjectName : subjects) {
            Subject subject = subjectRepository.findByName(subjectName);
            if (subject != null)
                student.removeSubject(subject);
        }

        studentRepository.save(student);

        return "Given Subjects are Deleted !";
    }

    private void addSubjects(Student student, Set<String> subjects) {

        for (String subjectName: subjects) {
            Subject subject = subjectRepository.findByName(subjectName);
            if (subject == null) {
                subject = new Subject();
                subject.setName(subjectName);
                subject = subjectRepository.save(subject);
            }

            student.addSubject(subject);
        }
    }



}