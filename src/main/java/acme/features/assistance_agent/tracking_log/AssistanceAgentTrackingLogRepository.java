
package acme.features.assistance_agent.tracking_log;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.customer_service_and_claims.TrackingLog;
import acme.realms.AssistanceAgent;

@Repository
public interface AssistanceAgentTrackingLogRepository extends AbstractRepository {

	@Query("select tl from TrackingLog tl")
	Collection<TrackingLog> findAllTrackingLogs();

	@Query("select tl from TrackingLog tl where tl.id = :id")
	TrackingLog findTrackingLogById(int id);

	@Query("select tl from TrackingLog tl where tl.claim.id = :claimId")
	Collection<TrackingLog> findAllTrackingLogsByClaimId(int claimId);

	@Query("select a from AssistanceAgent a where a.userAccount.id = :id")
	AssistanceAgent findAssistanceAgentByUserAccountId(int id);

	@Query("select c from Claim c")
	Collection<Claim> findAllClaims();

	@Query("select c from Claim c where c.assistanceAgent.id = :agentId")
	Collection<Claim> findAllClaimsByCurrentUser(int agentId);

	@Query("select c from Claim c where c.id = :claimId")
	Claim findClaimById(int claimId);

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :claimId ORDER BY tl.resolutionPercentage DESC")
	List<TrackingLog> findTrackingLogsByResolutionPercentageOrder(int claimId);

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :claimId ORDER BY tl.creationMoment ASC")
	List<TrackingLog> findTrackingLogsBycreationMomentOrderAsc(int claimId);
}
