
package acme.entities.flight_management;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface FlightRepository extends AbstractRepository {

	@Query("select l from Leg l where l.flight.id = :flightId")
	List<Leg> computeLegsByFlight(int flightId);

}
