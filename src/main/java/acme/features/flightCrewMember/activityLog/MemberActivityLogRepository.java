
package acme.features.flightCrewMember.activityLog;

import java.sql.Timestamp;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.maintenance_and_technical.ActivityLog;
import acme.realms.FlightCrewMember;

@Repository
public interface MemberActivityLogRepository extends AbstractRepository {

	@Query("select al from ActivityLog al")
	Collection<ActivityLog> findAllActivityLogs();

	@Query("select al from ActivityLog al where al.id =:id")
	ActivityLog findActivityLogById(int id);

	@Query("select al from ActivityLog al where al.flightAssignment.id =:id and :date >= al.registrationMoment")
	Collection<ActivityLog> findActivityLogsByFlightAssignmentId(int id, Timestamp date);

	@Query("select fa from FlightAssignment fa")
	Collection<FlightAssignment> findAllFlightAssignments();

	@Query("select fcm from FlightCrewMember fcm where fcm.id =:id")
	FlightCrewMember findFlightCrewMemberById(int id);

	@Query("select fa from FlightAssignment fa where fa.id =:id")
	FlightAssignment findFlightAssignmentById(int id);
}
