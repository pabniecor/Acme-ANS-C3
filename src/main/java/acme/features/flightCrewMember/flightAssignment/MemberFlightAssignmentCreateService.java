
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
public class MemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private MemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		Boolean status;
		int id;
		FlightCrewMember fa;
		int l;
		Duty duty;
		acme.entities.airport_management.Status st;
		Leg leg;
		Collection<Duty> duties;
		Collection<acme.entities.airport_management.Status> statuss;

		status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
		if (super.getRequest().hasData("id")) {
			leg = super.getRequest().getData("leg", Leg.class);
			l = super.getRequest().getData("leg", int.class);
			duty = super.getRequest().getData("duty", Duty.class);
			st = super.getRequest().getData("currentStatus", acme.entities.airport_management.Status.class);
			boolean statusLeg = l == 0 ? true : this.repository.findAllLegs().contains(leg);
			status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class) && statusLeg;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		FlightAssignment fa;

		fa = new FlightAssignment();
		fa.setDraft(true);
		fa.setFlightCrew(this.repository.findFlightCrewMemberById(super.getRequest().getPrincipal().getActiveRealm().getId()));
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
		Collection<Leg> legs;
		Long nPilots;
		Long nCopilots;

		fcm = fa.getFlightCrew();
		super.state(fcm.getAvailabilityStatus() == Status.AVAILABLE, "*", "acme.validation.flightCrewUnavailable.message");
		Date currentMoment = MomentHelper.getCurrentMoment();
		Timestamp moment = Timestamp.from(currentMoment.toInstant());

		legs = this.repository.findLegsByFlightCrewMemberId(moment, fcm.getId());
		super.state(legs.isEmpty(), "leg", "acme.validation.legAssigned.message");

		if (fa.getLeg() != null) {
			nPilots = this.repository.countMembersByIdAndDuty(fa.getLeg().getId(), Optional.of(Duty.PILOT));
			nCopilots = this.repository.countMembersByIdAndDuty(fa.getLeg().getId(), Optional.of(Duty.CO_PILOT));

			if (fa.getDuty() == Duty.PILOT)
				super.state(nPilots < 1, "duty", "acme.validation.tooManyPilots.message");

			if (fa.getDuty() == Duty.CO_PILOT)
				super.state(nCopilots < 1, "duty", "acme.validation.tooManyCopilots.message");
		}

	}

	@Override
	public void perform(final FlightAssignment fa) {
		fa.setDraft(true);
		fa.setFlightCrew(this.repository.findFlightCrewMemberById(this.getRequest().getPrincipal().getActiveRealm().getId()));
		this.repository.save(fa);
	}

	@Override
	public void unbind(final FlightAssignment fa) {
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
		dataset.put("member", fa.getFlightCrew().getId());

		super.getResponse().addData(dataset);
	}

}
