
package acme.features.technician.maintenanceRecord;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenance_and_technical.Involves;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.realms.Technician;

@Repository
public interface TechnicianMRRepository extends AbstractRepository {

	@Query("select i from Involves i")
	List<Involves> findAllInvolves();

	@Query("select mr from MaintenanceRecord mr")
	List<MaintenanceRecord> findAllMRs();

	@Query("select t from Technician t where t.userAccount.id = :id")
	Technician findTechnicianById(Integer id);

}
