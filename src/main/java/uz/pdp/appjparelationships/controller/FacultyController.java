package uz.pdp.appjparelationships.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Faculty;
import uz.pdp.appjparelationships.entity.University;
import uz.pdp.appjparelationships.payload.FacultyDto;
import uz.pdp.appjparelationships.repository.FacultyRepository;
import uz.pdp.appjparelationships.repository.UniversityRepository;

import java.util.Optional;


@RestController
@RequestMapping(value = "/faculty")
public class FacultyController {

    private final FacultyRepository facultyRepository;
    private final UniversityRepository universityRepository;


    public FacultyController(FacultyRepository facultyRepository, UniversityRepository universityRepository) {
        this.facultyRepository = facultyRepository;
        this.universityRepository = universityRepository;
    }


    @GetMapping("/{id}")
    public Faculty getFacultyById(@PathVariable Integer id){
        Optional<Faculty> optionalFaculty = facultyRepository.findById(id);
        return optionalFaculty.orElseGet(Faculty::new);
    }


    @GetMapping
    public Page<Faculty> getFaculties(@RequestParam(defaultValue = "0") Integer pageNo,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {

        if (pageNo != 0)
            pageNo--;

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return facultyRepository.findAll(pageable);
    }


    @GetMapping("/byUniversityId/{universityId}")
    public Page<Faculty> getFacultiesByUniversityId(@PathVariable Integer universityId,
                                                    @RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {

        if (pageNo != 0)
            pageNo--;

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return facultyRepository.findAllByUniversityId(universityId, pageable);
    }


    @PostMapping
    public String addFaculty(@RequestBody FacultyDto facultyDto) {

        Integer universityId = facultyDto.getUniversityId();

        Optional<University> optionalUniversity = universityRepository.findById(universityId);

        if (!optionalUniversity.isPresent())
            return "University not found !";

        Faculty faculty = new Faculty();
        faculty.setName(facultyDto.getName());
        faculty.setUniversity(optionalUniversity.get());
        try {
            facultyRepository.save(faculty);
            return "Faculty Added Successfully !";
        }
        catch (Exception e){
            return "This Faculty already exists in given University !";
        }
    }


    @PutMapping("/{id}")
    public String updateFacultyById(@PathVariable Integer id, @RequestBody FacultyDto facultyDto) {

        Optional<Faculty> optionalFaculty = facultyRepository.findById(id);

        if (optionalFaculty.isPresent()) {

            Faculty faculty = optionalFaculty.get();
            University university = faculty.getUniversity();

            Integer universityId = facultyDto.getUniversityId();

            boolean checkChange = false;

            if (!university.getId().equals(universityId)) {
                Optional<University> optionalUniversity = universityRepository.findById(universityId);
                if (!optionalUniversity.isPresent())
                    return "University not found !";
                university = optionalUniversity.get();
                checkChange = true;
            }

            String facultyName = facultyDto.getName();
            if (!faculty.getName().equals(facultyName))
                checkChange = true;

            if (checkChange) {
                faculty.setName(facultyName);
                faculty.setUniversity(university);
                try {
                    facultyRepository.save(faculty);
                    return "Faculty Updated Successfully !";
                } catch (Exception e) {
                    return "This Faculty already exists in given University !";
                }
            }

            return "Faculty Updated Successfully !";
        }

        return "Faculty not found !";
    }


    @DeleteMapping("/{id}")
    public String deleteFacultyById(@PathVariable Integer id) {
        try {
            facultyRepository.deleteById(id);
            return "Faculty Deleted Successfully !";
        } catch (Exception e) {
            return "Faculty not found !";
        }
    }


}