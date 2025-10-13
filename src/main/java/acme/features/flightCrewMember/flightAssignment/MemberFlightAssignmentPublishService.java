
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
public class MemberFlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private MemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		Boolean status;
		int id;
		FlightAssignment fa;
		int l;
		Leg leg;
		Duty d;
		acme.entities.airport_management.Status s;
		Collection<Leg> legs;
		Collection<Duty> duties;
		Collection<acme.entities.airport_management.Status> statuss;

		id = super.getRequest().getData("id", int.class);
		fa = this.repository.findFlightAssignmentById(id);

		boolean statusD = fa == null ? false : fa.getDraft();
		boolean statusM = fa == null ? false : super.getRequest().getPrincipal().hasRealm(fa.getFlightCrew());
		status = statusM && statusD;

		String ss = super.getRequest().getMethod();
		Boolean statusMethod = ss.equals("POST");
		if (!statusMethod)
			status = false;

		if (status) {
			leg = super.getRequest().getData("leg", Leg.class);
			l = super.getRequest().getData("leg", int.class);
			d = super.getRequest().getData("duty", Duty.class);
			FlightCrewMember fcm = this.repository.findFlightCrewMemberById(this.getRequest().getPrincipal().getActiveRealm().getId());
			s = super.getRequest().getData("currentStatus", acme.entities.airport_management.Status.class);

			Timestamp date = Timestamp.from(MomentHelper.getCurrentMoment().toInstant());
			legs = this.repository.findAllLegsPublished(date);
			//boolean statusLeg = l == 0 ? true : this.repository.findAllLegsPublished(date).contains(leg);
			boolean statusLeg = l == 0 ? true : this.repository.findLegsWithoutOverlap(fcm.getAirline().getId(), date, fcm.getId(), fa.getLeg().getId()).contains(leg);
			status = statusLeg;
		}
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
		super.bindObject(fa, "leg", "duty", "currentStatus", "remarks");
	}

	@Override
	public void validate(final FlightAssignment fa) {
		FlightCrewMember fcm;
		Collection<Leg> legs;
		Collection<FlightAssignment> assignments;
		boolean overlappedLegs = true;
		FlightAssignment pilotFa;
		FlightAssignment copilotFa;

		fcm = fa.getFlightCrew();
		super.state(fcm.getAvailabilityStatus() == Status.AVAILABLE, "*", "acme.validation.flightCrewUnavailable.message");

		Date currentMoment = MomentHelper.getCurrentMoment();
		if (fa.getLeg() != null) {
			super.state(MomentHelper.isAfter(fa.getLeg().getScheduledArrival(), currentMoment), "leg", "acme.validation.legCompleted.message");
			assignments = this.repository.findAllFlightAssignmentByFMCPUBLISHED(fa.getFlightCrew().getId());

			overlappedLegs = assignments.stream().filter(a -> a.getId() != fa.getId())
				.anyMatch(a -> !(MomentHelper.isBeforeOrEqual(fa.getLeg().getScheduledArrival(), a.getLeg().getScheduledDeparture()) || MomentHelper.isBeforeOrEqual(a.getLeg().getScheduledArrival(), fa.getLeg().getScheduledDeparture())));
			super.state(!overlappedLegs, "leg", "acme.validation.overlappedLegs.message");

			if (fa.getDuty() != null) {
				pilotFa = this.repository.findAssignmentByLegIdAndDuty(fa.getLeg().getId(), Optional.of(Duty.PILOT)).stream().findFirst().orElse(null);
				super.state(pilotFa == null || pilotFa.getId() == fa.getId() || !fa.getDuty().equals(Duty.PILOT), "duty", "acme.validation.tooManyPilots.message");

				copilotFa = this.repository.findAssignmentByLegIdAndDuty(fa.getLeg().getId(), Optional.of(Duty.CO_PILOT)).stream().findFirst().orElse(null);
				super.state(copilotFa == null || copilotFa.getId() == fa.getId() || !fa.getDuty().equals(Duty.CO_PILOT), "duty", "acme.validation.tooManyCopilots.message");
			}
		}
	}
	@Override
	public void perform(final FlightAssignment fa) {
		fa.setDraft(false);
		fa.setMoment(MomentHelper.getCurrentMoment());
		this.repository.save(fa);
	}

	@Override
	public void unbind(final FlightAssignment fa) {
		Dataset dataset;
		FlightCrewMember fcm;
		Collection<Leg> legs;
		SelectChoices choisesLeg;
		SelectChoices choisesSta;
		SelectChoices choisesDut;
		Collection<Leg> publishedFaLegs;

		fcm = this.repository.findFlightCrewMemberById(this.getRequest().getPrincipal().getActiveRealm().getId());
		Timestamp date = Timestamp.from(MomentHelper.getCurrentMoment().toInstant());

		//publishedFaLegs = this.repository.findAllFlightAssignmentByFlightCrewMemberIdPublished(fcm.getId());
		//legs = this.repository.findLegsByAirline(fcm.getAirline().getId(), date);
		//legs.removeAll(publishedFaLegs);

		legs = fa.getLeg() != null ? this.repository.findLegsWithoutOverlap(fcm.getAirline().getId(), date, fcm.getId(), fa.getLeg().getId()) : this.repository.findLegsWithoutOverlapNoCurrent(fcm.getAirline().getId(), date, fcm.getId());

		choisesLeg = SelectChoices.from(legs, "flightNumber", fa.getLeg());
		choisesSta = SelectChoices.from(acme.entities.airport_management.Status.class, fa.getCurrentStatus());
		choisesDut = SelectChoices.from(Duty.class, fa.getDuty());

		dataset = super.unbindObject(fa, "leg", "duty", "currentStatus", "remarks", "draft");
		dataset.put("leg", choisesLeg.getSelected().getKey());
		dataset.put("legs", choisesLeg);
		dataset.put("status", choisesSta);
		dataset.put("duties", choisesDut);

		super.getResponse().addData(dataset);
	}

}
