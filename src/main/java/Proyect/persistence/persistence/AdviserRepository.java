package Proyect.persistence.persistence;

import Proyect.persistence.entity.AdviserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdviserRepository extends JpaRepository<AdviserEntity, String> {
    Optional<AdviserEntity> findByEmail(String email);
}
