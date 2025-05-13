
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Aircraft;
import acme.entities.airport_management.Airport;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.Leg;
import acme.entities.flight_management.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegCreateService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Flight flight;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.repository.findFlightById(masterId);
		status = flight != null && flight.getDraftMode() && super.getRequest().getPrincipal().hasRealm(flight.getManager());

		if (status) {
			String method;
			int daId, aaId, aId;
			Airport da;
			Airport aa;
			Aircraft a;

			method = super.getRequest().getMethod();

			if (method.equals("GET"))
				status = true;
			else {
				daId = super.getRequest().getData("departureAirport", int.class);
				aaId = super.getRequest().getData("arrivalAirport", int.class);
				aId = super.getRequest().getData("aircraft", int.class);
				da = super.getRequest().getData("departureAirport", Airport.class);
				aa = super.getRequest().getData("arrivalAirport", Airport.class);
				a = super.getRequest().getData("aircraft", Aircraft.class);
				@SuppressWarnings("unused")
				LegStatus st = super.getRequest().getData("status", LegStatus.class);
				Boolean statusDa = daId == 0 ? true : this.repository.findAllAirports().contains(da);
				Boolean statusAa = aaId == 0 ? true : this.repository.findAllAirports().contains(aa);
				Boolean statusA = aId == 0 ? true : this.repository.findAllAircrafts().contains(a);
				status = super.getRequest().getPrincipal().hasRealm(flight.getManager()) && statusDa && statusAa && statusA;
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int masterId;
		Flight flight;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.repository.findFlightById(masterId);

		Integer lastSequenceOrder = this.repository.findLastSequenceOrderByFlightId(masterId);
		if (lastSequenceOrder == null)
			lastSequenceOrder = 0;

		Integer sequenceOrder = lastSequenceOrder + 1;

		leg = new Leg();

		leg.setDraftMode(flight.getDraftMode());
		leg.setFlight(flight);
		leg.setSequenceOrder(sequenceOrder);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft");
	}

	@Override
	public void validate(final Leg leg) {
		;
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		assert leg != null;
		Dataset dataset;
		Collection<Airport> airports;
		Collection<Aircraft> aircrafts;
		SelectChoices departureAirport;
		SelectChoices arrivalAirport;
		SelectChoices aircraft;
		SelectChoices legStatus;

		airports = this.repository.findAllAirports();
		aircrafts = this.repository.findAllAircrafts();
		departureAirport = SelectChoices.from(airports, "iataCode", leg.getDepartureAirport());
		arrivalAirport = SelectChoices.from(airports, "iataCode", leg.getArrivalAirport());
		aircraft = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());
		legStatus = SelectChoices.from(LegStatus.class, leg.getStatus());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "flight", "departureAirport", "arrivalAirport", "aircraft", "sequenceOrder", "draftMode");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("departureAirport", departureAirport.getSelected().getKey());
		dataset.put("arrivalAirport", arrivalAirport.getSelected().getKey());
		dataset.put("aircraft", aircraft.getSelected().getKey());
		dataset.put("departureAirports", departureAirport);
		dataset.put("arrivalAirports", arrivalAirport);
		dataset.put("aircrafts", aircraft);
		dataset.put("status", legStatus);

		super.getResponse().addData(dataset);
	}

}
