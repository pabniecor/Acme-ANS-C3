
package acme.features.flightCrewMember.flightAssignment;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airport_management.Duty;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.flight_management.Leg;
import acme.entities.maintenance_and_technical.ActivityLog;
import acme.realms.FlightCrewMember;

@Repository
public interface MemberFlightAssignmentRepository extends AbstractRepository {

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival <:date and fa.flightCrew.id =:id")
	Collection<FlightAssignment> findAllFlightAssignmentCompleted(Timestamp date, int id);

	@Query("select fa from FlightAssignment fa where fa.flightCrew.id =:id")
	Collection<FlightAssignment> findAllFlightAssignmentByFlightCrewMemberId(int id);

	@Query("select fa from FlightAssignment fa where fa.id =:id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival >:date and fa.flightCrew.id =:id")
	Collection<FlightAssignment> findAllFlightAssignmentIncompleted(Timestamp date, int id);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select l from Leg l where l.draftMode = false")
	Collection<Leg> findAllLegsPublished();

	@Query("select l from Leg l where l.id =:id")
	Leg findLegById(int id);

	@Query("select fcm from FlightCrewMember fcm where fcm.id =:id")
	FlightCrewMember findFlightCrewMemberById(int id);

	@Query("select fa.leg from FlightAssignment fa where fa.leg.scheduledArrival >:date and fa.flightCrew.id =:id")
	Collection<Leg> findLegsByFlightCrewMemberId(Timestamp date, int id);

	@Query("select m from FlightCrewMember m")
	Collection<FlightCrewMember> findAllMembers();

	@Query("select fa from FlightAssignment fa where fa.leg.id =:id and fa.duty =:duty and fa.draft = false")
	Collection<FlightAssignment> findAssignmentByLegIdAndDuty(int id, Optional<Duty> duty);

	@Query("select al from ActivityLog al where al.flightAssignment.id =:id")
	Collection<ActivityLog> findAllActivityLogByAssignmentId(int id);

	@Query("select l from Leg l where l.aircraft.airline.id =:airlineId and l.draftMode = false")
	Collection<Leg> findLegsByAirline(int airlineId);
}
