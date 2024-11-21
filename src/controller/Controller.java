package controller;

import java.io.IOException;
import java.util.Scanner;

import model.logic.Modelo;
import view.View;

public class Controller<T> {

	/* Instancia del Modelo*/
	private Modelo modelo;
	
	/* Instancia de la Vista*/
	private View view;
	
	/**
	 * Crear la vista y el modelo del proyecto
	 * @param capacidad tamaNo inicial del arreglo
	 */
	public Controller ()
	{
		view = new View();
	}
		
	public void run() {
		Scanner lector = new Scanner(System.in).useDelimiter("\n");
		boolean fin = false;
	
		while (!fin) {
			view.printMenu();
	
			int option = Integer.parseInt(lector.nextLine());
			try {
				switch (option) {
					case 1:
						cargarDatos();
						break;
					case 2:
						procesarReq1(lector);
						break;
					case 3:
						procesarReq2();
						break;
					case 4:
						procesarReq3(lector);
						break;
					case 5:
						procesarReq4();
						break;
					case 6:
						procesarReq5(lector);
						break;
					case 7:
						fin = cerrarPrograma(lector);
						break;
					default:
						view.printMessage("--------- \n Opción Inválida !! \n---------");
						break;
				}
			} catch (Exception e) {
				view.printMessage("Ha ocurrido un error: " + e.getMessage());
			}
		}
	}
	
	private void cargarDatos() throws IOException {
		view.printMessage("--------- \nCargar datos");
		modelo = new Modelo(1);
		modelo.cargar();
		view.printModelo(modelo);
	}
	
	private void procesarReq1(Scanner lector) {
		view.printMessage("--------- \nIngrese el nombre del primer punto de conexión");
		String punto1 = lector.next();
		lector.nextLine();
	
		view.printMessage("--------- \nIngrese el nombre del segundo punto de conexión");
		String punto2 = lector.next();
		lector.nextLine();
	
		String res = modelo.req1String(punto1, punto2);
		view.printMessage(res);
	}
	
	private void procesarReq2() {
		String res = modelo.req2String();
		view.printMessage(res);
	}
	
	private void procesarReq3(Scanner lector) {
		view.printMessage("--------- \nIngrese el nombre del primer país");
		String pais1 = lector.next();
		lector.nextLine();
	
		view.printMessage("--------- \nIngrese el nombre del segundo país");
		String pais2 = lector.next();
		lector.nextLine();
	
		String res = modelo.req3String(pais1, pais2);
		view.printMessage(res);
	}
	
	private void procesarReq4() {
		String res = modelo.req4String();
		view.printMessage(res);
	}
	
	private void procesarReq5(Scanner lector) {
		view.printMessage("--------- \nIngrese el nombre del punto de conexión");
		String landing = lector.next();
		lector.nextLine();
	
		String res = modelo.req5String(landing);
		view.printMessage(res);
	}
	
	private boolean cerrarPrograma(Scanner lector) {
		view.printMessage("--------- \n Hasta pronto !! \n---------");
		lector.close();
		return true;
	}
		
}
