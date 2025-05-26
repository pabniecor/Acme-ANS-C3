
package acme.features.assistance_agent.tracking_log;

import java.util.Collection;

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
		int trackingLogId;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);

		currentAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();

		status = trackingLog != null && super.getRequest().getPrincipal().hasRealm(currentAgent) && trackingLog.getReclaim() == true && !trackingLog.getDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		TrackingLog lastTrackingLog;
		Claim claimAssociated = new Claim();
		AcceptanceStatus statusAssociated;
		int id = super.getRequest().getData("masterId", int.class);

		lastTrackingLog = this.repository.findTrackingLogById(id);
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
		Collection<Claim> claims;
		SelectChoices statusChoices;
		SelectChoices claimChoices;

		claims = this.repository.findAllClaims();
		statusChoices = SelectChoices.from(AcceptanceStatus.class, trackingLog.getStatus());
		claimChoices = SelectChoices.from(claims, "id", trackingLog.getClaim());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "reclaimed", "claim");
		dataset.put("claim", claimChoices.getSelected().getKey());
		dataset.put("claims", claimChoices);
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("trackStatus", statusChoices);
		dataset.put("reclaim", trackingLog.getReclaim());

		super.getResponse().addData(dataset);
	}
}
