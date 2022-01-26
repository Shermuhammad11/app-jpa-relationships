package uz.pdp.appjparelationships.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UniversityDto {
    private String name;
    private String city;
    private String district;
    private String street;
}
