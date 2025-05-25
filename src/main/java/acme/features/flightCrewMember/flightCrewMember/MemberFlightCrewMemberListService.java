
package acme.features.flightCrewMember.flightCrewMember;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport_management.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class MemberFlightCrewMemberListService extends AbstractGuiService<FlightCrewMember, FlightCrewMember> {

	@Autowired
	private MemberFlightCrewMemberRepository repository;


	@Override
	public void authorise() {
		int masterId;
		FlightAssignment fa;
		Boolean status;

		masterId = super.getRequest().getData("masterId", int.class);
		fa = this.repository.findFlightAssignmentById(masterId);
		if (fa == null)
			status = false;
		else
			status = super.getRequest().getPrincipal().hasRealm(fa.getFlightCrew());
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<FlightCrewMember> fcms;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		fcms = this.repository.findMembersById(masterId);

		super.getBuffer().addData(fcms);
	}

	@Override
	public void unbind(final FlightCrewMember fcm) {
		Dataset dataset;

		dataset = super.unbindObject(fcm, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "airline", "salary", "yearsOfExperience");
		dataset.put("airline", fcm.getAirline().getName());
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<FlightCrewMember> fcms) {
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);

		super.getResponse().addGlobal("masterId", masterId);

	}
}
