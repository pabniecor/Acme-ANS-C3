
package acme.features.flightCrewMember.flightCrewMember;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport_management.FlightAssignment;
import acme.realms.FlightCrewMember;
import acme.realms.Status;

@GuiService
public class MemberFlightCrewMemberShowService extends AbstractGuiService<FlightCrewMember, FlightCrewMember> {

	@Autowired
	private MemberFlightCrewMemberRepository repository;


	@Override
	public void authorise() {
		int id;
		FlightCrewMember fc;
		FlightAssignment fa;
		Boolean status;

		id = super.getRequest().getData("id", int.class);
		fc = this.repository.findMemberById(id);
		if (fc == null)
			status = false;
		else
			status = super.getRequest().getPrincipal().hasRealm(fc);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightCrewMember fcm;
		int masterId;

		masterId = super.getRequest().getData("id", int.class);
		fcm = this.repository.findMemberById(masterId);

		super.getBuffer().addData(fcm);
	}

	@Override
	public void unbind(final FlightCrewMember fcm) {
		Dataset dataset;
		SelectChoices choicesS;

		choicesS = SelectChoices.from(Status.class, fcm.getAvailabilityStatus());
		dataset = super.unbindObject(fcm, "employeeCode", "phoneNumber", "languageSkills", "availabilityStatus", "airline", "salary", "yearsOfExperience");
		dataset.put("statuss", choicesS);
		dataset.put("airline", fcm.getAirline().getName());

		super.getResponse().addData(dataset);
	}
}
