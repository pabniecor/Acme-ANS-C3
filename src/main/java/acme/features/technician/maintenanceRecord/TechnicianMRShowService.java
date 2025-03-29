
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.entities.maintenance_and_technical.MaintenanceStatus;
import acme.entities.maintenance_and_technical.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianMRShowService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	TechnicianMRRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Integer mrId;
		MaintenanceRecord maintenanceRecord;

		mrId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findAllMRs().stream().filter(mr -> mr.getId() == mrId).findFirst().orElse(null);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {

		Dataset dataset;
		Collection<Task> tasks;
		SelectChoices maintenanceStatus;
		String parsedTasks;

		maintenanceStatus = SelectChoices.from(MaintenanceStatus.class, mr.getMaintenanceStatus());
		tasks = this.repository.findAllInvolves().stream().filter(i -> i.getMaintenanceRecord().getId() == mr.getId()).map(i -> i.getTask()).toList();
		parsedTasks = tasks.stream().map(t -> String.valueOf(t.getId())).collect(Collectors.joining(", "));

		dataset = super.unbindObject(mr, "momentDone", "maintenanceStatus", "nextInspection", "estimatedCost", "notes", "aircraft", "technician");
		dataset.put("status", maintenanceStatus);
		dataset.put("tasks", parsedTasks.isEmpty() ? "No associated tasks" : parsedTasks);

		super.getResponse().addData(dataset);
	}
}
