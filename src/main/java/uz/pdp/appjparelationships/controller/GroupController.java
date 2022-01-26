package uz.pdp.appjparelationships.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Faculty;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.payload.GroupDto;
import uz.pdp.appjparelationships.repository.FacultyRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;

import java.util.Optional;


@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupRepository groupRepository;
    private final FacultyRepository facultyRepository;


    public GroupController(GroupRepository groupRepository, FacultyRepository facultyRepository) {
        this.groupRepository = groupRepository;
        this.facultyRepository = facultyRepository;
    }


    @GetMapping("/{id}")
    public Group getGroupById(@PathVariable Integer id){
        Optional<Group> optionalGroup = groupRepository.findById(id);
        return optionalGroup.orElseGet(Group::new);
    }


    @GetMapping
    public Page<Group> getGroups(@RequestParam(defaultValue = "0") Integer pageNo,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {

        if (pageNo != 0)
            pageNo--;

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return groupRepository.findAll(pageable);
    }


    @GetMapping("/byUniversityId/{universityId}")
    public Page<Group> getGroupsByUniversityId(@PathVariable Integer universityId,
                                               @RequestParam(defaultValue = "0") Integer pageNo,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {

        if (pageNo != 0)
            pageNo--;

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return groupRepository.findAllByFaculty_UniversityId(universityId, pageable);
    }


    @GetMapping("/byFacultyId/{facultyId}")
    public Page<Group> getGroupsByFacultyId(@PathVariable Integer facultyId,
                                            @RequestParam(defaultValue = "0") Integer pageNo,
                                            @RequestParam(defaultValue = "10") Integer pageSize){

        if (pageNo != 0)
            pageNo--;

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return groupRepository.findAllByFaculty_Id(facultyId, pageable);
    }


    @PostMapping
    public String addGroup(@RequestBody GroupDto groupDto) {

        Integer facultyId = groupDto.getFacultyId();

        Optional<Faculty> optionalFaculty = facultyRepository.findById(facultyId);
        if (!optionalFaculty.isPresent())
            return "Such faculty not found";

        Group group = new Group();
        group.setName(groupDto.getName());
        group.setFaculty(optionalFaculty.get());

        try {
            groupRepository.save(group);
            return "Group Added Successfully !";
        }
        catch (Exception e){
            return "This Group already exists in given Faculty !";
        }
    }


    @PutMapping("/{id}")
    public String updateGroupById(@PathVariable Integer id, @RequestBody GroupDto groupDto){

        Optional<Group> byId = groupRepository.findById(id);
        if (!byId.isPresent())
            return "Group Not Found !";

        Group group = byId.get();
        Faculty faculty = group.getFaculty();

        boolean checkChange = false;

        Integer facultyId = groupDto.getFacultyId();
        if (!faculty.getId().equals(facultyId)){
            Optional<Faculty> optionalFaculty = facultyRepository.findById(facultyId);
            if (!optionalFaculty.isPresent())
                return "Faculty Not Found !";
            faculty = optionalFaculty.get();
            checkChange = true;
        }

        String name = groupDto.getName();
        if (!group.getName().equals(name))
            checkChange = true;

        if (checkChange) {
            try {
                group.setName(name);
                group.setFaculty(faculty);
                groupRepository.save(group);
                return "Group Updated Successfully !";
            } catch (Exception e) {
                return "This Group already exists in given Faculty !";
            }
        }

        return "Group Updated Successfully !";
    }


    @DeleteMapping("/{id}")
    public String deleteGroupById(@PathVariable Integer id){
        try {
            groupRepository.deleteById(id);
            return "Group Deleted Successfully !";
        }
        catch (Exception e){
            return "Group Not Found !";
        }
    }


}
