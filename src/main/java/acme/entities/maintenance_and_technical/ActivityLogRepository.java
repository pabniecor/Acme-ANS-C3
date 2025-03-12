
package acme.entities.maintenance_and_technical;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface ActivityLogRepository extends AbstractRepository {

	@Query("select a from ActivityLog a where a.flightAssignment.id = :flightAssignmentId order by a.registrationMoment desc")
	List<ActivityLog> findActivityLogtByFlightAssignmentId(int flightAssignmentId);
}
