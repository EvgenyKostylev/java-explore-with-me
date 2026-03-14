package ru.practicum.explorewithme.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.explorewithme.model.Location;

public interface LocationRepository extends CrudRepository<Location, Integer> {
}