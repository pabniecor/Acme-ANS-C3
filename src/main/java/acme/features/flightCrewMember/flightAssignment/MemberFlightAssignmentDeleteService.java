
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
public class MemberFlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private MemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment fa;

		masterId = super.getRequest().getData("id", int.class);
		fa = this.repository.findFlightAssignmentById(masterId);
		status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class) && fa != null && fa.getDraft() && super.getRequest().getPrincipal().getAccountId() == fa.getFlightCrew().getUserAccount().getId() && fa.getDraft();
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
		super.bindObject(fa, "leg", "duty", "moment", "currentStatus", "remarks");
	}

	@Override
	public void validate(final FlightAssignment fa) {
		boolean confirmation;
		FlightCrewMember fcm;
		//		Leg leg;
		Collection<Leg> legs;
		Long nPilots;
		Long nCopilots;
		fcm = fa.getFlightCrew();
		super.state(fcm.getAvailabilityStatus() == Status.AVAILABLE, "*", "acme.validation.flightCrewUnavailable.message");
		Date currentMoment = MomentHelper.getCurrentMoment();
		Timestamp moment = Timestamp.from(currentMoment.toInstant());

		legs = this.repository.findLegsByFlightCrewMemberId(moment, fcm.getId());
		super.state(legs.isEmpty() || legs.contains(fa.getLeg()) || legs.size() == 1, "leg", "acme.validation.legAssigned.message");

		if (fa.getLeg() != null) {
			nPilots = this.repository.countMembersByIdAndDuty(fa.getLeg().getId(), Optional.of(Duty.PILOT));
			nCopilots = this.repository.countMembersByIdAndDuty(fa.getLeg().getId(), Optional.of(Duty.CO_PILOT));

			if (fa.getDuty() == Duty.PILOT)
				super.state(nPilots < 1 || fcm != super.getRequest().getPrincipal().getActiveRealm(), "duty", "acme.validation.tooManyPilots.message");

			if (fa.getDuty() == Duty.CO_PILOT)
				super.state(nCopilots < 1 || fcm != super.getRequest().getPrincipal().getActiveRealm(), "duty", "acme.validation.tooManyCopilots.message");
		}

	}

	@Override
	public void perform(final FlightAssignment fa) {
		this.repository.delete(fa);
	}

	@Override
	public void unbind(final FlightAssignment fa) {
		assert fa != null;
		Dataset dataset;
		Collection<Leg> legs;
		FlightCrewMember fcm;
		SelectChoices choisesLeg;
		SelectChoices choisesSta;
		SelectChoices choisesDut;

		legs = this.repository.findAllLegs();
		fcm = this.repository.findFlightCrewMemberById(this.getRequest().getPrincipal().getActiveRealm().getId());

		choisesLeg = SelectChoices.from(legs, "flightNumber", fa.getLeg());
		choisesSta = SelectChoices.from(acme.entities.airport_management.Status.class, fa.getCurrentStatus());
		choisesDut = SelectChoices.from(Duty.class, fa.getDuty());

		dataset = super.unbindObject(fa, "leg", "duty", "moment", "currentStatus", "remarks", "draft");
		dataset.put("leg", choisesLeg.getSelected().getKey());
		dataset.put("legs", choisesLeg);
		dataset.put("status", choisesSta);
		dataset.put("duties", choisesDut);

		super.getResponse().addData(dataset);
	}
}
