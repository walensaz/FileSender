import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class Main2 {

    private static JFileChooser fileChooser;

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        int option = JOptionPane.showConfirmDialog(null, "Receive files?");
        if (option == 1) {
            String ip = JOptionPane.showInputDialog("Enter the ip of the person you're trying to connect to.");
            fileChooser = new JFileChooser("Choose file to send");
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.showDialog(null, "Yeeet");
            File[] filesChosen = fileChooser.getSelectedFiles();
            Socket socket = new Socket(ip.split(":")[0], Integer.parseInt(ip.split(":")[1]));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            for (File file : filesChosen) {
                System.out.println(file.getName());
                objectOutputStream.writeObject(file.getName().split("\\.")[0] + "_copy." + file.getName().split("\\.")[1]);
                objectOutputStream.writeObject(Files.readAllBytes(file.toPath()));
                objectOutputStream.flush();
            }
            objectOutputStream.writeObject(true);
            objectOutputStream.flush();
            System.out.println("Sent...");
            Thread.sleep(5000L);
        } else {
            Socket socket = null;
            ServerSocket serverSocket = new ServerSocket(5555);
            fileChooser = new JFileChooser("Choose Directory to save");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.showDialog(null, "accept");
            File directory = fileChooser.getSelectedFile();
            System.out.println(directory);
            while(socket == null) {
                System.out.println("Waiting...");
                socket = serverSocket.accept();
                Thread.sleep(500L);
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Thread.sleep(500L);
            Object object = new Object();
            HashMap<String, byte[]> files = new HashMap<>();
            while((object = objectInputStream.readObject()) != null) {
                if(object instanceof String) {
                    files.put((String) object, (byte[]) objectInputStream.readObject());
                    continue;
                }
                if(object instanceof Boolean) {
                    break;
                }
            }

            files.forEach((name, file) -> {
                try {
                    System.out.println(directory.getName() + "\\" + name);
                    Files.write(new File(directory + "\\" + name).toPath(), file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Done");
        }
    }
}
