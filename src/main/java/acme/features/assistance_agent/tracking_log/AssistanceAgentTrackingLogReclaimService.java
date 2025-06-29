
package acme.features.assistance_agent.tracking_log;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_service_and_claims.AcceptanceStatus;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.customer_service_and_claims.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogReclaimService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		AssistanceAgent currentAgent;
		int claimId;
		List<TrackingLog> trackingLogs;
		TrackingLog lastTrackingLog;

		claimId = super.getRequest().getData("masterId", int.class);
		trackingLogs = this.repository.findTrackingLogsByResolutionPercentageOrder(claimId);
		lastTrackingLog = !trackingLogs.isEmpty() ? trackingLogs.get(0) : null;

		currentAgent = lastTrackingLog == null ? null : lastTrackingLog.getClaim().getAssistanceAgent();

		status = lastTrackingLog != null && super.getRequest().getPrincipal().hasRealm(currentAgent) && lastTrackingLog.getReclaim() == true && !lastTrackingLog.getDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int claimId;
		List<TrackingLog> trackingLogs;
		TrackingLog lastTrackingLog;
		Claim claimAssociated;
		AcceptanceStatus statusAssociated;
		TrackingLog trackingLog;

		claimId = super.getRequest().getData("masterId", int.class);
		trackingLogs = this.repository.findTrackingLogsByResolutionPercentageOrder(claimId);
		lastTrackingLog = trackingLogs.get(0);

		claimAssociated = lastTrackingLog.getClaim();
		statusAssociated = lastTrackingLog.getStatus();

		trackingLog = new TrackingLog();
		trackingLog.setCreationMoment(MomentHelper.getCurrentMoment());
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setDraftMode(true);
		trackingLog.setStatus(statusAssociated);
		trackingLog.setResolutionPercentage(100.);
		trackingLog.setClaim(claimAssociated);
		trackingLog.setReclaimed(true);
		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setDraftMode(true);
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setReclaimed(true);
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices statusChoices;

		statusChoices = SelectChoices.from(AcceptanceStatus.class, trackingLog.getStatus());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "reclaimed", "claim");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("trackStatus", statusChoices);
		dataset.put("reclaim", trackingLog.getReclaim());

		super.getResponse().addData(dataset);
	}
}
