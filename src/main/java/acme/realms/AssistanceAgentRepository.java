
package acme.realms;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AssistanceAgentRepository extends AbstractRepository {

	@Query("select a from AssistanceAgent a where a.id = :id")
	AssistanceAgent findAssistanceAgentsById(int id);

}
