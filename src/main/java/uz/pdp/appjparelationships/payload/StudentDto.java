package uz.pdp.appjparelationships.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StudentDto {
    private String firstName;
    private String lastName;
    private String city;
    private String district;
    private String street;
    private Integer groupId;
    private Set<String> subjectNames;
}
