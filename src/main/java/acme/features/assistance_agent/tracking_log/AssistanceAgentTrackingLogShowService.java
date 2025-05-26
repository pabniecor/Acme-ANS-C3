
package acme.features.assistance_agent.tracking_log;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_service_and_claims.AcceptanceStatus;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.customer_service_and_claims.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogShowService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		int trackingLogId;
		TrackingLog trackingLog;
		AssistanceAgent currentAgent;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);
		currentAgent = trackingLog == null ? null : trackingLog.getClaim().getAssistanceAgent();

		boolean authorised = trackingLog != null && (super.getRequest().getPrincipal().hasRealm(currentAgent) || !trackingLog.getDraftMode());

		super.getResponse().setAuthorised(authorised);
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
		dataset.put("trackStatus", statusChoices);
		dataset.put("reclaim", trackingLog.getReclaim());

		super.getResponse().addData(dataset);
	}
}
