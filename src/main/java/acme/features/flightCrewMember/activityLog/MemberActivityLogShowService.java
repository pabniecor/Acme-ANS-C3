
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
public class MemberActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private MemberActivityLogRepository repository;


	@Override
	public void authorise() {
		Boolean status;
		int masterId;
		FlightCrewMember member;
		ActivityLog al;

		masterId = super.getRequest().getData("id", int.class);
		al = this.repository.findActivityLogById(masterId);
		if (al == null)
			status = false;
		else {
			member = al.getFlightAssignment().getFlightCrew();
			status = super.getRequest().getPrincipal().hasRealm(member) && MomentHelper.isAfterOrEqual(MomentHelper.getCurrentMoment(), al.getRegistrationMoment());
		}

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
	public void unbind(final ActivityLog al) {
		Dataset dataset;
		Collection<FlightAssignment> fas;
		SelectChoices choicesFas;

		fas = this.repository.findAllFlightAssignments();

		choicesFas = SelectChoices.from(fas, "id", al.getFlightAssignment());
		dataset = super.unbindObject(al, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draft");
		//		dataset.put("flightAssignment", choicesFas.getSelected().getKey());
		dataset.put("assignments", choicesFas);

		super.getResponse().addData(dataset);
	}
}
