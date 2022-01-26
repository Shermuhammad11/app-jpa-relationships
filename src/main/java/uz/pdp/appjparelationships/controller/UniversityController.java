package uz.pdp.appjparelationships.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.University;
import uz.pdp.appjparelationships.payload.UniversityDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.UniversityRepository;

import java.util.Optional;

@RestController
@RequestMapping("/university")
public class UniversityController {

    private final UniversityRepository universityRepository;
    private final AddressRepository addressRepository;


    public UniversityController(UniversityRepository universityRepository, AddressRepository addressRepository) {
        this.universityRepository = universityRepository;
        this.addressRepository = addressRepository;
    }


    @GetMapping("/{id}")
    public University getUniversityById(@PathVariable Integer id){
        Optional<University> optionalUniversity = universityRepository.findById(id);
        return optionalUniversity.orElseGet(University::new);
    }


    @GetMapping
    public Page<University> getUniversities(@RequestParam(defaultValue = "0") Integer pageNo,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {

        if (pageNo != 0)
            pageNo--;

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        return universityRepository.findAll(pageable);
    }


    @PostMapping
    public String addUniversity(@RequestBody UniversityDto universityDto) {

        String name = universityDto.getName();
        if (universityRepository.existsByName(name))
            return "This University already exists !";

        Address address = new Address();
        address.setCity(universityDto.getCity());
        address.setDistrict(universityDto.getDistrict());
        address.setStreet(universityDto.getStreet());

        try {
            address = addressRepository.save(address);
        }
        catch (Exception e){
            return "This address already exist !";
        }

        University university = new University();
        university.setName(name);
        university.setAddress(address);
        universityRepository.save(university);
        return "University Added Successfully !";
    }


    @PutMapping("/{id}")
    public String updateUniversityById(@PathVariable Integer id, @RequestBody UniversityDto universityDto) {

        Optional<University> optionalUniversity = universityRepository.findById(id);

        if (optionalUniversity.isPresent()) {

            String name = universityDto.getName();

            University university = optionalUniversity.get();

            if (!university.getName().equals(name)){
                if (universityRepository.existsByName(name))
                    return "This University already exists !";
            }

            Address address = university.getAddress();

            String city = universityDto.getCity();
            String district = universityDto.getDistrict();
            String street = universityDto.getStreet();

            if (!address.getCity().equals(city) || !address.getDistrict().equals(district) || !address.getStreet().equals(street)){
                address.setCity(city);
                address.setDistrict(district);
                address.setStreet(street);
                try{
                    addressRepository.save(address);
                }
                catch (Exception e){
                    return "This address already exist !";
                }
            }

            university.setName(name);

            universityRepository.save(university);
            return "University Updated Successfully !";
        }

        return "University not found";
    }


    @DeleteMapping("/{id}")
    public String deleteUniversityById(@PathVariable Integer id){
        try {
            universityRepository.deleteById(id);
            return "University Deleted Successfully !";
        }
        catch (EmptyResultDataAccessException e){
            return "University not found !";
        }
    }

}
