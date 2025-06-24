
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
public class AssistanceAgentTrackingLogDeleteService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	protected AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		AssistanceAgent currentAgent;
		int trackingLogId;
		TrackingLog trackingLog;

		if (super.getRequest().hasData("id", int.class)) {
			trackingLogId = super.getRequest().getData("id", int.class);
			trackingLog = this.repository.findTrackingLogById(trackingLogId);
			currentAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();
			status = trackingLog != null && super.getRequest().getPrincipal().hasRealm(currentAgent) && trackingLog.getDraftMode();
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);

		TrackingLog trackingLog = this.repository.findTrackingLogById(id);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.delete(trackingLog);
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
