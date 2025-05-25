
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.flight_management.Leg;
import acme.entities.maintenance_and_technical.ActivityLog;
import acme.realms.FlightCrewMember;

@GuiService
public class MemberActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private MemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		ActivityLog log;

		masterId = super.getRequest().getData("id", int.class);
		log = this.repository.findActivityLogById(masterId);
		if (log == null)
			status = false;
		else
			status = super.getRequest().getPrincipal().hasRealm(log.getFlightAssignment().getFlightCrew()) && !log.getFlightAssignment().getDraft();

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		ActivityLog al;
		int id;

		id = super.getRequest().getData("id", int.class);
		al = this.repository.findActivityLogById(id);

		super.getBuffer().addData(al);
	}
	@Override
	public void bind(final ActivityLog al) {
		super.bindObject(al, "registrationMoment", "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog al) {
		Leg l;

		l = al.getFlightAssignment().getLeg();
		if (al.getRegistrationMoment() != null)
			super.state(MomentHelper.isAfter(al.getRegistrationMoment(), l.getScheduledArrival()), "registrationMoment", "acme.validation.activityLog.registrationMoment.message");
	}
	@Override
	public void perform(final ActivityLog al) {
		al.setDraft(false);
		this.repository.save(al);
	}

	@Override
	public void unbind(final ActivityLog al) {
		Dataset dataset;
		Collection<FlightAssignment> fas;
		SelectChoices choicesFas;

		fas = this.repository.findAllFlightAssignments();

		choicesFas = SelectChoices.from(fas, "id", al.getFlightAssignment());
		dataset = super.unbindObject(al, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draft");
		dataset.put("assignments", choicesFas);

		super.getResponse().addData(dataset);
	}
}
