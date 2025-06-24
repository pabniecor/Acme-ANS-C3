
package acme.entities.maintenance_and_technical;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "maintenance_record_id"), @Index(columnList = "task_id")
})
public class Involves extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	// Relationships -----------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Task				task;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private MaintenanceRecord	maintenanceRecord;

}
