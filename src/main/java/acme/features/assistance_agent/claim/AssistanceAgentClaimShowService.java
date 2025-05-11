
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

		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		currentAgent = this.repository.findAssistanceAgentByUserAccountId(userAccountId);
		Collection<Claim> agentXClaims = this.repository.findAllClaimsByCurrentUser(currentAgent.getId());

		boolean authorised = (super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) || claim != null && !claim.getDraftMode()) && agentXClaims.contains(claim);

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
		assert claim != null;
		Dataset dataset;
		Collection<AssistanceAgent> assistanceAgents;
		Collection<Leg> legs;
		SelectChoices agentChoices;
		SelectChoices legChoices;
		SelectChoices claimTypes;

		assistanceAgents = this.repository.findAllAssistanceAgents();
		legs = this.repository.findAllLegs();
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
