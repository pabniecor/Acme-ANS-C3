
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
import acme.entities.maintenance_and_technical.ActivityLog;
import acme.realms.FlightCrewMember;

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
		if (fa == null)
			status = false;
		else
			status = super.getRequest().getPrincipal().hasRealm(fa.getFlightCrew()) && fa.getDraft();
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
		Collection<ActivityLog> logs = this.repository.findAllActivityLogByAssignmentId(fa.getId());
		super.state(logs.stream().allMatch(x -> x.getDraft()), "*", "acme.validation.logsPublished.message");
	}

	@Override
	public void perform(final FlightAssignment fa) {
		Collection<ActivityLog> logs = this.repository.findAllActivityLogByAssignmentId(fa.getId());
		if (logs.isEmpty())
			this.repository.delete(fa);
		if (logs.stream().allMatch(x -> x.getDraft())) {
			this.repository.deleteAll(logs);
			this.repository.delete(fa);
		}
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

		super.getResponse().addData(dataset);
	}
}
