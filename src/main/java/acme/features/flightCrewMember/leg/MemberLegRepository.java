
package acme.features.flightCrewMember.leg;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.flight_management.Leg;

@Repository
public interface MemberLegRepository extends AbstractRepository {

	@Query("select fa.leg from FlightAssignment fa where fa.id =:id")
	Collection<Leg> findAllLegsOfFlightAssingment(int id);

	@Query("select l from Leg l where l.id =:id")
	Leg findLegById(int id);

	@Query("select fa from FlightAssignment fa where fa.id =:id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select fa.leg from FlightAssignment fa where fa.flightCrew.id =:id")
	Leg findLegByMemberId(int id);
}
