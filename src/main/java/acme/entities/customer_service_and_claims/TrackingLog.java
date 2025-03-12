
package acme.entities.customer_service_and_claims;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidTrackingLog;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidTrackingLog
public class TrackingLog extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdateMoment;

	@Mandatory
	@ValidString(max = 50, min = 1)
	@Automapped
	private String				step;

	@Optional
	@ValidNumber(max = 100., min = 0., fraction = 2)
	@Automapped
	private Double				resolutionPercentage;

	@Optional
	@Valid
	@Automapped
	private Boolean				indicator;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				resolution;

	// Relationships -----------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Claim				claim;
}
