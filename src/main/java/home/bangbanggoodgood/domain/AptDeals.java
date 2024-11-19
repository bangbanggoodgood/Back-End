package home.bangbanggoodgood.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="housedeals")
public class AptDeals {

    @Id
    @Column(name="no")
    private Long no;

    @Column(name="apt_seq")
    private String aptSeq;

    @Column(name="apt_dong")
    private String aptDong;

    @Column(name="floor")
    private Integer floor;

    @Column(name="deal_year")
    private Integer dealYear;

    @Column(name="deal_month")
    private Integer dealMonth;

    @Column(name="deal_day")
    private Integer dealDay;

    @Column(name="exclu_use_ar")
    private BigDecimal excluUseAr;

    @Column(name="deal_amount")
    private String dealAmount;

//    @ManyToOne
//    @JoinColumn(name="apt_seq", referencedColumnName = "apt_seq")
//    private AptInfos aptInfos;

}
