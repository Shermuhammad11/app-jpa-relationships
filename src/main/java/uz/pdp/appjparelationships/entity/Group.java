package uz.pdp.appjparelationships.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "groups")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "faculty_id"})})
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "name")
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "faculty_id", referencedColumnName = "id")
    private Faculty faculty;
}