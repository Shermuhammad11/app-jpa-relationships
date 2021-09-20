package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Faculty;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.payload.GroupDto;
import uz.pdp.appjparelationships.repository.FacultyRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    FacultyRepository facultyRepository;

    //VAZIRLIK UCHUN
    //READ
    @GetMapping
    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    //UNIVERSITET MAS'UL XODIMI UCHUN
    @GetMapping("/byUniversityId/{universityId}")
    public List<Group> getGroupsByUniversityId(@PathVariable Integer universityId) {
        return groupRepository.findAllByFaculty_UniversityId(universityId);
    }

    // Fakultet Dekanati uchun
    @GetMapping("/byFacultyId/{facultyId}")
    public List<Group> getGroupsByFacultyId(@PathVariable Integer facultyId){
        return groupRepository.findAllByFaculty_Id(facultyId);
    }

    @PostMapping
    public String addGroup(@RequestBody GroupDto groupDto) {

        Integer facultyId = groupDto.getFacultyId();
        if (facultyId == null)
            return "Group ID is Empty !";

        String name = groupDto.getName();
        if (name == null)
            return "Group Name is Empty !";

        Optional<Faculty> optionalFaculty = facultyRepository.findById(facultyId);
        if (!optionalFaculty.isPresent())
            return "Such faculty not found";

        Group group = new Group();
        group.setName(groupDto.getName());
        group.setFaculty(optionalFaculty.get());

        try {
            groupRepository.save(group);
            return "Group Added !";
        }
        catch (Exception e){
            return "This Group Already Exists !";
        }

    }

    @DeleteMapping("/{id}")
    public String deleteGroup(@PathVariable Integer id){
        try {
            groupRepository.deleteById(id);
            return "Group deleted !";
        }
        catch (Exception e){
            return "Error in deleting group !";
        }
    }

    @PutMapping("/{id}")
    public String updateGroup(@PathVariable Integer id, @RequestBody GroupDto groupDto){

        Optional<Group> byId = groupRepository.findById(id);
        if (!byId.isPresent())
            return "Group Not Found !";

        Group group = byId.get();

        String name = groupDto.getName();
        if (name != null)
            group.setName(name);

        Integer facultyId = groupDto.getFacultyId();
        if (facultyId != null){
            Optional<Faculty> byId1 = facultyRepository.findById(facultyId);
            if (!byId1.isPresent())
                return "Faculty Not Found !";
            group.setFaculty(byId1.get());
        }

        try {
            groupRepository.save(group);
            return "Group Updated Succesfully !";
        }
        catch (Exception e){
            return "This Group Already Exists In Given Faculty !";
        }
    }

}
