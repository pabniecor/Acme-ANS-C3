
package acme.entities.maintenance_and_technical;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface ActivityLogRepository extends AbstractRepository {

	@Query("select al from ActivityLog al where al.flightAssignment.id = :flightAssignmentId")
	ActivityLog findActivityLogtByFlightAssignmentId(int flightAssignmentId);
}
