import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import org.hyperic.sigar.*;
import java.io.File;

/* Autor: Itzel Alessandra Reyes Flores*/

public class Server {
	
	final static int PUERTO=5000;
	private static Sigar sigar = new Sigar();
	
	static BufferedReader entrada;
	static DataOutputStream salida;
	
	public static void main(String args[]) throws IOException
	{		
		ServerSocket s1= new ServerSocket(PUERTO);
		System.out.println("Esperando una conexión...");
		
		Socket so = s1.accept();		
		System.out.println("Un cliente se ha conectado");
		
		entrada = new BufferedReader(new InputStreamReader(so.getInputStream()));
		salida = new DataOutputStream(so.getOutputStream());
		
		System.out.println("Confirmando conexión al cliente...");
		
		salida.writeUTF("Conexion exitosa.\r\n Que desea realizar?\r\n");
		
		String mensajeRecibido = entrada.readLine();
		System.out.println(mensajeRecibido);
		
		if(mensajeRecibido.equals("getInformation()"))
		{
			getInformation();
		}else{
			salida.writeUTF("No se puede ejecutar esta peticion, intente con otra...\r\n");			
		}
		
		salida.writeUTF("Terminando conexion...\r\n");
		salida.writeUTF("Gracias por conectarte.");
		
		System.out.println("Cerrando conexión...");
		
		s1.close();	
		
	}
	
	public static void getInformation() throws IOException{
			salida.writeUTF("**************************************\r\n");
			salida.writeUTF("***** Informacion del Servidor: ******\r\n");
			salida.writeUTF("**************************************\r\n");

	        OperatingSystem os = null;
	        NetInterfaceConfig net = null;
	        InetAddress address= null;
	        String mac="", nombreEquipo="", fecha="";
	        
	        File [] unidades = null;
	        
            
            String process;
	        Process p = null;
	        
	        try {

	            os = OperatingSystem.getInstance(); // SISTEMA OPERATIVO
	            net = sigar.getNetInterfaceConfig(null); // RED
	            
	            mac = net.getNetmask(); //DIRECCIÓN MAC	            
	            
	            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	            Date date = new Date();
	            fecha = dateFormat.format(date); //FECHA
	            
	            unidades = File.listRoots(); //UNIDADES DE DISCO
	            
	            address = InetAddress.getLocalHost();
	            nombreEquipo =  address.getHostName(); // NOMBRE DEL EQUIPO

	            //PROCESOS EN EJECUCIÓN
	            p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
	            
	        } catch (SigarException se) {
	            se.printStackTrace();
	        }

	        salida.writeUTF("\r\nSistema Operativo: "+ os.getDescription()+" "+ os.getName());
	        salida.writeUTF("\r\nUsuario Activo: "+ System.getProperty("user.name"));
	        salida.writeUTF("\r\nDireccion MAC: " + mac);
	        
	        salida.writeUTF("\r\nUnidades de disco: ");
	        for(File f : unidades){
	        	salida.writeUTF(f.toString()+"  ");	        	
	        }
	        salida.writeUTF("\r\nFecha y Hora: " + fecha);
	        salida.writeUTF("\r\nNombre del equipo: " + nombreEquipo);
	        
	        salida.writeUTF("\r\nProcesos en ejecucion:");
	        
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));			
	        while ((process = input.readLine()) != null) {
	        	salida.writeUTF(process+ "\r\n"); 
			}

	        salida.writeUTF("\r\n**************************************\r\n");
	}

}
