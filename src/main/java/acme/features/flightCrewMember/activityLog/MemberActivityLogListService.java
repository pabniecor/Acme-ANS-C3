
package acme.features.flightCrewMember.activityLog;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.maintenance_and_technical.ActivityLog;
import acme.realms.FlightCrewMember;

@GuiService
public class MemberActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private MemberActivityLogRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		FlightAssignment fa;

		masterId = super.getRequest().getData("masterId", int.class);
		fa = this.repository.findFlightAssignmentById(masterId);
		if (fa == null)
			status = false;
		else
			status = super.getRequest().getPrincipal().hasRealm(fa.getFlightCrew()) && !fa.getDraft() && MomentHelper.isAfter(MomentHelper.getCurrentMoment(), fa.getLeg().getScheduledArrival());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<ActivityLog> aLs;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		Date currentMoment = MomentHelper.getCurrentMoment();
		Timestamp moment = Timestamp.from(currentMoment.toInstant());
		aLs = this.repository.findActivityLogsByFlightAssignmentId(masterId, moment);

		super.getBuffer().addData(aLs);
	}

	@Override
	public void unbind(final ActivityLog al) {
		Dataset dataset;

		dataset = super.unbindObject(al, "flightAssignment", "registrationMoment", "typeOfIncident", "description", "severityLevel", "draft");
		dataset.put("flightAssignment", al.getFlightAssignment().getLeg().getFlightNumber());

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<ActivityLog> als) {
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);

		super.getResponse().addGlobal("masterId", masterId);
	}
}
