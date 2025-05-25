
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline_operations.Aircraft;
import acme.entities.airport_management.Airport;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.Leg;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select l from Leg l where l.flight.id = :masterId order by l.scheduledDeparture asc")
	Collection<Leg> findLegsByMasterIdOrderedByMoment(int masterId);

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select max(l.sequenceOrder) from Leg l where l.flight.id = :flightId")
	Integer findLastSequenceOrderByFlightId(int flightId);

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select a from Airport a")
	Collection<Airport> findAllAirports();

	@Query("select a from Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("select a from Airport a where a.id = :airportId")
	Airport findAirportById(int airportId);

	@Query("select a from Aircraft a where a.id = :aircraftId")
	Aircraft findAircraftById(int aircraftId);

	@Query("select c from Claim c where c.leg.id = :legId")
	Collection<Claim> findClaimsByLegId(int legId);

	@Query("select f from FlightAssignment f where f.leg.id = :legId")
	Collection<FlightAssignment> findFlightAssignmentsByLegId(int legId);
}
