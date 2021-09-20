package uz.pdp.appjparelationships.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"city", "district", "street"})})

public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//1

    @Column(nullable = false, name = "city")
    private String city;//Toshkent

    @Column(nullable = false, name = "district")
    private String district;//Mirobod

    @Column(nullable = false, name = "street")
    private String street;//U.Nosir ko'chasi
}