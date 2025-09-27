
package acme.features.flightCrewMember.flightAssignment;

import java.sql.Timestamp;
import java.util.Collection;

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

@GuiService
public class MemberFlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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
			s = super.getRequest().getData("currentStatus", acme.entities.airport_management.Status.class);
			FlightCrewMember fcm = this.repository.findFlightCrewMemberById(this.getRequest().getPrincipal().getActiveRealm().getId());

			Timestamp date = Timestamp.from(MomentHelper.getCurrentMoment().toInstant());
			legs = this.repository.findAllLegsPublished(date);
			//boolean statusLeg = l == 0 ? true : this.repository.findAllLegsPublished(date).contains(leg);
			boolean statusLeg = l == 0 ? true : this.repository.findLegsByAirlineAndCrew(fcm.getAirline().getId(), date, fcm.getId()).contains(leg);

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
		;
	}

	@Override
	public void perform(final FlightAssignment fa) {
		fa.setMoment(MomentHelper.getCurrentMoment());
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
		Collection<Leg> publishedFaLegs;

		fcm = this.repository.findFlightCrewMemberById(this.getRequest().getPrincipal().getActiveRealm().getId());
		Timestamp date = Timestamp.from(MomentHelper.getCurrentMoment().toInstant());

		//publishedFaLegs = this.repository.findAllFlightAssignmentByFlightCrewMemberIdPublished(fcm.getId());
		//legs = this.repository.findLegsByAirline(fcm.getAirline().getId(), date);
		//legs.removeAll(publishedFaLegs);

		legs = this.repository.findLegsByAirlineAndCrew(fcm.getAirline().getId(), date, fcm.getId());

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
