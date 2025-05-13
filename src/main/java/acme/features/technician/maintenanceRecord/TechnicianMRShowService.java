
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline_operations.Aircraft;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.entities.maintenance_and_technical.MaintenanceStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMRShowService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	TechnicianMRRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		MaintenanceRecord mr = this.repository.findMRById(id);
		int technicianId = this.repository.findTechnicianByUserId(super.getRequest().getPrincipal().getAccountId()).getId();
		boolean authorised;

		if (mr != null) {
			if (mr.getDraftMode())
				authorised = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && mr.getTechnician().getId() == technicianId;
			else
				authorised = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		} else
			authorised = false;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Integer mrId;
		MaintenanceRecord maintenanceRecord;

		mrId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMRById(mrId);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord mr) {

		Dataset dataset;
		SelectChoices maintenanceStatus;
		Collection<Aircraft> aircrafts;
		Collection<Technician> technicians;
		SelectChoices aircraftChoices;
		SelectChoices technicianChoices;

		maintenanceStatus = SelectChoices.from(MaintenanceStatus.class, mr.getMaintenanceStatus());
		aircrafts = this.repository.findAllAircrafts();
		technicians = this.repository.findAllTechnicians();
		aircraftChoices = SelectChoices.from(aircrafts, "model", mr.getAircraft());
		technicianChoices = SelectChoices.from(technicians, "licenseNumber", mr.getTechnician());

		dataset = super.unbindObject(mr, "momentDone", "maintenanceStatus", "nextInspection", "estimatedCost", "notes", "draftMode", "aircraft", "technician");
		dataset.put("status", maintenanceStatus);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("technician", technicianChoices.getSelected().getKey());
		dataset.put("technicians", technicianChoices);

		super.getResponse().addData(dataset);
	}
}
