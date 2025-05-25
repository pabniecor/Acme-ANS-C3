
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.customer_management.Booking;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.Leg;

@Repository
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select f from Flight f where f.manager.id = :managerId")
	Collection<Flight> findFlightsByManagerId(int managerId);

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(int flightId);

	@Query("select b from Booking b where b.flight.id = :flightId")
	Collection<Booking> findBookingsByFlightId(int flightId);

	@Query("select c from Claim c where c.leg.id = :legId")
	Collection<Claim> findClaimsByLegId(int legId);

	@Query("select f from FlightAssignment f where f.leg.id = :legId")
	Collection<FlightAssignment> findFlightAssignmentsByLegId(int legId);
}
