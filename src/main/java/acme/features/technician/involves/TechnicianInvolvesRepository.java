
package acme.features.technician.involves;

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
public interface TechnicianInvolvesRepository extends AbstractRepository {

	@Query("select t from Technician t where t.userAccount.id = :id")
	Technician findTechnicianByUserId(int id);

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :id")
	Collection<MaintenanceRecord> findMRsByTechnicianId(int id);

	@Query("select i from Involves i where i.maintenanceRecord.id = :mrId and i.task.id = :taskId")
	Involves findInvolvesbyBothIds(int mrId, int taskId);

	@Query("select i.task from Involves i where i.maintenanceRecord.id = :id")
	List<Task> findTasksByMRId(int id);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMRById(int id);

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

	@Query("""
		    select t from Task t
		    where t.draftMode = false
		    and t not in (
		        select i.task from Involves i where i.maintenanceRecord.id = :id
		    )
		""")
	Collection<Task> findPublicTasksNotInvolvedWithMR(int id);

}
