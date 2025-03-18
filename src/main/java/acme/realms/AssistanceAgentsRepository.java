
package acme.realms;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AssistanceAgentsRepository extends AbstractRepository {

	@Query("select a from AssistanceAgents a where a.id = :id")
	AssistanceAgents findAssistanceAgentsById(int id);

}
