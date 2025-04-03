
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Aircraft;
import acme.entities.airport_management.Airport;
import acme.entities.flight_management.Leg;
import acme.entities.flight_management.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int legId;
		Leg leg;
		Manager manager;

		legId = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(legId);
		manager = leg == null ? null : leg.getFlight().getManager();
		status = leg != null && leg.getDraftMode() && super.getRequest().getPrincipal().hasRealm(manager);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int legId;
		Leg leg;

		legId = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(legId);

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
