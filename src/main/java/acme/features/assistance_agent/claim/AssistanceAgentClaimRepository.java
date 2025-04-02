
package acme.features.assistance_agent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.customer_service_and_claims.TrackingLog;
import acme.entities.flight_management.Leg;
import acme.realms.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c")
	Collection<Claim> findAllClaims();

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select c from Claim c where c.accepted = true or c.accepted = false")
	Collection<Claim> findCompletedClaims();

	@Query("select a from AssistanceAgent a")
	Collection<AssistanceAgent> findAllAssistanceAgents();

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select t from TrackingLog t where t.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

}
