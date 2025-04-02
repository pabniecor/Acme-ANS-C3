
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
	private AssistanceAgentClaimListService		listService;

	@Autowired
	private AssistanceAgentClaimShowService		showService;

	@Autowired
	private AssistanceAgentClaimCreateService	createService;

	@Autowired
	private AssistanceAgentClaimUpdateService	updateService;

	@Autowired
	private AssistanceAgentClaimDeleteService	deleteService;

	@Autowired
	private AssistanceAgentClaimPublishService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("publish", "update", this.publishService);
	}
}
