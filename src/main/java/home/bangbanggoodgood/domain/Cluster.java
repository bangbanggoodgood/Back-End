package home.bangbanggoodgood.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "cluster")
public class Cluster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "cluster", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<HashTag> hashtags;
}
