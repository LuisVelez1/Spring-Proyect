package Proyect.persistence.persistence;

import Proyect.persistence.entity.AdministratorEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends ListCrudRepository<AdministratorEntity, String> {
    Optional<AdministratorEntity> findByEmail(String email);
}
