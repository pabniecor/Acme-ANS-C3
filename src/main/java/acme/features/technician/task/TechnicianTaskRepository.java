
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenance_and_technical.Involves;
import acme.entities.maintenance_and_technical.Task;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("select i from Involves i where i.maintenanceRecord.id = :id")
	Collection<Involves> findInvolvesByMRId(int id);

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

}
