
package acme.features.administrator.airport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.airport_management.Airport;

@GuiController
public class AdministratorAirportController extends AbstractGuiController<Administrator, Airport> {

	@Autowired
	private AdministratorAirportListService	listService;

	@Autowired
	private AdministratorAirportShowService	showService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}
}
