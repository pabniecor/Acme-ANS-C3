
package acme.features.technician.involves;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
public class TechnicianInvolvesCreateService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TechnicianInvolvesRepository repository;


	@Override
	public void authorise() {
		boolean status;
		Collection<Task> tasks;
		Collection<MaintenanceRecord> MRs;
		int selectedTaskId;
		Technician loggedTechnician;
		int selectedMRId;
		Task t;
		MaintenanceRecord mr;

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		if (super.getRequest().hasData("id", int.class)) {
			loggedTechnician = this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId());

			selectedMRId = super.getRequest().getData("maintenanceRecord", int.class);
			MRs = this.repository.findMRsByTechnicianId(loggedTechnician.getId());
			mr = this.repository.findMRById(selectedMRId);

			tasks = this.repository.findPublicTasks();
			selectedTaskId = super.getRequest().getData("task", int.class);
			t = this.repository.findTaskById(selectedTaskId);

			if (selectedTaskId != 0 && selectedMRId != 0)
				status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && tasks.contains(t) && MRs.contains(mr);
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

		Integer technicianId = this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId()).getId();

		if (super.getRequest().getData("maintenanceRecord", MaintenanceRecord.class) != null && super.getRequest().getData("task", Task.class) != null) {
			boolean isTaskNotAssigned;
			Set<Integer> tasksIds;
			Set<Integer> technicianTasksIds;
			List<Integer> nonAssignedTasksIds;

			tasksIds = this.repository.findTasksIdsByMRId(super.getRequest().getData("maintenanceRecord", MaintenanceRecord.class).getId()).stream().collect(Collectors.toSet());
			technicianTasksIds = this.repository.findPublicTasks().stream().map(t -> t.getId()).collect(Collectors.toSet());
			technicianTasksIds.removeAll(tasksIds);
			nonAssignedTasksIds = technicianTasksIds.stream().toList();

			isTaskNotAssigned = nonAssignedTasksIds.contains(super.getRequest().getData("task", Task.class).getId());
			String parsedTasksIds = nonAssignedTasksIds.toString().replace("[", "").replace("]", "");
			super.state(isTaskNotAssigned, "task", "acme.validation.technician.involves.error.alreadyAssignedTask.message", parsedTasksIds.isBlank() ? "none" : parsedTasksIds);

			boolean isMRAlreadyAssigned;
			Set<Integer> MRsIds;
			Set<Integer> technicianMRsIds;
			List<Integer> nonAssignedMRsIds;

			MRsIds = this.repository.findMRsIdsByTaskId(super.getRequest().getData("task", Task.class).getId()).stream().collect(Collectors.toSet());
			technicianMRsIds = this.repository.findMRsByTechnicianId(technicianId).stream().map(mr -> mr.getId()).collect(Collectors.toSet());
			technicianMRsIds.removeAll(MRsIds);
			nonAssignedMRsIds = technicianMRsIds.stream().toList();

			isMRAlreadyAssigned = nonAssignedMRsIds.contains(super.getRequest().getData("maintenanceRecord", MaintenanceRecord.class).getId());
			String parsedMRIds = nonAssignedMRsIds.toString().replace("[", "").replace("]", "");
			super.state(isMRAlreadyAssigned, "maintenanceRecord", "acme.validation.technician.involves.error.alreadyAssignedMR.message", parsedMRIds.isBlank() ? "none" : parsedMRIds);
		}

	}

	@Override
	public void perform(final Involves involves) {
		this.repository.save(involves);
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
		tasks = this.repository.findPublicTasks();
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
