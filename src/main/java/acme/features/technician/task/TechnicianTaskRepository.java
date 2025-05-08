
package acme.features.technician.task;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenance_and_technical.Involves;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.entities.maintenance_and_technical.Task;
import acme.realms.Technician;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("select i from Involves i where i.maintenanceRecord.id = :id")
	Collection<Involves> findInvolvesByMRId(int id);

	@Query("select i from Involves i where i.task.id = :id")
	Collection<Involves> findInvolvesByTaskId(int id);

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

	@Query("select t from Technician t where t.userAccount.id = :id")
	Technician findTechnicianByUserId(int id);

	@Query("select t from Task t where t.technician.id = :id")
	Collection<Task> findTasksByTechnicianId(int id);

	@Query("select t from Technician t")
	List<Technician> findAllTechnicians();

	@Query("select t from Task t where t.draftMode = false")
	List<Task> findPublicTasks();

	@Query("select t from Technician t where t.id = :id")
	Technician findTechnicianById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMRById(int id);

}
