
package acme.features.technician.maintenanceRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenance_and_technical.MaintenanceRecord;
import acme.realms.Technician;

@GuiController
public class TechnicianMRController extends AbstractGuiController<Technician, MaintenanceRecord> {

	@Autowired
	TechnicianMRListService	listService;

	@Autowired
	TechnicianMRShowService	showService;


	@PostConstruct
	public void initialise() {

		super.addBasicCommand("list", this.listService);

		super.addBasicCommand("show", this.showService);
	}
}
