
package acme.entities.customer_service_and_claims;

import java.util.Date;

import javax.persistence.Entity;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Review extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				name;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				momentPosted;

	@Mandatory
	@ValidString(max = 50, min = 1)
	@Automapped
	private String				subject;

	@Mandatory
	@ValidString(max = 255, min = 1)
	@Automapped
	private String				text;

	@Optional
	@ValidNumber(min = 0, max = 10)
	@Automapped
	private Double				score;

	@Optional
	@Valid
	@Automapped
	private Boolean				recommended;

}
