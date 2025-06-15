package iNE;

import java.io.File;
import java.util.ArrayList;


public class UtilsDirectorios {

	static final String path_directorio = "C:/Users/Pablo Guerrero/eclipse-workspace/AEMET/INE";


	public static ArrayList<String> listarSubdirectorios(String path) {
		File directorio = new File(path);
		ArrayList<String> subdirectorios = new ArrayList<>();

		if (directorio.exists() && directorio.isDirectory()) {
			File[] archivos = directorio.listFiles(File::isDirectory);
			if (archivos != null) {
				for (File archivo : archivos) {
					subdirectorios.add(archivo.getName());
				}
			}
		}
		return subdirectorios;
	}

	public static ArrayList<String> listarArchivos(String path) {
		File directorio = new File(path);
		ArrayList<String> archivos = new ArrayList<>();

		// Comprobamos si el directorio existe y es un directorio
		if (directorio.exists() && directorio.isDirectory()) {
			File[] archivosLista = directorio.listFiles(File::isFile); // Filtramos solo archivos
			if (archivosLista != null) {
				for (File archivo : archivosLista) {
					archivos.add(archivo.getName());
				}
			}
		}
		return archivos;
	}

	public static void main (String[] args) {

		/*
		 * PRUEBAS PARA COMPROBAR EL FUNCIONAMIENTO DE LOS METODOS
		 */
		
		ArrayList<String> directories = listarSubdirectorios(path_directorio);
		System.out.println(directories);

		ArrayList<String> archivos = listarArchivos(path_directorio + "/" + directories.get(1));

		System.out.println(path_directorio + "/" + directories.get(1) + "/" + archivos.get(0));
	}
}
