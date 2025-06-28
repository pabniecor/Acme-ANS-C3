
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
		AssistanceAgent currentAgent;
		int claimId;
		Claim claim;

		claimId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(claimId);
		currentAgent = claim == null ? null : claim.getAssistanceAgent();

		status = claim != null && super.getRequest().getPrincipal().hasRealm(currentAgent) && !claim.getDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<TrackingLog> trackingLogs;
		int claimId;

		claimId = super.getRequest().getData("masterId", int.class);

		trackingLogs = this.repository.findAllTrackingLogsByClaimId(claimId);

		super.getBuffer().addData(trackingLogs);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status");

		super.addPayload(dataset, trackingLog, "resolution", "draftMode", "claim.passengerEmail", "reclaimed");
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrackingLog> trackingLog) {
		int masterId;
		Collection<TrackingLog> trackingLogs;
		boolean existsTl100Percentage;
		boolean previosTlPublishedToCreate;
		boolean couldReclaim;
		boolean couldCreate;

		masterId = super.getRequest().getData("masterId", int.class);

		trackingLogs = this.repository.findAllTrackingLogsByClaimId(masterId);
		existsTl100Percentage = trackingLogs.stream().anyMatch(tl -> tl.getResolutionPercentage() == 100.) ? false : true; // Al revés ya que busco que no haya uno al 100% para crear
		previosTlPublishedToCreate = trackingLogs.stream().allMatch(tl -> tl.getDraftMode() == false);

		couldCreate = existsTl100Percentage && previosTlPublishedToCreate;
		couldReclaim = trackingLogs.stream().anyMatch(tl -> tl.getReclaim());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("couldCreate", couldCreate);
		super.getResponse().addGlobal("couldReclaim", couldReclaim);
	}
}
