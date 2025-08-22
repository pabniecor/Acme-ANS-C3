
package acme.features.flightCrewMember.flightAssignment;

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
			boolean statusLeg = l == 0 ? true : this.repository.findAllLegsPublished().contains(leg);
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
		fa.setMoment(MomentHelper.getCurrentMoment());
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
		fa.setDraft(true);
		fa.setFlightCrew(this.repository.findFlightCrewMemberById(this.getRequest().getPrincipal().getActiveRealm().getId()));
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

		fcm = this.repository.findFlightCrewMemberById(this.getRequest().getPrincipal().getActiveRealm().getId());
		legs = this.repository.findLegsByAirline(fcm.getAirline().getId());

		choisesLeg = SelectChoices.from(legs, "flightNumber", fa.getLeg());
		choisesSta = SelectChoices.from(acme.entities.airport_management.Status.class, fa.getCurrentStatus());
		choisesDut = SelectChoices.from(Duty.class, fa.getDuty());

		dataset = super.unbindObject(fa, "leg", "duty", "currentStatus", "remarks", "draft");
		dataset.put("leg", choisesLeg.getSelected().getKey());
		dataset.put("legs", choisesLeg);
		dataset.put("status", choisesSta);
		dataset.put("duties", choisesDut);
		dataset.put("member", fa.getFlightCrew().getId());

		super.getResponse().addData(dataset);
	}

}
