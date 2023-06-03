
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

class ServidorChat  {

    static int PORT;
    static String HOST;

    //private static final int PORT = 4002;
    static public void main (String args[]) {

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

            HOST = envVariables.get("HOST").trim();
            PORT = Integer.parseInt(envVariables.get("PORT").trim());



            System.out.println("HOST: " + HOST);
            System.out.println("PORT: " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.setProperty("java.rmi.server.hostname",HOST);

        try {

            ServicioChatImpl srv = new ServicioChatImpl();
            ServicioChat stub =(ServicioChat) UnicastRemoteObject.exportObject(srv,0);

            Registry registry = LocateRegistry.getRegistry(HOST,PORT);

            System.out.println("Servidor escuchando en el puerto " + String.valueOf(PORT));

            registry.bind("Chat", stub);
        }

        catch (Exception e) {
            System.err.println("Exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
