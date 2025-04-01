
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport_management.Duty;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.flight_management.Leg;
import acme.realms.FlightCrewMember;

@GuiService
public class MemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private MemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		Boolean status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment fa;
		int id;

		id = super.getRequest().getData("id", int.class);
		fa = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(fa);
	}

	@Override
	public void bind(final FlightAssignment fa) {
		super.bindObject(fa, "leg", "flightCrew", "duty", "moment", "currentStatus", "remarks");
	}

	@Override
	public void validate(final FlightAssignment fa) {
		super.state(fa.getDuty() == Duty.LEAD_ATTENDANT, "duty", "acme.validation.leadAttendant");
	}
	@Override
	public void perform(final FlightAssignment fa) {
		fa.setDraft(false);
		this.repository.save(fa);
	}

	@Override
	public void unbind(final FlightAssignment fa) {
		assert fa != null;
		Dataset dataset;
		Collection<Leg> legs;
		Collection<FlightCrewMember> fcms;
		SelectChoices choisesLeg;
		SelectChoices choisesSta;
		SelectChoices choisesDut;
		SelectChoices choisesMem;

		legs = this.repository.findAllLegs();
		fcms = this.repository.findAllMembers();

		choisesLeg = SelectChoices.from(legs, "flightNumber", fa.getLeg());
		choisesSta = SelectChoices.from(acme.entities.airport_management.Status.class, fa.getCurrentStatus());
		choisesDut = SelectChoices.from(Duty.class, fa.getDuty());
		choisesMem = SelectChoices.from(fcms, "employeeCode", fa.getFlightCrew());

		dataset = super.unbindObject(fa, "leg", "flightCrew", "duty", "moment", "currentStatus", "remarks", "draft");
		if (fa.getDuty() != Duty.LEAD_ATTENDANT || fa.getDraft() == false)
			dataset.put("readonly", true);
		dataset.put("leg", choisesLeg.getSelected().getKey());
		dataset.put("legs", choisesLeg);
		dataset.put("status", choisesSta);
		dataset.put("duties", choisesDut);
		dataset.put("members", choisesMem);
		dataset.put("flightCrew", choisesMem.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

}
