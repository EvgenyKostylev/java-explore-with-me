package ru.practicum.explorewithme.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events",
        indexes = {
                @Index(name = "idx_events_category", columnList = "category_id"),
                @Index(name = "idx_events_location", columnList = "location_id")
        })
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"category", "initiator", "location", "compilations"})
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;

    @Column
    private LocalDateTime createdOn;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @ToString.Exclude
    private User initiator;

    @Column
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @ToString.Exclude
    private Location location;

    @Column
    private Boolean paid;

    @Column
    private Integer participantLimit;

    @Column
    private LocalDateTime publishedOn;

    @Column
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column
    private State state;

    @Column
    private String title;

    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
    private Set<Compilation> compilations = new HashSet<>();
}