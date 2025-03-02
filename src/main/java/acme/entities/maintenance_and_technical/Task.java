
package acme.entities.maintenance_and_technical;

import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.realms.Technician;

public class Task extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@Automapped
	private TaskType			taskType;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 10)
	@Automapped
	private Integer				priority;

	@Mandatory
	@ValidNumber(min = 0)
	@Automapped
	private Integer				estimatedDuration;

	@Mandatory
	@Valid
	@ManyToOne
	private Technician			technician;

	/*
	 * will be commented while aircrafts don't exist
	 * 
	 * @Mandatory
	 * 
	 * @Valid
	 * 
	 * @ManyToOne
	 * private Aircraft aircraft;
	 */

}
