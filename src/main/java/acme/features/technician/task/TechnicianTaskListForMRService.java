
package acme.features.technician.task;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance_and_technical.Involves;
import acme.entities.maintenance_and_technical.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskListForMRService extends AbstractGuiService<Technician, Task> {

	@Autowired
	TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		Collection<Involves> involves;
		Collection<Task> tasks = new ArrayList<>();
		Integer mrId;

		mrId = super.getRequest().getData("masterId", int.class);
		involves = this.repository.findInvolvesByMRId(mrId);
		for (Involves i : involves) {
			Task t = this.repository.findTaskById(i.getTask().getId());
			tasks.add(t);
		}

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
