import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    public static boolean connetionState = false;
    public static void main(String args[]){
        connect();
    }

    private static void connect() {
        try {
            socket = new Socket("localhost", 1199);
            connetionState = true;
            ObjectOutputStream objectOutputStream;
            ObjectInputStream objectInputStream;
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            new Thread(new ClientListen(socket, objectInputStream)).start();
            new Thread(new ClientSend(socket, objectOutputStream)).start();
            new Thread(new ClientHeart(socket, objectOutputStream)).start();
        } catch (IOException e) {
            e.printStackTrace();
            connetionState = false;
            reConnect();
        }
    }

    public static void reConnect(){
        while(!connetionState){
            System.out.println("Trying to reconnect.");
            connect();
            try{
                Thread.sleep(3000);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}


class ClientListen implements Runnable{
    private Socket socket;
    private ObjectInputStream objectInputStream;
    ClientListen(Socket socket, ObjectInputStream objectInputStream){
        this.socket = socket;
        this.objectInputStream = objectInputStream;
    }

    public void run() {
        try {
            while(true){
                System.out.println(objectInputStream.readObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
            try{
                socket.close();
                Client.connetionState = false;
                Client.reConnect();
            } catch (Exception ee){
                ee.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}

class ClientSend implements Runnable{
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    ClientSend(Socket socket, ObjectOutputStream objectOutputStream){
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
    }

    public void run() {
        try {
            while(true){
                System.out.println("请输入你想发送的信息：");
                Scanner scanner = new Scanner(System.in);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "chat");
                jsonObject.put("msg", scanner.nextLine());
                objectOutputStream.writeObject(jsonObject);
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try{
                socket.close();
                Client.connetionState = false;
                Client.reConnect();
            } catch (Exception ee){
                ee.printStackTrace();
            }
        }

    }
}

class ClientHeart implements Runnable{
    private Socket socket;
    private ObjectOutputStream objectOutputStream;

    ClientHeart(Socket socket, ObjectOutputStream objectOutputStream){
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
    }

    public void run() {
        try {
            System.out.println("thread heart beat has been started.");
            while(true){
                Thread.sleep(5000);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "heart");
                jsonObject.put("msg", "心跳包。");
                objectOutputStream.writeObject(jsonObject);
                objectOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try{
                socket.close();
                Client.connetionState = false;
                Client.reConnect();
            } catch (Exception ee){
                ee.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}