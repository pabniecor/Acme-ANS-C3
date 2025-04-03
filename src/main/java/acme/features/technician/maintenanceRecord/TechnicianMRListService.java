
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianMRListService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	TechnicianMRRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		Collection<MaintenanceRecord> maintenanceRecords;
		Integer technicianId;

		technicianId = this.repository.findTechnicianById(super.getRequest().getPrincipal().getAccountId()).getId();
		maintenanceRecords = this.repository.findMRsByTechnicianId(technicianId);

		super.getBuffer().addData(maintenanceRecords);

	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {

		Dataset dataset;

		dataset = super.unbindObject(maintenanceRecord, "momentDone", "maintenanceStatus", "nextInspection", "estimatedCost");

		super.addPayload(dataset, maintenanceRecord, "notes", "draftMode", "aircraft.model", "technician.licenseNumber");
		super.getResponse().addData(dataset);
	}
}
