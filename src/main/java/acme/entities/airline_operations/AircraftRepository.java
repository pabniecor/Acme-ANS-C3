
package acme.entities.airline_operations;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AircraftRepository extends AbstractRepository {

	@Query("select a from Aircraft a where a.registrationNumber =:reg")
	Aircraft findAircraftByRegNum(String reg);
}
