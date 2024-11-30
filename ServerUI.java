/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package NOPBAITHI;

/**
 *
 * @author Admin
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
public class ServerUI {
    public static void main(String[] args) {
        // Tạo giao diện
        JFrame frame = new JFrame("Server - Hệ Thống Nhận File");
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        String default_gateway = "127.0.0.1";
        int port = 9900;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            textArea.append("Server đang chạy trên port: " + port + "\n");

            while (true) {
                // Chờ kết nối từ client
                Socket socket = serverSocket.accept();
                textArea.append("Kết nối từ client: " + socket.getInetAddress() + "\n");

                // Xử lý dữ liệu từ client
                try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                     FileOutputStream fos = new FileOutputStream("received_file_" + System.currentTimeMillis() + ".txt")) {

                    // Nhận thông tin file
                    String fileName = dis.readUTF();
                    long fileSize = dis.readLong();
                    textArea.append("Đang nhận file: " + fileName + " (" + fileSize + " bytes)\n");

                    // Nhận dữ liệu file
                    byte[] buffer = new byte[4096];
                    int read;
                    long totalRead = 0;
                    while ((read = dis.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                        totalRead += read;
                        if (totalRead >= fileSize) break;
                    }

                    // Ghi lại thời gian nhận file
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    textArea.append("Đã nhận file lúc: " + timestamp + "\n\n");
                }

                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            textArea.append("Lỗi: " + e.getMessage() + "\n");
        }
    }
}
