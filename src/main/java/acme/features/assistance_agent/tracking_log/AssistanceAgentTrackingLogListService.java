
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

		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		AssistanceAgent currentAgent;
		Collection<Claim> claimsRelatedToCurrentAgent;
		Collection<Integer> claimsId;
		Collection<TrackingLog> trackingLogs;

		currentAgent = this.repository.findAssistanceAgentByUserAccountId(userAccountId);

		claimsRelatedToCurrentAgent = this.repository.findAllClaimsByCurrentUser(currentAgent.getId());

		claimsId = claimsRelatedToCurrentAgent.stream().map(c -> c.getId()).toList();

		trackingLogs = this.repository.findAllTrackingLogs().stream().filter(t -> claimsId.contains(t.getClaim().getId())).toList();

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
