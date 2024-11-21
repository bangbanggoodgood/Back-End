package home.bangbanggoodgood.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dongcodes")
public class DongCode {

    @Id
    @Column(name = "dong_code")
    private String dongCode;

    @Column(name = "sido_name")
    private String sidoName;

    @Column(name = "gugun_name")
    private String gugunName;

    @Column(name = "dong_name")
    private String dongName;

}
