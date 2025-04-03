
package acme.features.assistance_agent.tracking_log;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.customer_service_and_claims.TrackingLog;
import acme.realms.AssistanceAgent;

@GuiController
public class AssistanceAgentTrackingLogController extends AbstractGuiController<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogListService		listService;

	@Autowired
	private AssistanceAgentTrackingLogShowService		showService;

	@Autowired
	private AssistanceAgentTrackingLogCreateService		createService;

	@Autowired
	private AssistanceAgentTrackingLogUpdateService		updateService;

	@Autowired
	private AssistanceAgentTrackingLogDeleteService		deleteService;

	@Autowired
	private AssistanceAgentTrackingLogPublishService	publishService;


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
