
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline_operations.Aircraft;
import acme.entities.maintenance_and_technical.Involves;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.entities.maintenance_and_technical.Task;
import acme.realms.Technician;

@Repository
public interface TechnicianMRRepository extends AbstractRepository {

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :id")
	List<MaintenanceRecord> findMRsByTechnicianId(int id);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMRById(int id);

	@Query("select t from Technician t where t.userAccount.id = :id")
	Technician findTechnicianByUserId(int id);

	@Query("select i from Involves i where i.maintenanceRecord.id = :id")
	Collection<Involves> findInvolvesByMRId(int id);

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

	@Query("select a from Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("select a from Aircraft a where a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("select t from Technician t")
	List<Technician> findAllTechnicians();

	@Query("select mr from MaintenanceRecord mr where mr.draftMode = false")
	List<MaintenanceRecord> findAllPublishedMRs();

}
