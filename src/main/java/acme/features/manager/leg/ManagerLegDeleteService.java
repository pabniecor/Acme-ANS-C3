
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Aircraft;
import acme.entities.airport_management.Airport;
import acme.entities.airport_management.FlightAssignment;
import acme.entities.customer_service_and_claims.Claim;
import acme.entities.flight_management.Flight;
import acme.entities.flight_management.Leg;
import acme.entities.flight_management.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegDeleteService extends AbstractGuiService<Manager, Leg> {

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
		int id;
		Leg leg;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
	}

	@Override
	public void validate(final Leg leg) {
		boolean validLeg = true;
		Collection<FlightAssignment> flightAssignments = this.repository.findFlightAssignmentsByLegId(leg.getId());
		//Collection<Claim> claims = this.repository.findClaimsByLegId(leg.getId());

		for (FlightAssignment fa : flightAssignments)
			if (fa.getDraft().equals(false)) {
				validLeg = false;
				super.state(validLeg, "*", "acme.validation.leg.canNotDelete.message");
				break;
			}
		//Este bucle for es inútil, ya que, para que una Claim se publique, su Leg asociado también debe estar publicado.
		//		for (Claim c : claims)
		//			if (c.getDraftMode().equals(false)) {
		//				validLeg = false;
		//				super.state(validLeg, "*", "acme.validation.leg.canNotDelete.message");
		//				break;
		//			}
	}

	@Override
	public void perform(final Leg leg) {
		Collection<FlightAssignment> flightAssignments = this.repository.findFlightAssignmentsByLegId(leg.getId());
		Collection<Claim> claims = this.repository.findClaimsByLegId(leg.getId());

		this.repository.deleteAll(flightAssignments);
		this.repository.deleteAll(claims);
		this.repository.delete(leg);
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

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "flight", "departureAirport", "arrivalAirport", "aircraft", "sequenceOrder", "draftMode", "duration");
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
