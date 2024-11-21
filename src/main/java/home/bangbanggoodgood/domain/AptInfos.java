package home.bangbanggoodgood.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name="houseinfos")
public class AptInfos {

    @Id
    @Column(name="apt_seq")
    private String aptSeq;

    @Column(name="sgg_cd")
    private String sggCd;

    @Column(name="umd_cd")
    private String umdCd;

    @Column(name="umd_nm")
    private String umdNm;

    @Column(name="jibun")
    private String jibun;

    @Column(name="road_nm_sgg_cd")
    private String roadNmSggCd;

    @Column(name="road_nm")
    private String roadNm;

    @Column(name="road_nm_bonbun")
    private String roadNmBonbun;

    @Column(name="road_nm_bubun")
    private String roadNmBubun;

    @Column(name="apt_nm")
    private String aptNm;

    @Column(name="build_year")
    private Integer buildYear;

    @Column(name="latitude")
    private Double latitude;

    @Column(name="longitude")
    private Double longitude;

    @Column(name="comment")
    private String comment;

    @Column(name = "like_count")
    private Long count = 0L;

    @OneToMany(mappedBy = "aptInfo")
    private List<Likes> likes;

    public void updateCount(Long newCount) {
        this.count += newCount;
    }

    public void updateComment(String comment) {this.comment = comment;}
}