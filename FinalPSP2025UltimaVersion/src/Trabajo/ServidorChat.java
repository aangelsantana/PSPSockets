package Trabajo; // Definimos el paquete en el que se encuentra la clase

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import java.awt.Color;

//Clase principal del servidor de chat
public class ServidorChat {
	private static final int PUERTO = 12345; // Puerto en el que el servidor escuchará conexiones
	private static Set<PrintWriter> clientes = new HashSet<>(); // Lista de clientes conectados
	private static JTextArea areaMensajes; // Área de texto donde se mostrarán los mensajes en el servidor
	private static JTextField campoMensaje; // Campo de texto donde el servidor puede escribir mensajes
	private static JButton botonEnviar; // Botón para enviar mensajes desde el servidor

	// Método principal que inicia el servidor y la interfaz gráfica
	public static void main(String[] args) {
		// Se crea la ventana del servidor con una interfaz gráfica
		JFrame frame = new JFrame("WhatsApp 2 (Servidor)"); 
		areaMensajes = new JTextArea(20, 40); 
		areaMensajes.setEditable(false); 
		campoMensaje = new JTextField(30); 
		campoMensaje.setForeground(Color.BLACK); 
		botonEnviar = new JButton(); 
		
		// Se asigna un icono al botón con la ruta de la imagen
		botonEnviar.setIcon(new ImageIcon(
				"C:\\Users\\Angel_Santana\\Desktop\\FinalPSP2025UltimaVersion\\FinalPSP2025UltimaVersion\\Image\\enviar (2).png"));
		// Se crea un panel y se agregan los componentes de la interfaz del chat
		JPanel panel = new JPanel(); 
		panel.add(campoMensaje); 
		panel.add(botonEnviar); 
		
		// Se configura el diseño de la ventana
		frame.getContentPane().add(new JScrollPane(areaMensajes), BorderLayout.CENTER); 
																						
		frame.getContentPane().add(panel, BorderLayout.SOUTH); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		frame.pack(); 
		frame.setVisible(true); 
		
		// Acción del botón enviar: el servidor puede enviar mensajes a los clientes
		botonEnviar.addActionListener(e -> {
			String mensaje = "Angel: " + campoMensaje.getText(); 
			enviarMensajeATodos(mensaje); 
			campoMensaje.setText(""); 
		});

		// Se inicia el servidor en el puerto especificado
		try (ServerSocket serverSocket = new ServerSocket(PUERTO)) { // Se abre un socket en el puerto 12345
			// Se muestra un mensaje en la interfaz indicando que el servidor ha iniciado
			SwingUtilities.invokeLater(() -> areaMensajes.append("Servidor iniciado...\n"));
			while (true) { // Bucle infinito para aceptar múltiples conexiones de clientes
				// Se acepta una nueva conexión de un cliente
				Socket socket = serverSocket.accept();
				// Se muestra un mensaje en la interfaz indicando que un nuevo usuario se ha conectado
				SwingUtilities.invokeLater(() -> areaMensajes.append("Nuevo contacto conectado: " + socket + "\n"));
				// Se crea un hilo para manejar la comunicación con el cliente
				new HiloServidorChat(socket).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	// Método para enviar mensajes a todos los clientes conectados
	public static void enviarMensajeATodos(String mensaje) {
		synchronized (clientes) { // Se sincroniza el acceso a la lista de clientes para evitar errores de concurrencia
			for (PrintWriter cliente : clientes) { // Se recorre la lista de clientes conectados
				cliente.println(mensaje); // Se envía el mensaje a cada cliente
			}
		}
		// Se actualiza el área de mensajes en la interfaz gráfica del servidor
		SwingUtilities.invokeLater(() -> areaMensajes.append(mensaje + "\n"));
	}
	

	// Método para agregar un cliente a la lista de conexiones activas
	public static void agregarCliente(PrintWriter writer) {
		synchronized (clientes) { // Se sincroniza el acceso a la lista de clientes
			clientes.add(writer); // Se agrega el cliente a la lista
		}
	}
	

	// Método para eliminar un cliente cuando se desconecta
	public static void eliminarCliente(PrintWriter writer) {
		synchronized (clientes) { // Se sincroniza el acceso a la lista de clientes
			clientes.remove(writer); // Se elimina el cliente de la lista
		}
	}
}
