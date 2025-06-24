
package acme.features.assistance_agent.tracking_log;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_service_and_claims.AcceptanceStatus;
import acme.entities.customer_service_and_claims.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogPublishService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

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

		status = trackingLog != null && super.getRequest().getPrincipal().hasRealm(currentAgent) && trackingLog.getDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		boolean confirmation;
		//		boolean claimIsPublished;
		boolean percentageIs100;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		//		claimIsPublished = !trackingLog.getClaim().getDraftMode();
		//		super.state(claimIsPublished, "claim", "acme.validation.trackingLog.claimMustBePublished.message");

		percentageIs100 = trackingLog.getResolutionPercentage() == 100.;
		super.state(percentageIs100, "resolutionPercentage", "acme.validation.trackingLog.trackingLogPercentageIs100ToPublish.message");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setDraftMode(false);
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices statusChoices;

		statusChoices = SelectChoices.from(AcceptanceStatus.class, trackingLog.getStatus());

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "status", "resolution", "draftMode", "reclaimed");
		dataset.put("trackStatus", statusChoices);
		dataset.put("reclaim", trackingLog.getReclaim());

		super.getResponse().addData(dataset);
	}
}
