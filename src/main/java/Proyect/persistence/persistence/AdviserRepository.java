package Proyect.persistence.persistence;

import Proyect.persistence.entity.AdviserEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdviserRepository extends ListCrudRepository<AdviserEntity, String> {
    Optional<AdviserEntity> findByEmail(String email);
}
