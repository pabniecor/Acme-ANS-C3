
package acme.entities.flight_management;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.helpers.MomentHelper;
import acme.constraints.ValidLeg;
import acme.entities.airline_operations.Aircraft;
import acme.entities.airport_management.Airport;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidLeg
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z]{3}\\d{4}$")  // IATA code + 4 dígitos
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory
	@ValidMoment(past = false) //min = Current time, max = 2201/01/01  00:00:00
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledDeparture;

	@Mandatory
	@ValidMoment //min = scheduledDeparture + 1 minute, max = 2201/01/01  00:00:00
	@Temporal(TemporalType.TIMESTAMP)
	private Date				scheduledArrival;

	@Mandatory
	@Valid
	@Automapped
	private LegStatus			status;

	// Derived attributes -----------------------------------------------------


	@Transient
	public Double getDuration() {
		Integer result = MomentHelper.computeDuration(this.scheduledArrival, this.scheduledDeparture).toHoursPart();
		return result.doubleValue();
	}

	// Relationships ----------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight		flight;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport		departureAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport		arrivalAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft	aircraft;

	// Additional attributes ----------------------------------------------------------

	@Mandatory
	@ValidNumber(min = 1)
	@Automapped
	private Integer		sequenceOrder;

	@Mandatory
	@Valid
	@Automapped
	private Boolean		draftMode;
}
