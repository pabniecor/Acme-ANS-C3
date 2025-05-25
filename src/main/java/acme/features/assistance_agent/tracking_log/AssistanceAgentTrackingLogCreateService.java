
package acme.features.assistance_agent.tracking_log;

import java.util.Collection;
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
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		AssistanceAgent currentAgent;
		int claimId;
		Claim claim;
		int userAccountId;
		Collection<Claim> agentXClaims;

		claimId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(claimId);
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		currentAgent = this.repository.findAssistanceAgentByUserAccountId(userAccountId);
		agentXClaims = this.repository.findAllClaimsByCurrentUser(currentAgent.getId());
		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && agentXClaims.contains(claim);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int claimId = super.getRequest().getData("masterId", int.class);
		Claim claim = this.repository.findClaimById(claimId);

		trackingLog = new TrackingLog();
		trackingLog.setDraftMode(true);
		trackingLog.setCreationMoment(MomentHelper.getCurrentMoment());
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setClaim(claim);
		trackingLog.setReclaimed(false);
		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		boolean confirmation;
		boolean resolutionPercentageMonotony;

		int claimId = trackingLog.getClaim().getId();

		List<TrackingLog> trackingLogsOrderedByPercentage = this.repository.getTrackingLogsByResolutionPercentageOrder(claimId);

		if (trackingLogsOrderedByPercentage != null && !trackingLogsOrderedByPercentage.isEmpty()) {
			TrackingLog trackingLogHighestPercentage = trackingLogsOrderedByPercentage.get(0);

			resolutionPercentageMonotony = trackingLog.getResolutionPercentage() > trackingLogHighestPercentage.getResolutionPercentage();
			super.state(resolutionPercentageMonotony, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage-monotony.message");
		}

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setDraftMode(true);
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setReclaimed(false);
		this.repository.save(trackingLog);
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
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("trackStatus", statusChoices);
		dataset.put("reclaim", trackingLog.getReclaim());

		super.getResponse().addData(dataset);
	}
}
