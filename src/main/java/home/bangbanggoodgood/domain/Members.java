package home.bangbanggoodgood.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "members")
@Setter @Getter
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    @Column(name = "social_id")
    String socialId;

    @Column(name="name")
    @Nullable
    private String name;

    @Column(name="sex")
    @Nullable
    private String sex;

    @Column(name="birth")
    @Nullable
    private String birth;

    @Column(name="job")
    @Nullable
    private String job;

    @Enumerated(EnumType.STRING)
    @Column(name="authority")
    private Authority authority;

    @Column(name="is_survey")
    @Nullable
    private Boolean isSurvey;

    @OneToMany(mappedBy = "member")
    private List<Likes> likes;
}
