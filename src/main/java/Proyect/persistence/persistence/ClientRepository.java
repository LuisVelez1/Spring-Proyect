package Proyect.persistence.persistence;

import Proyect.persistence.entity.ClientEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends ListCrudRepository<ClientEntity, String> {
    Optional<ClientEntity> findByEmail(String email);
}

