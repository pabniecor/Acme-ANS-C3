
package acme.features.flightCrewMember.leg;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flight_management.Leg;
import acme.realms.FlightCrewMember;

@GuiController
public class MemberLegController extends AbstractGuiController<FlightCrewMember, Leg> {

	@Autowired
	private MemberLegListService	listService;

	@Autowired
	private MemberLegShowService	showService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}
}
