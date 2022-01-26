package uz.pdp.appjparelationships.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.payload.SubjectsDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentRepository studentRepository;
    private final AddressRepository addressRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;


    public StudentController(StudentRepository studentRepository, AddressRepository addressRepository, GroupRepository groupRepository, SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.addressRepository = addressRepository;
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
    }


    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam(defaultValue = "0") Integer page) {
        if (page != 0)
            page--;
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAll(pageable);
    }


    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam(defaultValue = "0") Integer page) {
        if (page != 0)
            page--;
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
    }


    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                              @RequestParam(defaultValue = "0") Integer page){
        if (page != 0)
            page--;
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_Id(facultyId, pageable);
    }


    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer groupId,
                                                @RequestParam(defaultValue = "0") Integer page){
        if (page != 0)
            page--;
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Id(groupId, pageable);
    }


    @PostMapping
    public String addStudent(@RequestBody StudentDto studentDto) {

        Integer groupId = studentDto.getGroupId();
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (!optionalGroup.isPresent())
            return "Group Not Found !";

        Set<String> subjectNames = studentDto.getSubjectNames();
        Set<Subject> subjects = new HashSet<>();

        for (String subjectName : subjectNames) {
            Optional<Subject> optionalSubject = subjectRepository.findByName(subjectName);
            if (!optionalSubject.isPresent())
                return subjectName + " Subject not found !";
            subjects.add(optionalSubject.get());
        }

        Address address = new Address();
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());

        try {
            address = addressRepository.save(address);
        }
        catch (Exception e) {
            return "This Address already exists !";
        }

        Student student = new Student();

        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setAddress(address);
        student.setGroup(optionalGroup.get());
        student.setSubjects(subjects);

        studentRepository.save(student);
        return "Student Added Successfully !";
    }


    @PostMapping("/{id}/addSubject")
    public String addSubjectsToStudent(@PathVariable Integer id,
                                      @RequestBody SubjectsDto subjectsDto){

        Optional<Student> byId = studentRepository.findById(id);
        if (!byId.isPresent())
            return "Student Not Found !";

        Student student = byId.get();

        Set<String> subjectNames = subjectsDto.getSubjectNames();
        Set<Subject> subjectsNew = new HashSet<>();

        for (String subjectName : subjectNames) {
            Optional<Subject> optionalSubject = subjectRepository.findByName(subjectName);
            if (!optionalSubject.isPresent())
                return subjectName + " Subject not found !";
            subjectsNew.add(optionalSubject.get());
        }

        Set<Subject> subjects = student.getSubjects();
        subjects.addAll(subjectsNew);
        student.setSubjects(subjects);

        studentRepository.save(student);
        return "Subjects Added To Student !";
    }


    @PutMapping("/{id}")
    public String updateStudentById(@PathVariable Integer id,
                                @RequestBody StudentDto studentDto){

        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent())
            return "Student Not Found !";

        Student student = optionalStudent.get();
        Group group = student.getGroup();

        Integer groupId = studentDto.getGroupId();

        if (!group.getId().equals(groupId)){
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (!optionalGroup.isPresent())
                return "Group Not Found !";
            group = optionalGroup.get();
        }

        Set<String> subjectNames = studentDto.getSubjectNames();
        Set<Subject> subjects = new HashSet<>();

        for (String subjectName : subjectNames) {
            Optional<Subject> optionalSubject = subjectRepository.findByName(subjectName);
            if (!optionalSubject.isPresent())
                return subjectName + " Subject not found !";
            subjects.add(optionalSubject.get());
        }

        Address address = student.getAddress();

        String street = studentDto.getStreet();
        String district = studentDto.getDistrict();
        String city = studentDto.getCity();

        if (!address.getStreet().equals(street) || !address.getDistrict().equals(district) || !address.getCity().equals(city)) {
            address.setStreet(street);
            address.setDistrict(district);
            address.setCity(city);
            try {
                addressRepository.save(address);
            } catch (Exception e) {
                return "Address Already Exists !";
            }
        }

        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setAddress(address);
        student.setGroup(group);
        student.setSubjects(subjects);

        studentRepository.save(student);
        return "Student Updated Successfully !";
    }


    @DeleteMapping("/{id}")
    public String deleteStudentById(@PathVariable Integer id){
        try {
            studentRepository.deleteById(id);
            return "Student with id: " + id + " deleted successfully!";
        }
        catch (Exception e) {
            return "Student Not Found !";
        }
    }


    @DeleteMapping("/{id}/deleteGivenSubjects")
    public String deleteGivenSubjectsFromStudent(@PathVariable Integer id, @RequestBody SubjectsDto subjectsDto){

        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent())
            return "Student Not Found !!";

        Student student = optionalStudent.get();

        Set<String> subjectNames = subjectsDto.getSubjectNames();
        Set<Subject> subjectSet = new HashSet<>();

        for (String subjectName : subjectNames) {
            Optional<Subject> optionalSubject = subjectRepository.findByName(subjectName);
            if (!optionalSubject.isPresent())
                return subjectName + " Subject not found !!";
            subjectSet.add(optionalSubject.get());
        }

        Set<Subject> subjects = student.getSubjects();
        subjects.removeAll(subjectSet);

        student.setSubjects(subjects);

        studentRepository.save(student);

        return "Given Subjects are Deleted !";
    }

}