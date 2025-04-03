
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance_and_technical.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskListService extends AbstractGuiService<Technician, Task> {

	@Autowired
	TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		Collection<Task> tasks;
		Integer technicianId;

		technicianId = this.repository.findTechnicianById(super.getRequest().getPrincipal().getAccountId()).getId();
		tasks = this.repository.findTasksByTechnicianId(technicianId);

		super.getBuffer().addData(tasks);

	}

	@Override
	public void unbind(final Task task) {

		Dataset dataset;

		dataset = super.unbindObject(task, "taskType", "priority", "estimatedDuration");

		super.addPayload(dataset, task, "description", "draftMode", "technician.licenseNumber");
		super.getResponse().addData(dataset);
	}

}
