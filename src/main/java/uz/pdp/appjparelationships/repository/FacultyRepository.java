package uz.pdp.appjparelationships.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appjparelationships.entity.Faculty;


public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

    Page<Faculty> findAllByUniversityId(Integer university_id, Pageable pageable);

}
