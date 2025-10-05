
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.maintenance_and_technical.ActivityLog;
import acme.realms.FlightCrewMember;

@GuiService
public class MemberActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private MemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment fa;
		FlightCrewMember member;

		masterId = super.getRequest().getData("masterId", int.class);
		fa = this.repository.findFlightAssignmentById(masterId);
		if (fa == null)
			status = false;
		else {
			member = fa.getFlightCrew();
			status = super.getRequest().getPrincipal().hasRealm(member) && !fa.getDraft() && MomentHelper.isAfter(MomentHelper.getCurrentMoment(), fa.getLeg().getScheduledArrival());
		}
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		ActivityLog al;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		al = new ActivityLog();
		al.setFlightAssignment(this.repository.findFlightAssignmentById(masterId));
		al.setDraft(true);
		al.setRegistrationMoment(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(al);
	}

	@Override
	public void bind(final ActivityLog al) {
		super.bindObject(al, "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog al) {
		;
	}

	@Override
	public void perform(final ActivityLog al) {
		al.setDraft(true);
		al.setFlightAssignment(this.repository.findFlightAssignmentById(super.getRequest().getData("masterId", int.class)));
		this.repository.save(al);
	}

	@Override
	public void unbind(final ActivityLog al) {
		Dataset dataset;
		Collection<FlightAssignment> fas;
		FlightAssignment fa;
		SelectChoices choicesFas;

		fas = this.repository.findAllFlightAssignments();

		choicesFas = SelectChoices.from(fas, "id", al.getFlightAssignment());
		dataset = super.unbindObject(al, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draft");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("assignments", choicesFas);

		super.getResponse().addData(dataset);
	}

}
