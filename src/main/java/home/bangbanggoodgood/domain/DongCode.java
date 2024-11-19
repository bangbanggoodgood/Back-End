package home.bangbanggoodgood.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dongcodes")
public class DongCode {

    @Id
    private String dongCode;

    private String sidoName;
    private String gugunName;
    private String dongName;

}
