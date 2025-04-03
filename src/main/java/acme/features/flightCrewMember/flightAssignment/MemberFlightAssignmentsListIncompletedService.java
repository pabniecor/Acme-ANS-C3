
package acme.features.flightCrewMember.flightAssignment;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport_management.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class MemberFlightAssignmentsListIncompletedService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private MemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class));
	}

	@Override
	public void load() {
		Collection<FlightAssignment> flightAssignments;
		int memberId;

		Date currentMoment = MomentHelper.getCurrentMoment();
		Timestamp moment = Timestamp.from(currentMoment.toInstant());

		memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		flightAssignments = this.repository.findAllFlightAssignmentIncompleted(moment, memberId);

		super.getBuffer().addData(flightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment fa) {
		Dataset dataset;

		dataset = super.unbindObject(fa, "currentStatus", "duty", "moment", "remarks", "flightCrew", "leg", "draft");

		super.getResponse().addData(dataset);
	}
}
