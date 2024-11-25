package home.bangbanggoodgood.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "infra")
@Getter
public class Infras {

    @Id
    @Column(name = "code")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "study_facilities")
    private int studyFacilities;

    @Column(name = "animal_hospitals")
    private int animalHospital;

    @Column(name = "hospitals")
    private int hospitals;

    @Column(name = "supermarkets")
    private int supermarkets;

    @Column(name = "sports_services")
    private int sportsService;

    @Column(name = "pet_supplies")
    private int petSupply;

    @Column(name = "pharmacies")
    private int pharmacies;

    @Column(name = "entertainment_services")
    private int entertainmentServices;

    @Column(name = "restaurants")
    private int restaurants;

    @Column(name = "pubs")
    private int pubs;

    @Column(name = "cafes")
    private int cafes;

    @Column(name = "convenience_stores")
    private int convenienceStores;

    @Column(name = "academies")
    private int academies;

    @Column(name = "high_schools")
    private int highSchools;

    @Column(name = "middle_schools")
    private int middleSchools;

    @Column(name = "elementary_schools")
    private int elementarySchools;

    @Column(name = "buses")
    private int buses;

    @Column(name = "subways")
    private int subways;

    @Column(name = "cluster")
    private int cluster;
}

