package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.University;
import uz.pdp.appjparelationships.payload.UniversityDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.UniversityRepository;

import java.util.List;
import java.util.Optional;

@RestController
public class UniversityController {
    @Autowired
    UniversityRepository universityRepository;
    @Autowired
    AddressRepository addressRepository;


    //READ
    @RequestMapping(value = "/university", method = RequestMethod.GET)
    public List<University> getUniversities() {
        List<University> universityList = universityRepository.findAll();
        return universityList;
    }


    //CREATE
    @RequestMapping(value = "/university", method = RequestMethod.POST)
    public String addUniversity(@RequestBody UniversityDto universityDto) {

        String name = universityDto.getName();

        if (name == null)
            return "University Name is Empty !";

        //YANGI ADDRES OCHIB OLDIK
        Address address = new Address();
        address.setCity(universityDto.getCity());
        address.setDistrict(universityDto.getDistrict());
        address.setStreet(universityDto.getStreet());

        //YASAB OLGAN ADDRESS OBJECTIMIZNI DB GA SAQLAMOCHI BO'LDIK
        // VA BAZADA BUNDAY ADDRESS BOR YO'QLIGINI TEKSHIRDIK
        // ADDRESS BAZADA MAVJUD BO'LMASA BAZAGA QO'SHIB QO'YDIK
        Address savedAddress;

        try {
            savedAddress = addressRepository.save(address);
        }
        catch (Exception e){
            return "This address already exist !";
        }

        //YANGI UNIVERSITET YASAB OLDIK
        University university = new University();
        university.setName(name);
        university.setAddress(savedAddress);
        universityRepository.save(university);

        return "University added";
    }

    //UPDATE
    @RequestMapping(value = "/university/{id}", method = RequestMethod.PUT)
    public String editUniversity(@PathVariable Integer id, @RequestBody UniversityDto universityDto) {
        Optional<University> optionalUniversity = universityRepository.findById(id);
        if (optionalUniversity.isPresent()) {

            University university = optionalUniversity.get();

            String name = universityDto.getName();

            boolean b = false;

            if (name != null && !name.equals(university.getName())){
                university.setName(name);
                universityRepository.save(university);
                b = true;
            }



            //universitet addressi
            Address address = university.getAddress();
            String x = universityDto.getCity();
            if (x != null)
                address.setCity(x);
            x = universityDto.getDistrict();
            if(x != null)
                address.setDistrict(x);
            x = universityDto.getStreet();
            if(x != null)
                address.setStreet(x);

            try {
                if (!address.equals(university.getAddress())) {
                    addressRepository.save(address);
                    return "University edited";
                }
                if (b)
                    return "University edited";
                return "Nothing Changed !";
            }
            catch (Exception e){
                return "This Address Already Exist !";
            }

        }

        return "University not found";
    }


    //DELETE
    @RequestMapping(value = "/university/{id}",method = RequestMethod.DELETE)
    public String deleteUniversity(@PathVariable Integer id){
        universityRepository.deleteById(id);
        return "University deleted";
    }
}
