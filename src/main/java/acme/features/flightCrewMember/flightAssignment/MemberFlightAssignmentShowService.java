
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
import acme.entities.airport_management.Status;
import acme.entities.flight_management.Leg;
import acme.realms.FlightCrewMember;

@GuiService
public class MemberFlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private MemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		Boolean status;
		int masterId;
		FlightCrewMember member;
		FlightAssignment fa;

		masterId = super.getRequest().getData("id", int.class);
		fa = this.repository.findFlightAssignmentById(masterId);
		member = fa == null ? null : fa.getFlightCrew();
		status = fa != null && super.getRequest().getPrincipal().hasRealm(member);

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
	public void unbind(final FlightAssignment fa) {
		Dataset dataset;
		Collection<Leg> legs;
		Collection<FlightCrewMember> fcms;
		FlightCrewMember fcm;
		SelectChoices choisesLeg;
		SelectChoices choisesSta;
		SelectChoices choisesDut;
		SelectChoices choisesMem;
		Collection<Leg> publishedFaLegs;

		fcm = this.repository.findFlightCrewMemberById(this.getRequest().getPrincipal().getActiveRealm().getId());
		Timestamp date = Timestamp.from(MomentHelper.getCurrentMoment().toInstant());
		//publishedFaLegs = this.repository.findAllFlightAssignmentByFlightCrewMemberIdPublished(fcm.getId());
		//legs = this.repository.findLegsByAirline(fcm.getAirline().getId(), date);

		legs = this.repository.findLegsByAirlineAndCrew(fcm.getAirline().getId(), date, fcm.getId());

		if (!fa.getDraft())
			legs.add(fa.getLeg());

		fcms = this.repository.findAllMembers();

		choisesLeg = SelectChoices.from(legs, "flightNumber", fa.getLeg());
		choisesSta = SelectChoices.from(Status.class, fa.getCurrentStatus());
		choisesDut = SelectChoices.from(Duty.class, fa.getDuty());
		choisesMem = SelectChoices.from(fcms, "employeeCode", fa.getFlightCrew());

		dataset = super.unbindObject(fa, "leg", "flightCrew", "duty", "moment", "currentStatus", "remarks", "draft");
		if (!fa.getDraft())
			dataset.put("readonly", true);
		dataset.put("leg", choisesLeg.getSelected().getKey());
		dataset.put("legs", choisesLeg);
		dataset.put("status", choisesSta);
		dataset.put("duties", choisesDut);
		dataset.put("members", choisesMem);
		dataset.put("flightCrew", choisesMem.getSelected().getKey());
		Leg ll = this.repository.findLegById(Integer.parseInt(choisesLeg.getSelected().getKey()));
		dataset.put("ocurred", MomentHelper.isAfter(MomentHelper.getCurrentMoment(), ll.getScheduledArrival()));

		super.getResponse().addData(dataset);
	}
}
