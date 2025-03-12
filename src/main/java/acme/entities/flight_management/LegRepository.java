
package acme.entities.flight_management;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LegRepository {

	@Query("select l from Leg l where l.flightNumber = :flightNumber")
	Leg findLegByFlightNumber(String flightNumber);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.sequenceOrder desc")
	List<Leg> computeLegsByFlight(int flightId);

}
