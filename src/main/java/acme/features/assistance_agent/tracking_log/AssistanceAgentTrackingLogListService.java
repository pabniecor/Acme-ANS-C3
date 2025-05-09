
package acme.features.assistance_agent.tracking_log;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.customer_service_and_claims.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		//		Collection<TrackingLog> trackingLogs;
		//		TrackingLog anyTrackingLog;
		AssistanceAgent currentAgent;
		int claimId = super.getRequest().getData("masterId", int.class);
		Claim claim = this.repository.findClaimById(claimId);

		//		trackingLogs = this.repository.findAllTrackingLogsByClaimId(claimId);
		//		anyTrackingLog = trackingLogs.stream().findFirst().get();

		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		currentAgent = this.repository.findAssistanceAgentByUserAccountId(userAccountId);
		Collection<Claim> agentXClaims = this.repository.findAllCompletedClaimsByCurrentUser(currentAgent.getId());

		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && agentXClaims.contains(claim);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<TrackingLog> trackingLogs;

		int claimId = super.getRequest().getData("masterId", int.class);

		trackingLogs = this.repository.findAllTrackingLogsByClaimId(claimId);

		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status");

		super.addPayload(dataset, trackingLog, "resolution", "draftMode", "claim.passengerEmail");
		super.getResponse().addData(dataset);
	}
}
