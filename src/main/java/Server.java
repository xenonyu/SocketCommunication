import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String args[]){
        try {
            System.out.println("socket服务器已在运行。");
            ServerSocket serverSocket = new ServerSocket(1199);
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServerListen(socket)).start();
                new Thread(new ServerSend(socket)).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ServerListen implements Runnable{
    private Socket socket;

    ServerListen(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            while(true){
                System.out.println(objectInputStream.readObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}

class ServerSend implements Runnable{
    private Socket socket;


    ServerSend(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            while(true){
                System.out.println("please enter send message.");
                String string = scanner.nextLine();
                JSONObject object = new JSONObject();
                object.put("type", "chat");
                object.put("msg", string);
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}