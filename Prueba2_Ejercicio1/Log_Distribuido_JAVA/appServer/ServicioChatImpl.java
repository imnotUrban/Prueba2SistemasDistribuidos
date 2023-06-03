import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;


class ServicioChatImpl implements ServicioChat {
    List<Cliente> l;
    ServicioChatImpl() throws RemoteException {
        l = new LinkedList<Cliente>();
    }
    public void alta(Cliente c) throws RemoteException {
	l.add(c);
    }
    public void baja(Cliente c) throws RemoteException {
	l.remove(l.indexOf(c));
    }
    public void envio(Cliente esc, String apodo, String m)
      throws RemoteException {
        for (Cliente c: l) 
	    if (!c.equals(esc))
                c.notificacion(apodo, m);
                //System.out.println("Se envio algo: "+m+" de: "+apodo);
                try {

                    //Sacamos la hora de la operación
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    // Obtener la representación en milisegundos desde 1970
                    long milliseconds_op = timestamp.getTime();

                    


                    // Abre el archivo "logMaster.txt" para lectura
                    File archivo = new File("logMaster.txt");
                    FileReader fr = new FileReader(archivo);
                    BufferedReader br = new BufferedReader(fr);
                    
                    // Lee el contenido actual del archivo
                    StringBuilder contenido = new StringBuilder();
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        contenido.append(linea);
                        contenido.append("\n");
                    }
                    
                    // Cierra el archivo de lectura
                    br.close();
                    fr.close();
                    
                    // Abre el archivo "logMaster.txt" para escritura
                    FileWriter fw = new FileWriter(archivo);
                    BufferedWriter bw = new BufferedWriter(fw);
                    
                    // Agrega la peticion del nodox al inicio del archivo
                    bw.write(m+";"+milliseconds_op+"\n");
                    bw.write(contenido.toString());
                    
                    // Cierra el archivo de escritura
                    bw.close();
                    fw.close();
                    

                    } catch (IOException e) {
                    System.out.println("Error al leer o escribir el archivo.");
                    e.printStackTrace();
                    }

                System.out.println("Se escribio una peticion en el logMaster.");



    }
}
