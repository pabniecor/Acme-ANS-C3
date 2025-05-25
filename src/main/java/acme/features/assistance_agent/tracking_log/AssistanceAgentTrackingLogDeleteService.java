
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
public class AssistanceAgentTrackingLogDeleteService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	protected AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		AssistanceAgent currentAgent;
		int trackingLogId;
		TrackingLog trackingLog;
		int userAccountId;
		Collection<Claim> agentXClaims;

		if (super.getRequest().hasData("id", int.class) && super.getRequest().getMethod().equals("POST")) {
			trackingLogId = super.getRequest().getData("id", int.class);
			trackingLog = this.repository.findTrackingLogById(trackingLogId);
			userAccountId = super.getRequest().getPrincipal().getAccountId();
			currentAgent = this.repository.findAssistanceAgentByUserAccountId(userAccountId);
			agentXClaims = this.repository.findAllClaimsByCurrentUser(currentAgent.getId());
			status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && agentXClaims.contains(trackingLog.getClaim()) && trackingLog != null && trackingLog.getDraftMode();
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
		boolean draftMode = trackingLog.getDraftMode();

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		if (draftMode == false)
			super.state(draftMode, "draftMode", "acme.validation.draftMode-delete.message");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.delete(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		assert trackingLog != null;
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
