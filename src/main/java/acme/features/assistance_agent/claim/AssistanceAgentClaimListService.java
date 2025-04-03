
package acme.features.assistance_agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.customer_service_and_claims.Claim;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimListService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		AssistanceAgent currentAgent;
		Collection<Claim> claimsRelatedToCurrentAgent;

		currentAgent = this.repository.findAssistanceAgentByUserAccountId(userAccountId);

		claimsRelatedToCurrentAgent = this.repository.findAllCompletedClaimsByCurrentUser(currentAgent.getId());

		super.getBuffer().addData(claimsRelatedToCurrentAgent);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;

		dataset = super.unbindObject(claim, "registrationMoment", "type", "accepted");

		super.addPayload(dataset, claim, "passengerEmail", "description", "draftMode", "assistanceAgent.employeeCode", "leg.flightNumber");
		super.getResponse().addData(dataset);
	}
}
