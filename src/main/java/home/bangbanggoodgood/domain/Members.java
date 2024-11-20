package home.bangbanggoodgood.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "members")
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="memberId")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="sex")
    private String sex;

    @Column(name="birth")
    private String birth;

    @Column(name="job")
    private String job;

    @Column(name="is_survey")
    private Boolean isSurvey;

    @OneToMany(mappedBy = "member")
    private List<Likes> likes;
}
