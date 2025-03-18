
package acme.entities.flight_management;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface FlightRepository extends AbstractRepository {

	@Query("select min(l.scheduledDeparture) from Leg l where l.flight.id = :flightId")
	Date computeDeparture(int flightId);

	@Query("select max(l.scheduledArrival) from Leg l where l.flight.id = :flightId")
	Date computeArrival(int flightId);

	//	@Query("select l.departureAirport.city from Leg l where l.flight.id = :flightId and l.scheduledDeparture = min(l.scheduledDeparture)")
	//	String computeOriginCity(int flightId);
	//
	//	@Query("select l.arrivalAirport.city from Leg l where l.flight.id = :flightId and l.scheduledArrival = max(l.scheduledArrival)")
	//	String computeDestinationCity(int flightId);

	@Query("select count(l) from Leg l where l.flight.id = :flightId")
	Integer computeNumLayovers(int flightId);

	@Query("select l.departureAirport.city from Leg l where l.flight.id = :flightId and l.scheduledDeparture = :departure")
	String computeOriginCity(int flightId, Date departure);

	@Query("select l.arrivalAirport.city from Leg l where l.flight.id = :flightId and l.scheduledArrival = :arrival")
	String computeDestinationCity(int flightId, Date arrival);

}
