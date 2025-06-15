package iNE;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class LeerExcel {




	/*
	 * FUNCION PARA SEPARAR EL STRING DEL NOMBRE DE LA FILA A
	 * LO DIVIDE EN CPRO, CMUN, CDIS, CSEC Y NOMBRE
	 * resultado[0] = CPRO
	 * resultado[1] = CMUN
	 * resultado[2] = CDIS
	 * resultado[3] = CSEC
	 * resultado[4] = NOMBRE MUNICIPIO
	 * resultado[5] = NOMBRE DISTRITO
	 * resultado[6] = NOMBRE SECCION
	 */
	public static ArrayList<String> separarNombreColumnaA(String[] partes) {

		ArrayList<String> resultado = new ArrayList<String>();
		if (partes.length == 2) {
			String codigo = partes[0]; //codigo CPRO+CMUN+{CDIS+{CSEC}}
			String nombreInicial = partes[1]; // nombre completo formato  "Belmonte de Tajo sección 01001" o "Boadilla del Monte"


			String[] nombreIntermedio = nombreInicial.split("\\s+");
			String[] nombreIntermedio2 = new String[nombreIntermedio.length-1];

			for(int i = 0; i < nombreIntermedio.length - 1; i++) {
				nombreIntermedio2[i] = nombreIntermedio[i];
			}

			String nombreMunicipio = "";
			String nombreDistrito = "";
			String nombreSeccion = "";
			for(String parte : nombreIntermedio2) {
				if(!parte.toLowerCase().startsWith("distrito") && !parte.toLowerCase().startsWith("sección")) {
					nombreMunicipio +=parte + " ";
				}
				else {
					if(parte.toLowerCase().startsWith("distrito")) {
						nombreDistrito = parte;
					}
					else {
						nombreSeccion = parte;
					}
				}
			}

			String codProvincia = "";
			String codMunicipio = "";
			String codigoDistrito = "";
			String codSeccion = "";
			if(codigo.length() == 5) {
				// Extraer los códigos de provincia y municipio del código
				codProvincia = codigo.substring(0, 2);
				codMunicipio = codigo.substring(2, 5);
			}
			else if(codigo.length() == 7) {
				// Extraer los códigos de provincia, municipio y distrito
				codProvincia = codigo.substring(0, 2);
				codMunicipio = codigo.substring(2, 5);
				codigoDistrito = codigo.substring(5,7);
				nombreDistrito += " " + nombreIntermedio[nombreIntermedio.length - 1];
			}
			else if (codigo.length() == 10){
				// Extraer los códigos de provincia, municipio, distrito y seccion
				codProvincia = codigo.substring(0, 2);
				codMunicipio = codigo.substring(2, 5);
				codigoDistrito = codigo.substring(5,7);
				codSeccion = codigo.substring(7,10);
				nombreSeccion += " " + nombreIntermedio[nombreIntermedio.length - 1];
			}

			if(codigo.length() == 5) {
				resultado.add(String.join(" ", nombreIntermedio));
			}
			else {
				resultado.add(nombreMunicipio);
			}
			resultado.add(nombreDistrito);
			resultado.add(nombreSeccion);
			resultado.add(codProvincia);
			resultado.add(codMunicipio);
			resultado.add(codigoDistrito);
			resultado.add(codSeccion);

		} else {
			System.err.println("ERROR: El formato no coincide.");
		}

		return resultado;
	}

	public static void main(String[] args) throws Exception{
		// Ruta del archivo Excel
		String nombreExcelMadrid = "C:/Users/Pablo Guerrero/Downloads/31097.xlsx";
		String nombreExcelGuadalajara = "C:/Users/Pablo Guerrero/Downloads/31034.xlsx";
		String excelAEMET = "C:/Users/Pablo Guerrero/eclipse-workspace/AEMET/INE/Madrid/31097.xlsx";

		FileInputStream file = new FileInputStream(new File(nombreExcelGuadalajara));
		Workbook workbook = new XSSFWorkbook(file);

		// Selecciona la primera hoja del archivo Excel
		Sheet sheet = workbook.getSheetAt(0);

		leerexcel("C:/Users/Pablo Guerrero/eclipse-workspace/AEMET/INE/Zamora/31269.xlsx");

		String aux = "2801901001 Belmonte de Tajo sección 01001";
		String aux2 = "2802201 Boadilla del Monte distrito 01";
		String aux3 = "28022 Boadilla del Monte";

		Row row = sheet.getRow(8);
		Cell cell = row.getCell(0);
		String[] partes = aux2.split(" ", 2);
		ArrayList<String> datos = separarNombreColumnaA(partes);
		System.out.println(datos);

	}

	// Obtiene la provincia a la que pertenecen los datos de la hoja
	public static String obtenerNombreProvinciaExcel(Sheet sheet){
		String nombreProvincia = "";
		Row filaProvincia = sheet.getRow(1);

		for(Cell cell: filaProvincia) {
			switch (cell.getCellType()) {
			case STRING:
				nombreProvincia = cell.getStringCellValue();
			default:
				break;
			}
		}
		return nombreProvincia;
	}

	// Obtiene el tipo del excel 
	// (indicadores renta media y mediana, distribucion por fuente de ingresos o indicadores demograficos)
	public static String obtenerNombreTipoExcel(Sheet sheet){

		String nombreTipoExcel = "";
		Row filaTipoExcel = sheet.getRow(3);
		for(Cell cell: filaTipoExcel) {
			switch (cell.getCellType()) {
			case STRING:
				nombreTipoExcel = cell.getStringCellValue();
			default:
				break;
			}
		}
		return nombreTipoExcel;
	}
	
	// Devuelve 0 si es municipio, 1 si es distrito, 2 si es sección
	public static int tipoDelNombre(ArrayList<String> nombre) {
		int res = -1;
		if(nombre.size() == 0) return -1;
		//Los datos que nos interesan estan en las posiciones 3,4,5,6
		String aux = nombre.get(3) + nombre.get(4) + nombre.get(5) + nombre.get(6);
		if(aux.length() == 5) res = 0;
		if(aux.length() == 7) res = 1;
		if(aux.length() == 10) res = 2;
		return res;
	}

	public static void leerexcel(String path) throws IOException{

		FileInputStream file = new FileInputStream(new File(path));
		Workbook workbook = new XSSFWorkbook(file);

		// Selecciona la primera hoja del archivo Excel
		Sheet sheet = workbook.getSheetAt(0);

		//Empezamos en la fila de indice 8 (fila 9) para evitar datos no importantes
		for (int rowIndex = 8; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row == null) continue;

			for (Cell cell : row) {

				int columnIndex = cell.getColumnIndex();
				switch (cell.getCellType()) {
				case STRING:
					if(!cell.toString().equals(".") && !(cell.getCellType() == CellType.BLANK)) {
						String[] partes = cell.getStringCellValue().split(" ", 2);

						if(!partes[0].matches("\\d+")) break; // Si la columna A no empieza por numero no nos interesa
						ArrayList<String> datos = separarNombreColumnaA(partes);
						System.out.print(datos + "  ");
					}
					else {
						System.out.print(0);	
					}
					break;
				case NUMERIC:
					// Imprimir el número directamente
					System.out.print((int)cell.getNumericCellValue() + "\t");
					break;
				case BOOLEAN:
					System.out.println(cell.getBooleanCellValue() + "\t");
					break;
				default:
					System.out.print("Unknown Type\t");
					break;
				}
			}
			System.out.println();
		}
		
		workbook.close();
	}
}

