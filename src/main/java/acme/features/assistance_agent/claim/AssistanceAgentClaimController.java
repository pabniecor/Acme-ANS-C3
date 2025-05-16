
package acme.features.assistance_agent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.customer_service_and_claims.Claim;
import acme.realms.AssistanceAgent;

@GuiController
public class AssistanceAgentClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentCompletedClaimListService		listCompletedService;

	@Autowired
	private AssistanceAgentCompletedPublicClaimListService	listPublicCompletedService;

	@Autowired
	private AssistanceAgentUndergoingClaimListService		listUndergoingService;

	@Autowired
	private AssistanceAgentClaimShowService					showService;

	@Autowired
	private AssistanceAgentClaimCreateService				createService;

	@Autowired
	private AssistanceAgentClaimUpdateService				updateService;

	@Autowired
	private AssistanceAgentClaimDeleteService				deleteService;

	@Autowired
	private AssistanceAgentClaimPublishService				publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("list-completed", "list", this.listCompletedService);
		super.addCustomCommand("list-public-completed", "list", this.listPublicCompletedService);
		super.addCustomCommand("list-undergoing", "list", this.listUndergoingService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
