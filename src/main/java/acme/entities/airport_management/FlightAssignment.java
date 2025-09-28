
package acme.entities.airport_management;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.flight_management.Leg;
import acme.realms.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "duty, draft"), @Index(columnList = "draft")
})
public class FlightAssignment extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg					leg;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private FlightCrewMember	flightCrew;

	@Mandatory
	@Valid
	@Automapped
	private Duty				duty;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				moment;

	@Mandatory
	@Valid
	@Automapped
	private Status				currentStatus;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				remarks;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				draft;
}
