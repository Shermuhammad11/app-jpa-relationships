package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Faculty;
import uz.pdp.appjparelationships.entity.University;
import uz.pdp.appjparelationships.payload.FacultyDto;
import uz.pdp.appjparelationships.repository.FacultyRepository;
import uz.pdp.appjparelationships.repository.UniversityRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/faculty")
public class FacultyController {

    @Autowired
    FacultyRepository facultyRepository;
    @Autowired
    UniversityRepository universityRepository;


    //VAZIRLIK UCHUN
    @GetMapping
    public List<Faculty> getFaculties() {
        return facultyRepository.findAll();
    }

    //    @RequestMapping(method = RequestMethod.POST)

    @PostMapping
    public String addFaculty(@RequestBody FacultyDto facultyDto) {

        String name = facultyDto.getName();
        if (name == null)
            return "Faculty Name is Empty !";

        Integer universityId = facultyDto.getUniversityId();
        if (universityId == null)
            return "University ID is Empty !";

        Optional<University> optionalUniversity =
                universityRepository.findById(universityId);

        if (!optionalUniversity.isPresent())
            return "University not found";

        boolean exists = facultyRepository.existsByNameAndUniversityId(name, universityId);
        if (exists)
            return "This university such faculty exist";

        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setUniversity(optionalUniversity.get());
        facultyRepository.save(faculty);
        return "Faculty saved";
    }


    //UNIVERSITET XODIMI UCHUN
    @GetMapping("/byUniversityId/{universityId}")
    public List<Faculty> getFacultiesByUniversityId(@PathVariable Integer universityId) {
        List<Faculty> allByUniversityId = facultyRepository.findAllByUniversityId(universityId);
        return allByUniversityId;
    }


    @DeleteMapping("/{id}")
    public String deleteFaculty(@PathVariable Integer id) {
        try {
            facultyRepository.deleteById(id);
            return "Faculty deleted";
        } catch (Exception e) {
            return "Error in deleting";
        }
    }


    @PutMapping("/{id}")
    public String editFaculty(@PathVariable Integer id, @RequestBody FacultyDto facultyDto) {
        Optional<Faculty> optionalFaculty = facultyRepository.findById(id);

        if (optionalFaculty.isPresent()) {

            Faculty faculty = optionalFaculty.get();

            String name = facultyDto.getName();
            if (name != null)
                faculty.setName(facultyDto.getName());

            Integer universityId = facultyDto.getUniversityId();

            if (universityId != null){

                Optional<University> optionalUniversity = universityRepository.findById(
                        facultyDto.getUniversityId());

                if (!optionalUniversity.isPresent())
                    return "University not found";

                faculty.setUniversity(optionalUniversity.get());
            }

            try {
                facultyRepository.save(faculty);
                return "Faculty edited";
            }
            catch (Exception e){
                return "Tnis Faculty Already Exists !";
            }

        }

        return "Faculty not found";
    }


}