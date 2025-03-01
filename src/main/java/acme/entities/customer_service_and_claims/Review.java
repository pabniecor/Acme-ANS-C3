
package acme.entities.customer_service_and_claims;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
	@ValidString(max = 50)
	@Automapped
	String						name;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date						momentPosted;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	String						subject;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	String						text;

	@Optional
	@ValidNumber(min = 0., max = 10.)
	@Automapped
	Double						score;

	@Optional
	@Automapped
	Boolean						recommended;

}
