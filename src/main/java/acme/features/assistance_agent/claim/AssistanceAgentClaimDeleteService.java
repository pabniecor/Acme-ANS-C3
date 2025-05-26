
package acme.features.assistance_agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.customer_service_and_claims.ClaimType;
import acme.entities.customer_service_and_claims.TrackingLog;
import acme.entities.flight_management.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimDeleteService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	protected AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		AssistanceAgent currentAgent;
		int claimId;
		Claim claim;

		if (super.getRequest().hasData("id", int.class)) {
			claimId = super.getRequest().getData("id", int.class);
			claim = this.repository.findClaimById(claimId);
			currentAgent = claim == null ? null : claim.getAssistanceAgent();
			status = claim != null && super.getRequest().getPrincipal().hasRealm(currentAgent) && claim.getDraftMode();
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);

		Claim claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		super.bindObject(claim, "passengerEmail", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Claim claim) {
		Collection<TrackingLog> trackingLogs;

		trackingLogs = this.repository.findTrackingLogsByClaimId(claim.getId());
		this.repository.deleteAll(trackingLogs);
		this.repository.delete(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		Collection<AssistanceAgent> assistanceAgents;
		Collection<Leg> legs;
		SelectChoices agentChoices;
		SelectChoices legChoices;
		SelectChoices claimTypes;

		assistanceAgents = this.repository.findAllAssistanceAgents();
		legs = this.repository.findLegsWithDepartureBeforeClaimRegistration();
		claimTypes = SelectChoices.from(ClaimType.class, claim.getType());
		agentChoices = SelectChoices.from(assistanceAgents, "employeeCode", claim.getAssistanceAgent());
		legChoices = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "accepted", "draftMode", "assistanceAgent", "leg");
		dataset.put("assistanceAgent", agentChoices.getSelected().getKey());
		dataset.put("assistanceAgents", agentChoices);

		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);

		dataset.put("types", claimTypes);

		super.getResponse().addData(dataset);
	}

}
