
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
public class TechnicianTaskCreateService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task task;

		task = new Task();
		super.getBuffer().addData(task);
	}

	@Override
	public void bind(final Task task) {
		super.bindObject(task, "taskType", "description", "priority", "estimatedDuration", "draftMode", "technician");
	}

	@Override
	public void validate(final Task task) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Task task) {
		task.setDraftMode(true);
		this.repository.save(task);
	}

	@Override
	public void unbind(final Task task) {
		assert task != null;
		Dataset dataset;
		SelectChoices taskType;
		Collection<Technician> technicians;
		SelectChoices technicianChoices;

		taskType = SelectChoices.from(TaskType.class, task.getTaskType());
		technicians = this.repository.findAllTechnicians();
		technicianChoices = SelectChoices.from(technicians, "licenseNumber", task.getTechnician());

		dataset = super.unbindObject(task, "taskType", "description", "priority", "estimatedDuration", "draftMode", "technician");
		dataset.put("status", taskType);
		dataset.put("technician", technicianChoices.getSelected().getKey());
		dataset.put("technicians", technicianChoices);

		super.getResponse().addData(dataset);
	}
}
