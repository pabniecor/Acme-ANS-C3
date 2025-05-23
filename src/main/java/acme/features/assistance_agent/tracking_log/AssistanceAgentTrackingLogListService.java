
package acme.features.assistance_agent.tracking_log;

import java.util.ArrayList;
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
		AssistanceAgent currentAgent;
		int claimId = super.getRequest().getData("masterId", int.class);
		Claim claim = this.repository.findClaimById(claimId);

		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		currentAgent = this.repository.findAssistanceAgentByUserAccountId(userAccountId);
		Collection<Claim> agentXClaims = this.repository.findAllClaimsByCurrentUser(currentAgent.getId());

		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && agentXClaims.contains(claim);

		super.getResponse().setAuthorised(status);
	}

	// probar si no puedo con el parametro claimId poniendo como parametro la trackingLog id y haciendo un filtro para saber si la id pertenece a claim o trackingLog
	@Override
	public void load() {
		//		List<Integer> trackingLogIds;
		//		List<Integer> claimIds;
		Collection<TrackingLog> trackingLogs = new ArrayList<>();
		int claimId;

		//		id = super.getRequest().getData("masterId", int.class);

		//		if (super.getRequest().getData("masterId", int.class) != null)
		//			claimId = super.getRequest().getData("masterId", int.class);
		//		else
		//			claimId = super.getRequest().getData("claimId", int.class);

		//		trackingLogIds = this.repository.findAllTrackingLogs().stream().map(tl -> tl.getId()).toList();
		//
		//		claimIds = this.repository.findAllClaims().stream().map(c -> c.getId()).toList();
		//
		//		if (claimIds.contains(id))
		//			trackingLogs = this.repository.findAllTrackingLogsByClaimId(id);
		//		else if (trackingLogIds.contains(id)) {
		//			TrackingLog tl = this.repository.findTrackingLogById(id);
		//			int claimId = tl.getClaim().getId();
		//			trackingLogs = this.repository.findAllTrackingLogsByClaimId(claimId);
		//		}

		claimId = super.getRequest().getData("masterId", int.class);

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
