package uz.pdp.appjparelationships.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appjparelationships.entity.Group;


public interface GroupRepository extends JpaRepository<Group, Integer> {

    Page<Group> findAllByFaculty_UniversityId(Integer faculty_university_id, Pageable pageable);

    Page<Group> findAllByFaculty_Id(Integer facultyId, Pageable pageable);

}
