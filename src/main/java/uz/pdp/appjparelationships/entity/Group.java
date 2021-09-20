package uz.pdp.appjparelationships.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "groups")
@Table(uniqueConstraints =
        {@UniqueConstraint(
                columnNames = {"name", "faculty_id"}
                        )
        })

public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "name")
    private String name;

    @ManyToOne//MANY group TO ONE faculty
    @JoinColumn(name = "faculty_id", referencedColumnName = "id")
    private Faculty faculty;
    //
//    @OneToMany//ONE group TO MANY students
//    private List<Student> students;
}