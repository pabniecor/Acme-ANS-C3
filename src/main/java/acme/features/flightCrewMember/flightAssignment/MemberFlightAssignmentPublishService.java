
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

		leg = super.getRequest().getData("leg", Leg.class);
		l = super.getRequest().getData("leg", int.class);
		d = super.getRequest().getData("duty", Duty.class);
		s = super.getRequest().getData("currentStatus", acme.entities.airport_management.Status.class);

		legs = this.repository.findAllLegs();
		boolean statusLeg = l == 0 ? true : this.repository.findAllLegs().contains(leg);
		duties = this.repository.findAllDutyTypes();
		statuss = this.repository.findAllStatusTypes();
		boolean statusDuty = d == null ? true : duties.contains(d);
		boolean statusSt = s == null ? true : statuss.contains(s);
		boolean statusD = fa.getDraft();
		status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class) && super.getRequest().getPrincipal().getAccountId() == fa.getFlightCrew().getUserAccount().getId() && statusLeg && statusDuty && statusSt && statusD;
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
		;
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

		dataset = super.unbindObject(fa, "leg", "duty", "moment", "currentStatus", "remarks", "draft");
		if (fa.getDraft() == false)
			dataset.put("readonly", true);
		dataset.put("leg", choisesLeg.getSelected().getKey());
		dataset.put("legs", choisesLeg);
		dataset.put("status", choisesSta);
		dataset.put("duties", choisesDut);

		super.getResponse().addData(dataset);
	}

}
