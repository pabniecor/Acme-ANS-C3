
package acme.realms;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Technician {

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2-3}\\d{6}$")
	@Column(unique = true)
	String	licenseNumber;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	String	phoneNumber;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	String	specialisation;

	@Mandatory
	@Automapped
	Boolean	healthTestPassed;

	@Mandatory
	@ValidNumber(min = 0)
	@Automapped
	Integer	experience;

	@Optional
	@ValidString(max = 255)
	@Automapped
	String	certifications;

}
