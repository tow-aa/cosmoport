
package com.space.model;
import org.hibernate.annotations.Proxy;
import javax.persistence.*;
import java.sql.Date;
import java.util.Calendar;


@Entity
@Table(name = "ship")
@Proxy(lazy = false)
public class Ship {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "planet")
    private String planet;
    @Column(name = "shipType")
    @Enumerated(EnumType.STRING)
    private ShipType shipType;
    @Column(name = "prodDate")
    private Date prodDate;
    @Column(name = "isUsed")
    private Boolean isUsed;
    @Column(name = "speed")
    private Double speed;
    @Column(name = "crewSize")
    private Integer crewSize;
    @Column(name = "rating")
    private Double rating;

    public ShipType getShipType() {
        return shipType;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public Date getProdDate() {
        return prodDate;
    }

    public void setProdDate(Date prodDate) {
        this.prodDate = prodDate;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public void setCrewSize(Integer crewSize) {
        this.crewSize = crewSize;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating() {
        double k = this.getUsed() ? 0.5 : 1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.getProdDate().getTime());
        int year = calendar.get(Calendar.YEAR);
        double temp = 3019 - year + 1;
        double temp2 = 80 * this.getSpeed() * k;
        double rating = Math.round(temp2 / temp * 100.0) / 100.0;
        this.rating = rating;
    }

    public Ship(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }



    public Ship() {
    }

}


