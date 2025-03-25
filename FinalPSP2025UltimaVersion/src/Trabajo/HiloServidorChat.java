package Trabajo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//Clase que maneja la comunicación con un cliente en un hilo separado
class HiloServidorChat extends Thread {
	private Socket socket; // Socket que representa la conexión con el cliente
	private PrintWriter writer; // Permite enviar mensajes al cliente
	private BufferedReader reader; // Permite leer mensajes del cliente
	// Constructor que recibe el socket del cliente

	public HiloServidorChat(Socket socket) {
		this.socket = socket;
	}

	// Método que se ejecuta cuando el hilo comienza
	public void run() {
		try {
			// Configuración de entrada y salida de datos para la comunicación con el
			// cliente
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Para recibir datos del
																							// cliente
			writer = new PrintWriter(socket.getOutputStream(), true); // Para enviar datos al cliente
			// Agregar el cliente a la lista de clientes conectados en el servidor
			ServidorChat.agregarCliente(writer);
			String mensaje;
			// Bucle que escucha continuamente los mensajes del cliente
			while ((mensaje = reader.readLine()) != null) {
				System.out.println("Mensaje recibido: " + mensaje); // Muestra el mensaje en la consola del servidor
				ServidorChat.enviarMensajeATodos(mensaje); // Reenvía el mensaje a todos los clientes conectados
			}
		} catch (IOException e) {
			e.printStackTrace(); // Muestra un error si ocurre un problema de conexión
		} finally {
			// Cuando el cliente se desconecta, lo eliminamos de la lista del servidor
			ServidorChat.eliminarCliente(writer);
			try {
				socket.close(); // Cierra la conexión con el cliente
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
