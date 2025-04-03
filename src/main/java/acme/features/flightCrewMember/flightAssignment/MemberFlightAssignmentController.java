
package acme.features.flightCrewMember.flightAssignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.airport_management.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiController
public class MemberFlightAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	@Autowired
	private MemberFlightAssignmentListCompleteService		completeListService;

	@Autowired
	private MemberFlightAssignmentsListIncompletedService	incompleteListService;

	@Autowired
	private MemberFlightAssignmentShowService				showService;

	@Autowired
	private MemberFlightAssignmentUpdateService				updateService;

	@Autowired
	private MemberFlightAssignmentCreateService				createService;

	@Autowired
	private MemberFlightAssignmentPublishService			publishService;

	@Autowired
	private MemberFlightAssignmentDeleteService				deleteService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("complete-list", "list", this.completeListService);
		super.addCustomCommand("incomplete-list", "list", this.incompleteListService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
