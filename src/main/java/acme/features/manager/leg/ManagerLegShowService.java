
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
public class ManagerLegShowService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int legId;
		Flight flight;

		legId = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightByLegId(legId);
		status = flight != null && super.getRequest().getPrincipal().hasRealm(flight.getManager()); //Meter lo de published de flight

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		assert leg != null;
		Dataset dataset;
		Collection<Flight> flights;
		Collection<Airport> airports;
		Collection<Aircraft> aircrafts;
		SelectChoices flight;
		SelectChoices departureAirport;
		SelectChoices arrivalAirport;
		SelectChoices aircraft;
		SelectChoices legStatus;

		flights = this.repository.findAllFlights();
		airports = this.repository.findAllAirports();
		aircrafts = this.repository.findAllAircrafts();
		flight = SelectChoices.from(flights, "tag", leg.getFlight());
		departureAirport = SelectChoices.from(airports, "iataCode", leg.getDepartureAirport());
		arrivalAirport = SelectChoices.from(airports, "iataCode", leg.getArrivalAirport());
		aircraft = SelectChoices.from(aircrafts, "registrationNumber", leg.getAircraft());
		legStatus = SelectChoices.from(LegStatus.class, leg.getStatus());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "flight", "departureAirport", "arrivalAirport", "aircraft", "sequenceOrder", "published");
		dataset.put("flight", flight.getSelected().getKey());
		dataset.put("departureAirport", departureAirport.getSelected().getKey());
		dataset.put("arrivalAirport", arrivalAirport.getSelected().getKey());
		dataset.put("aircraft", aircraft.getSelected().getKey());
		dataset.put("flights", flight);
		dataset.put("departureAirports", departureAirport);
		dataset.put("arrivalAirports", arrivalAirport);
		dataset.put("aircrafts", aircraft);
		dataset.put("status", legStatus);

		super.getResponse().addData(dataset);
	}

}
