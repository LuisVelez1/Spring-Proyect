package Proyect.persistence.persistence;

import Proyect.persistence.entity.AdministratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdministratorEntity, String> {
    Optional<AdministratorEntity> findByEmail(String email);
}
