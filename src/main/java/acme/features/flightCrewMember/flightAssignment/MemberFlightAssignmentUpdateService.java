
package acme.features.flightCrewMember.flightAssignment;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport_management.Duty;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.flight_management.Leg;
import acme.realms.FlightCrewMember;
import acme.realms.Status;

@GuiService
public class MemberFlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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
		super.bindObject(fa, "leg", "flightCrew", "duty", "moment", "currentStatus", "remarks", "draft");
	}

	@Override
	public void validate(final FlightAssignment fa) {
		boolean confirmation;
		FlightCrewMember fcm;
		//		Leg leg;
		Collection<Leg> legs;
		Long nPilots;
		Long nCopilots;

		//		confirmation = super.getRequest().getData("confirmation", boolean.class);
		//		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		fcm = super.getRequest().getData("flightCrew", FlightCrewMember.class);
		super.state(fcm.getAvailabilityStatus() == Status.AVAILABLE, "flightCrew", "acme.validation.flightCrewUnavailable.message");

		//		leg = super.getRequest().getData("leg", Leg.class);
		Date currentMoment = MomentHelper.getCurrentMoment();
		Timestamp moment = Timestamp.from(currentMoment.toInstant());
		//		super.state(MomentHelper.isBefore(leg.getScheduledDeparture(), currentMoment), "moment", "acme.validation.momentInvalid.message");
		// 		CREO Q SOLO VA EN EL PUBLISH

		legs = this.repository.findLegsByFlightCrewMemberId(moment, fcm.getId());
		super.state(legs.isEmpty(), "leg", "acme.validation.legAssigned");

		super.state(fa.getDuty() == Duty.LEAD_ATTENDANT, "duty", "acme.validation.leadAttendant");

		nPilots = this.repository.countMembersByIdAndDuty(fa.getId(), Optional.of(Duty.PILOT));
		nCopilots = this.repository.countMembersByIdAndDuty(fa.getId(), Optional.of(Duty.CO_PILOT));

		if (fa.getDuty() == Duty.PILOT)
			super.state(nPilots < 1, "flightCrew", "acme.validation.tooManyPilots");

		if (fa.getDuty() == Duty.CO_PILOT)
			super.state(nCopilots < 1, "flightCrew", "acme.validation.tooManyCopilots");

		super.state(fa.getDraft(), "draft", "acme.validation.assignmentPublished");
	}

	@Override
	public void perform(final FlightAssignment fa) {
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
		dataset.put("duty", Duty.LEAD_ATTENDANT);
		if (fa.getDuty() != Duty.LEAD_ATTENDANT)
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
