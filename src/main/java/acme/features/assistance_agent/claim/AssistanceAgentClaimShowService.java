
package acme.features.assistance_agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.customer_service_and_claims.ClaimType;
import acme.entities.flight_management.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimShowService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Claim claim = this.repository.findClaimById(id);
		AssistanceAgent currentAgent;

		currentAgent = claim == null ? null : claim.getAssistanceAgent();

		boolean authorised = claim != null && super.getRequest().getPrincipal().hasRealm(currentAgent);

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Claim claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		Collection<Leg> legs;
		SelectChoices legChoices;
		SelectChoices claimTypes;

		legs = this.repository.findLegsWithDepartureBeforeClaimRegistration();
		claimTypes = SelectChoices.from(ClaimType.class, claim.getType());
		legChoices = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "accepted", "draftMode", "leg");
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("types", claimTypes);

		super.getResponse().addData(dataset);
	}
}
