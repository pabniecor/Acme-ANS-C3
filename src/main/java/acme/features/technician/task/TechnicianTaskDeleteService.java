
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance_and_technical.Involves;
import acme.entities.maintenance_and_technical.Task;
import acme.entities.maintenance_and_technical.TaskType;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskDeleteService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean authorised = false;

		if (super.getRequest().hasData("id", int.class) && super.getRequest().getMethod().equals("POST"))
			authorised = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);

		super.getBuffer().addData(task);
	}

	@Override
	public void bind(final Task task) {
		super.bindObject(task, "taskType", "description", "priority", "estimatedDuration");
	}

	@Override
	public void validate(final Task task) {
		;
	}

	@Override
	public void perform(final Task task) {
		Collection<Involves> relatedInvolves;
		relatedInvolves = this.repository.findInvolvesByTaskId(task.getId());

		this.repository.deleteAll(relatedInvolves);
		this.repository.delete(task);
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
