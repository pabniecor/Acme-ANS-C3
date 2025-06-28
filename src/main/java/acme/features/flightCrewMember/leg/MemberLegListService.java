
package acme.features.flightCrewMember.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.flight_management.Leg;
import acme.realms.FlightCrewMember;

@GuiService
public class MemberLegListService extends AbstractGuiService<FlightCrewMember, Leg> {

	@Autowired
	private MemberLegRepository repository;


	@Override
	public void authorise() {
		int id;
		FlightAssignment fa;
		Boolean status;

		id = super.getRequest().getData("masterId", int.class);
		fa = this.repository.findFlightAssignmentById(id);
		if (fa == null)
			status = false;
		else
			status = super.getRequest().getPrincipal().hasRealm(fa.getFlightCrew()) && !fa.getDraft();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Leg> legs;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		legs = this.repository.findAllLegsOfFlightAssingment(masterId);

		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "flight", "departureAirport", "arrivalAirport", "aircraft");
		dataset.put("flight", leg.getFlight().getTag());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getIataCode());
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Leg> legs) {
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);

		super.getResponse().addGlobal("masterId", masterId);

	}

}
