import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.*;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
import java.sql.Timestamp;

import java.util.HashMap;
import java.util.Map;


class ClienteChat {
    static String LOG_ESCLAVO;
    static int PORT;
    static String HOST;
    //private static final int PORT = 4002;
    public static boolean is_File_Exists(File file) {
        return file.exists() && !file.isDirectory();
    }

    static public void main(String args[]) {
        if (args.length != 1) {
            System.err.println("Uso: ClienteChat nombreCliente");
            return;
        }


        /**
         * Lectura del .env
         */

        String envFilePath = ".env";

        try (BufferedReader reader = new BufferedReader(new FileReader(envFilePath))) {
            Map<String, String> envVariables = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    envVariables.put(key, value);
                }
            }

            // Utilizar las variables de entorno

            LOG_ESCLAVO = envVariables.get("LOG_ESCLAVO").trim();
            HOST = envVariables.get("HOST").trim();
            PORT = Integer.parseInt(envVariables.get("PORT").trim());



            System.out.println("LOG_ESCLAVO: " + LOG_ESCLAVO);
            System.out.println("HOST: " + HOST);
            System.out.println("PORT: " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }




        try {
            
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            ServicioChat srv = (ServicioChat) registry.lookup("Chat");

            ClienteImpl c = new ClienteImpl();

            srv.alta(c);

            Scanner ent = new Scanner(System.in);

            String client = args[0];

            String msg = "";
            String msgExit = "EXIT";

            boolean doCode = true; 
            //while (ent.hasNextLine()) {
            //System.out.println("Para iniciar servidor escribe cualquier cosa ");
            while (doCode) {
               
                //msg = ent.nextLine();
                if (msg.equals(msgExit)) {

                    srv.envio(c, client, " Se ha cerrado el nodo: "+client);
                    break;
                } else {

                    String stringEnvio = msg;
                    //srv.envio(c, client, stringEnvio);
                    /**
                     * Lógica de los logs
                     */
                    String clientMemory = "memory_"+args[0];
                    String memoryLog = "log\\"+clientMemory+".txt"; //Path del log de memoria
                    File fileMemory = new File(memoryLog);
                    String ultimoLog = "";
                    /**
                     * Verificamos si el archivo de memoria existe, si no creamos uno
                     */
                    if (is_File_Exists(fileMemory)) {//Si existe leemos el ultimo log
                        System.out.println("Leyendo memoria");
                        FileReader LeerMemoria = new FileReader(fileMemory);
                        BufferedReader brLog = new BufferedReader(LeerMemoria);
                        
                        ultimoLog = brLog.readLine();

                        // System.out.println("Este es el ultimo log encontrado");
                        // System.out.println(ultimoLog);

                        brLog.close();
                        LeerMemoria.close();
                    }
                    else {
                        fileMemory.createNewFile();
                        ultimoLog = "Vacio";
                        
                        System.out.println("El Archivo ha sido creado");
                        
                    }

                    /**
                     * Leemos el todos los logs a partir del ultimo log (memoria del ultimo log leido)
                     */

                    String rutaArchivo1 = LOG_ESCLAVO;
                    File archivo1 = new File(rutaArchivo1);
                    FileReader fr = new FileReader(archivo1);
                    BufferedReader br = new BufferedReader(fr);
                    String linea1;
                    while ((linea1 = br.readLine()) != null && !linea1.trim().equals(ultimoLog.trim()) && !ultimoLog.trim().equals("Vacio")) {
                        //System.out.println("Diferentes"); //Recorre hasta que encuentra la linea donde se encuentra el ultimo log (MEROMIRAZION)
                    }
                    int i = 0;
                    while( (linea1 = br.readLine()) != null ){
                        System.out.println(linea1);
                        i++;
                        // TODO: Hay que enviar al servidor estas lineas, asi se puede centralizar el log
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                        // Obtener la representación en milisegundos desde 1970
                        long millisecondsOp = timestamp.getTime();

                        // Imprimir el resultado
                        String LogOp = linea1+";"+millisecondsOp+";"+args[0];
                        ultimoLog = linea1;
                        
                        srv.envio(c, client, LogOp);
                        

                        //Fin envío al MasterLog
                        
                        /**
                         * Arreglamos el String para enviar después
                         */
                
                    }
                    if(i<1){ //Si no encuentra nuevos logs nos lo hace saber
                        System.out.println("No se encontraron logs nuevos");

                    }

                    br.close();
                    fr.close();
                    System.out.println("Fin busqueda, esperar 60 segundos");

                    /**
                     * Reemplazar el log memoria en el archivo, de tal manera que tenga persistencia aunque el nodo caiga
                     */
                    try{
                        BufferedWriter writer3 = new BufferedWriter(new FileWriter(memoryLog));
                        writer3.write(ultimoLog);
                        writer3.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // System.out.println(client + " dice > ");
                Thread.sleep(60000);  //5000 = 5 segundos, 60000 = 60 seg
            }
            srv.baja(c);
            System.exit(0);
        } catch (RemoteException e) {
            System.err.println("Error de comunicacion: " + e.toString());
        } catch (Exception e) {
            System.err.println("Excepcion en ClienteChat:");
            e.printStackTrace();
        }
    }
}
