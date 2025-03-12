
package acme.entities.flight_management;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 1, max = 50) //min especifies "up to 50 characters"
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				selfTransfer;

	@Mandatory
	@ValidMoney(min = 0.00, max = 1000000.00)
	@Automapped
	private Money				cost;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				description;

	// Derived attributes -----------------------------------------------------


	@Transient
	public Date getDeparture() { //min = Current time, max = 2201/01/01 00:00:00
		Date result;
		FlightRepository repository;
		Date departure;

		repository = SpringHelper.getBean(FlightRepository.class);
		departure = repository.computeDeparture(this.getId());
		result = departure;

		return result;
	}

	@Transient
	public Date getArrival() { //min = scheduledDeparture + 1 minute, max = 2201/01/01 00:00:00
		Date result;
		FlightRepository repository;
		Date arrival;

		repository = SpringHelper.getBean(FlightRepository.class);
		arrival = repository.computeArrival(this.getId());
		result = arrival;

		return result;
	}

	@Transient
	public String getOriginCity() { //min = 1, max = 50
		String result;
		FlightRepository repository;
		String departureCity;

		repository = SpringHelper.getBean(FlightRepository.class);

		departureCity = repository.computeOriginCity(this.getId());
		result = departureCity == null ? "null" : departureCity;

		return result;
	}

	@Transient
	public String getDestinationCity() { //min = 1, max = 50
		String result;
		FlightRepository repository;
		String arrivalCity;

		repository = SpringHelper.getBean(FlightRepository.class);

		arrivalCity = repository.computeOriginCity(this.getId());
		result = arrivalCity == null ? "null" : arrivalCity;

		return result;
	}

	@Transient
	public Integer getLayovers() {
		Integer result;
		FlightRepository repository;
		Integer layovers;

		repository = SpringHelper.getBean(FlightRepository.class);
		layovers = repository.computeNumLayovers(this.getId());
		result = layovers == null ? 0 : layovers.intValue();

		return result;
	}

	// Relationships ----------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager manager;

}
