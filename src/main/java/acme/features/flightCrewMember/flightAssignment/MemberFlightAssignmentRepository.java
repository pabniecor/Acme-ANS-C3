
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

	@Query("select l from Leg l where l.draftMode = false and l.scheduledArrival >:date")
	Collection<Leg> findAllLegsPublished(Timestamp date);

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

	@Query("select l from Leg l where l.aircraft.airline.id =:airlineId and l.draftMode = false and l.scheduledArrival >:date")
	Collection<Leg> findLegsByAirline(int airlineId, Timestamp date);

	@Query("select fa.leg from FlightAssignment fa where fa.flightCrew.id =:id and fa.draft = false")
	Collection<Leg> findAllFlightAssignmentByFlightCrewMemberIdPublished(int id);

	@Query("select l from Leg l where l.aircraft.airline.id = :airlineId and l.draftMode = false and l.scheduledArrival > :date and not exists (select 1 from FlightAssignment fa where fa.leg = l and fa.flightCrew.id = :crewId and fa.draft = false)")
	Collection<Leg> findLegsByAirlineAndCrew(int airlineId, Timestamp date, int crewId);

	@Query("select l from Leg l where l.aircraft.airline.id = :airlineId and l.draftMode = false and l.scheduledArrival >= :date and not exists (select 1 from FlightAssignment fa where fa.flightCrew.id = :crewId and fa.draft = false and ((l.scheduledDeparture < fa.leg.scheduledArrival) and (l.scheduledArrival > fa.leg.scheduledDeparture)))")
	Collection<Leg> findLegsWithoutOverlapNoCurrent(int airlineId, Timestamp date, int crewId);

	@Query("select l from Leg l where l.aircraft.airline.id = :airlineId and l.draftMode = false and ((:currentLegId is not null and l.id = :currentLegId) or (l.scheduledArrival >= :date and not exists (select 1 from FlightAssignment fa where fa.flightCrew.id = :crewId and fa.draft = false and l.scheduledDeparture < fa.leg.scheduledArrival and l.scheduledArrival > fa.leg.scheduledDeparture)))")
	Collection<Leg> findLegsWithoutOverlap(int airlineId, Timestamp date, int crewId, int currentLegId);

	@Query("select fa from FlightAssignment fa where fa.flightCrew.id =:id and fa.draft = false")
	Collection<FlightAssignment> findAllFlightAssignmentByFMCPUBLISHED(int id);

}
