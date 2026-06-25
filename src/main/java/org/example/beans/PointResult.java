package org.example.beans;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Setter
@Getter
@Entity
@Table(name = "point_results")
public class PointResult implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double x;
    private Double y;
    private Double r;
    private Boolean hit; // попадание

    private LocalDateTime  serverTime;

    private Double processingTime; // в миллисекундах

    // Конструктор по умолчанию (обязателен для JPA)
    public PointResult() {}

    // Конструктор со всеми полями
    public PointResult(Double x, Double y, Double r, Boolean hit, LocalDateTime serverTime, Double processingTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.serverTime = serverTime;
        this.processingTime = processingTime;
    }

}