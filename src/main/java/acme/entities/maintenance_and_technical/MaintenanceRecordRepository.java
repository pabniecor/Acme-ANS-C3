
package acme.entities.maintenance_and_technical;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface MaintenanceRecordRepository extends AbstractRepository {

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMRById(Integer id);

}
