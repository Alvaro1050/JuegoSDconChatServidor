/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import Modelo.Usuario;
import Modelo.listaUsuarios;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alvar
 */
public class Server implements Runnable {

    Socket cliente = null;
    boolean win1, win2;
    static int port = 8000;
    ArrayList<Usuario> usuarios;
    boolean status = true;
    ServerSocket servidor = null;
    ObjectInputStream flujoEntrada;
    ObjectOutputStream flujosalida;
    String mensaje = "";
    String chat = "";
    Gson gson = new Gson();
    boolean exists = false;
    int cantidadUsuarios = 0;

    public Server() {
        usuarios = new ArrayList<>();
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            servidor = new ServerSocket(port);
            System.out.println("Server online escuchando por el ouerto " + port);

            while (status) {
                System.out.println("Server esperando la solicitud....");
                cliente = servidor.accept();
                System.out.println("Se recibio la solicitud");
                cantidadUsuarios++;

                flujoEntrada = new ObjectInputStream(cliente.getInputStream());
                mensaje = (String) flujoEntrada.readObject();
                Usuario usu = gson.fromJson(mensaje, Usuario.class);
                System.out.println(mensaje);

                if (usu.getTiempo() != 0) {
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usu.getNombreUsuario().equals(usuarios.get(i).getNombreUsuario())) {
                            exists = true;
                            if (usu.getTiempo() > usuarios.get(i).getTiempo()) {
                                usuarios.set(i, usu);
                            }

                        }
                    }
                    if (!exists) {
                        usuarios.add(usu);
                    }

                }

                if (usu.getMensaje().equals("")) {
                    listaUsuarios ls = new listaUsuarios(chat, usuarios, cantidadUsuarios);
                    flujosalida = new ObjectOutputStream(cliente.getOutputStream());
                    flujosalida.writeObject(gson.toJson(ls));
                } else {
                    chat = chat + usu.getMensaje() + "\n";

                }
                System.out.println(mensaje);
                exists = false;

                //cliente.close();
            }
        } catch (IOException e) {
            System.out.println("Error" + e);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void desconectar() {
        status = false;
        cantidadUsuarios--;
        try {

            if (flujoEntrada != null) {
                flujoEntrada.close();
            }
            if (cliente != null) {
                cliente.close();

            }
            if (servidor != null) {
                servidor.close();
            }
        } catch (IOException e) {
            System.out.println("Error desconectado servidor");
        }
    }
}
