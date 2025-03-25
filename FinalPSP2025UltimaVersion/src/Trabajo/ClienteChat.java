package Trabajo;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;

//Clase ClienteChat que maneja la conexión y comunicación con el servidor
class ClienteChat implements Runnable {
	private Socket socket; // Socket para la conexión con el servidor
	private BufferedReader reader; // Lector para recibir mensajes del servidor
	private PrintWriter writer; // Escritor para enviar mensajes al servidor
	private String nickname; // Nombre del usuario en el chat
	private JTextArea areaMensajes; // Área de texto donde se mostrarán los mensajes recibidos

	// Constructor del cliente que establece la conexión con el servidor
	public ClienteChat(String servidor, int puerto, String nickname, JTextArea areaMensajes) {
		this.nickname = nickname; // Guarda el nombre del usuario
		this.areaMensajes = areaMensajes; // Asigna el área de mensajes
		try {
			// Se intenta conectar al servidor con la IP y el puerto especificados
			socket = new Socket(servidor, puerto);
			// Se configura para leer los mensajes entrantes del servidor
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// Se configura para enviar mensajes al servidor
			writer = new PrintWriter(socket.getOutputStream(), true);
			// Envía un mensaje notificando que el usuario se ha unido al chat
			writer.println(nickname + " se ha unido al chat");
			// Se inicia un hilo para recibir mensajes del servidor en segundo plano
			new Thread(this).start();
		} catch (IOException e) {
			// Si la conexión falla, muestra un mensaje de error y cierra la aplicación
			JOptionPane.showMessageDialog(null, "No se pudo conectar al servidor. Verifica la IP y el puerto.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	// Método para enviar un mensaje al servidor
	public void enviarMensaje(String mensaje) {
		if (writer != null) { // Verifica que el cliente esté conectado antes de enviar
			writer.println(nickname + ": " + mensaje); // Envía el mensaje al servidor con el formato "Usuario: Mensaje"
		} else {
			JOptionPane.showMessageDialog(null, "No hay conexión con el servidor.", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	// Método para cerrar la conexión con el servidor
	public void cerrarConexion() {
		try {
			if (writer != null)
				writer.close(); // Cierra el canal de escritura
			if (reader != null)
				reader.close(); // Cierra el canal de lectura
			if (socket != null)
				socket.close(); // Cierra el socket
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	// Método principal que inicia la interfaz gráfica del cliente
	public static void main(String[] args) {
		// Solicita al usuario la IP del servidor
		String servidor = JOptionPane.showInputDialog("Ingrese la IP del servidor:");
		if (servidor == null || servidor.trim().isEmpty()) { // Si no ingresa una IP válida, muestra un error y sale
			JOptionPane.showMessageDialog(null, "Debes ingresar una IP válida.", "ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		// Solicita el nombre del usuario
		String nickname = JOptionPane.showInputDialog("Ingrese su nombre de contacto:");
		if (nickname == null || nickname.trim().isEmpty()) { // Si no ingresa un nombre válido, muestra un error y sale
			JOptionPane.showMessageDialog(null, "Debes ingresar un nombre de contacto.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		// Se crea la ventana del chat
		JFrame frame = new JFrame("WhatsApp 2"); // Nombre de la ventana
		JTextArea areaMensajes = new JTextArea(20, 40); // Área de mensajes con 20 filas y 40 columnas
		areaMensajes.setEditable(false); // Evita que el usuario edite los mensajes recibidos
		JTextField campoMensaje = new JTextField(40); // Campo de entrada de texto
		JButton botonEnviar = new JButton(new ImageIcon("C:\\Users\\Angel_Santana\\Desktop\\FinalPSP2025UltimaVersion\\FinalPSP2025UltimaVersion\\Image\\enviar (2).png")); // Botón con icono
		// Se crea la instancia del cliente con los datos ingresados
		ClienteChat cliente = new ClienteChat(servidor, 12345, nickname, areaMensajes);
		// Evento para enviar el mensaje al presionar el botón
		botonEnviar.addActionListener(e -> {
			cliente.enviarMensaje(campoMensaje.getText()); // Envía el mensaje al servidor
			campoMensaje.setText(""); // Borra el campo de texto después de enviar
		});
		// Configuración de la ventana
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(areaMensajes), BorderLayout.CENTER);
		JPanel panel = new JPanel(); // Panel para la entrada de mensajes
		panel.add(campoMensaje);
		panel.add(botonEnviar);
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		// Cierra la conexión cuando se cierra la ventana
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				cliente.cerrarConexion(); // Cierra la conexión con el servidor antes de salir
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack(); // Ajusta el tamaño de la ventana
		frame.setVisible(true); // Muestra la ventana
	}

	@Override
	public void run() {
		try {
			String mensaje;
			while ((mensaje = reader.readLine()) != null) { // Mientras haya mensajes entrantes
				areaMensajes.append(mensaje + "\n"); // Muestra el mensaje en el área de chat
				areaMensajes.setCaretPosition(areaMensajes.getDocument().getLength()); // Desplaza automáticamente
																						// hacia abajo
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
