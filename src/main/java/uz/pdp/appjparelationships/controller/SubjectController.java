package uz.pdp.appjparelationships.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.SubjectDto;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.Optional;


@RestController
@RequestMapping(value = "/subject")
public class SubjectController {

    private final SubjectRepository subjectRepository;


    public SubjectController(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }


    @GetMapping("/{id}")
    public Subject getSubjectById(@PathVariable Integer id){
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        return optionalSubject.orElseGet(Subject::new);
    }


    @GetMapping
    public Page<Subject> getSubjects(@RequestParam(defaultValue = "0") Integer pageNo,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {

        if (pageNo != 0)
            pageNo--;

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return subjectRepository.findAll(pageable);
    }


    @PostMapping
    public String addSubject(@RequestBody SubjectDto subjectDto) {
        Subject subject = new Subject();
        subject.setName(subjectDto.getSubjectName());
        try {
            subjectRepository.save(subject);
            return "Subject Added Successfully !";
        }
        catch (Exception e){
            return "This Subject already exists !";
        }
    }


    @PutMapping("/{id}")
    public String updateSubjectById(@PathVariable Integer id, @RequestBody SubjectDto subjectDto){
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        if (!optionalSubject.isPresent())
            return "Subject Not Found !";

        Subject subject = optionalSubject.get();

        String subjectName = subjectDto.getSubjectName();
        if (!subject.getName().equals(subjectName)){
            try {
                subjectRepository.save(subject);
                return "Subject Updated Successfully !";
            }
            catch (Exception e){
                return "This Subject already exists !";
            }
        }

        return "Subject Updated Successfully !";
    }


    @DeleteMapping("/{id}")
    public String deleteSubjectById(@PathVariable Integer id){
        try{
            subjectRepository.deleteById(id);
            return "Subject Deleted Successfully !";
        }
        catch (EmptyResultDataAccessException e){
            return "Subject Not Found !";
        }
    }

}
