package home.bangbanggoodgood.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hashtag")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="hashtag")
    private String hashtag;

    @ManyToOne
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;

}
