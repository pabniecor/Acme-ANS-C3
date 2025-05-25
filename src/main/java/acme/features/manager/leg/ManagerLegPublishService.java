
package acme.features.manager.leg;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Aircraft;
import acme.entities.airline_operations.AircraftStatus;
import acme.entities.airport_management.Airport;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.Leg;
import acme.entities.flight_management.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegPublishService extends AbstractGuiService<Manager, Leg> {

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
				status = statusDa && statusAa && statusA;
			}
		}

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
		boolean activeAircraftStatus;
		activeAircraftStatus = leg.getAircraft() != null && leg.getAircraft().getStatus() == AircraftStatus.ACTIVE_SERVICE;
		super.state(activeAircraftStatus, "aircraft", "acme.validation.leg.aircraft.message");

		boolean validDate;
		Date currentMoment = MomentHelper.getCurrentMoment();
		if (leg.getScheduledDeparture() != null) {
			validDate = MomentHelper.isAfterOrEqual(leg.getScheduledDeparture(), currentMoment);
			super.state(validDate, "scheduledDeparture", "acme.validation.leg.scheduledDeparture.message");
		}
	}

	@Override
	public void perform(final Leg leg) {
		leg.setDraftMode(false);
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
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

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "flight", "departureAirport", "arrivalAirport", "aircraft", "sequenceOrder", "draftMode");
		dataset.put("flight", flight.getSelected().getKey());
		dataset.put("departureAirport", departureAirport.getSelected().getKey());
		dataset.put("arrivalAirport", arrivalAirport.getSelected().getKey());
		dataset.put("aircraft", aircraft.getSelected().getKey());
		dataset.put("flights", flight);
		dataset.put("departureAirports", departureAirport);
		dataset.put("arrivalAirports", arrivalAirport);
		dataset.put("aircrafts", aircraft);
		dataset.put("status", legStatus);
		dataset.put("duration", leg.getDuration());

		super.getResponse().addData(dataset);
	}

}
