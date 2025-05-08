
package acme.features.technician.involves;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance_and_technical.Involves;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.entities.maintenance_and_technical.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianInvolvesDeleteService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TechnicianInvolvesRepository repository;


	@Override
	public void authorise() {
		boolean status;
		List<Task> tasks;
		List<MaintenanceRecord> MRs;
		Task selectedTask;
		Technician loggedTechnician;
		MaintenanceRecord selectedMR;

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		if (super.getRequest().hasData("id", int.class)) {
			loggedTechnician = this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId());
			tasks = this.repository.findTasksByTechnicianId(loggedTechnician.getId());
			MRs = this.repository.findMRsByTechnicianId(loggedTechnician.getId());
			selectedTask = super.getRequest().getData("task", Task.class);
			selectedMR = super.getRequest().getData("maintenanceRecord", MaintenanceRecord.class);

			if (selectedTask != null && selectedMR != null)
				status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && tasks.contains(selectedTask) && MRs.contains(selectedMR) && selectedMR.getTechnician().equals(loggedTechnician)
					&& selectedTask.getTechnician().equals(loggedTechnician);

		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Involves involves;

		involves = new Involves();

		super.getBuffer().addData(involves);
	}

	@Override
	public void bind(final Involves involves) {
		super.bindObject(involves, "task", "maintenanceRecord");
	}

	@Override
	public void validate(final Involves involves) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		if (super.getRequest().getData("maintenanceRecord", MaintenanceRecord.class) == null || super.getRequest().getData("task", Task.class) == null)
			super.state(false, "*", "acme.validation.technician.involves.error.nullDelete.message");

		if (super.getRequest().getData("maintenanceRecord", MaintenanceRecord.class) != null && super.getRequest().getData("task", Task.class) != null) {
			boolean isTaskAssigned;
			List<Integer> MRTasksIds;
			MRTasksIds = this.repository.findTasksIdsByMRId(super.getRequest().getData("maintenanceRecord", MaintenanceRecord.class).getId());
			isTaskAssigned = MRTasksIds.contains(super.getRequest().getData("task", Task.class).getId());
			String parsedTasksIds = MRTasksIds.toString().replace("[", "").replace("]", "");
			super.state(isTaskAssigned, "task", "acme.validation.technician.involves.error.notAssignedTask.message", parsedTasksIds.isBlank() ? "none" : parsedTasksIds);

			boolean isMRAssigned;
			List<Integer> TaskMRsIds;
			TaskMRsIds = this.repository.findMRsIdsByTaskId(super.getRequest().getData("task", Task.class).getId());
			isMRAssigned = TaskMRsIds.contains(super.getRequest().getData("maintenanceRecord", MaintenanceRecord.class).getId());
			String parsedMRIds = TaskMRsIds.toString().replace("[", "").replace("]", "");
			super.state(isMRAssigned, "maintenanceRecord", "acme.validation.technician.involves.error.notAssignedMR.message", parsedMRIds.isBlank() ? "none" : parsedMRIds);
		}
	}

	@Override
	public void perform(final Involves involves) {
		Involves i = this.repository.findInvolvesbyBothIds(involves.getMaintenanceRecord().getId(), involves.getTask().getId());
		this.repository.delete(i);
	}

	@Override
	public void unbind(final Involves involves) {
		assert involves != null;
		Dataset dataset;
		Collection<MaintenanceRecord> MRs;
		Collection<Task> tasks;
		SelectChoices MRChoices;
		SelectChoices taskChoices;
		int technicianId;

		technicianId = this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId()).getId();
		tasks = this.repository.findTasksByTechnicianId(technicianId);
		MRs = this.repository.findMRsByTechnicianId(technicianId);

		MRChoices = SelectChoices.from(MRs, "id", involves.getMaintenanceRecord());
		taskChoices = SelectChoices.from(tasks, "id", involves.getTask());

		dataset = super.unbindObject(involves, "task", "maintenanceRecord");
		dataset.put("maintenanceRecord", MRChoices.getSelected().getKey());
		dataset.put("MRs", MRChoices);
		dataset.put("task", taskChoices.getSelected().getKey());
		dataset.put("tasks", taskChoices);

		super.getResponse().addData(dataset);
	}

}
