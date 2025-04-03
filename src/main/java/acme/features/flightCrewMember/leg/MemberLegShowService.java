
package acme.features.flightCrewMember.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight_management.Leg;
import acme.entities.flight_management.LegStatus;
import acme.realms.FlightCrewMember;

@GuiService
public class MemberLegShowService extends AbstractGuiService<FlightCrewMember, Leg> {

	@Autowired
	private MemberLegRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class));
	}

	@Override
	public void load() {
		Leg leg;
		int masterId;

		masterId = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(masterId);

		super.getBuffer().addData(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;
		SelectChoices choicesS;

		choicesS = SelectChoices.from(LegStatus.class, leg.getStatus());
		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "flight", "departureAirport", "arrivalAirport", "aircraft");
		dataset.put("flight", leg.getFlight().getTag());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getIataCode());
		dataset.put("departureAirport", leg.getDepartureAirport().getIataCode());
		dataset.put("aircraft", leg.getAircraft().getModel());
		dataset.put("statuss", choicesS);
		super.getResponse().addData(dataset);
	}
}
