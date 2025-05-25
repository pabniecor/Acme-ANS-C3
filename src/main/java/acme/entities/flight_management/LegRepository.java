
package acme.entities.flight_management;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.flightNumber = :flightNumber")
	Leg findLegByFlightNumber(String flightNumber);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.scheduledDeparture ASC")
	List<Leg> findLegsOrderByAscendent(Integer flightId);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.scheduledDeparture DESC")
	List<Leg> findLegsOrderByDescendent(Integer flightId);

	@Query("select count(l) from Leg l where l.flight.id = :flightId")
	Integer countNumberOfLegsOfFlight(Integer flightId);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.scheduledDeparture")
	List<Leg> findLegsByFlight(Integer flightId);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.sequenceOrder ASC")
	List<Leg> findLegsOrderByAscendentUsingSequenceOrder(Integer flightId);

	@Query("select max(l.sequenceOrder) from Leg l where l.flight.id = :flightId")
	Integer findLastSequenceOrderByFlightId(int flightId);

	@Query("select l from Leg l where l.aircraft.id = :aircraftId and l.id != :legId and l.draftMode = false")
	Collection<Leg> findLegsByAircraftId(int aircraftId, int legId);

}
