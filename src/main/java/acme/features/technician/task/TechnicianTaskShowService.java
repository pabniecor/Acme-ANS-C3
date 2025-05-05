
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance_and_technical.Task;
import acme.entities.maintenance_and_technical.TaskType;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskShowService extends AbstractGuiService<Technician, Task> {

	@Autowired
	TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Task task = this.repository.findTaskById(id);
		int technicianId = this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId()).getId();

		boolean authorised = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && task != null && task.getTechnician().getId() == technicianId;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Integer taskId;
		Task task;

		taskId = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(taskId);

		super.getBuffer().addData(task);
	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;
		Collection<Technician> technicians;
		SelectChoices technicianChoices;
		SelectChoices taskType;

		technicians = this.repository.findAllTechnicians();
		taskType = SelectChoices.from(TaskType.class, task.getTaskType());
		technicianChoices = SelectChoices.from(technicians, "licenseNumber", task.getTechnician());

		dataset = super.unbindObject(task, "taskType", "description", "priority", "estimatedDuration", "draftMode", "technician");
		dataset.put("technician", technicianChoices.getSelected().getKey());
		dataset.put("technicians", technicianChoices);
		dataset.put("status", taskType);

		super.getResponse().addData(dataset);
	}

}
