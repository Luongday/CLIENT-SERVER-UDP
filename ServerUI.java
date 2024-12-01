package NOPBAITHI;
import java.awt.BorderLayout;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerUI {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Server - Hệ Thống Nhận File");
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        int port = 9900;
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            textArea.append("Server đang chạy trên port: " + port + "\n");

        byte[] buffer = new byte[4096];

        // Nhận thời gian thi
        DatagramPacket timePacket = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(timePacket);
        String examTime = new String(timePacket.getData(), 0, timePacket.getLength());
        textArea.append("Ca thi: " + examTime + "\n");

        // Nhận tên file
        DatagramPacket fileNamePacket = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(fileNamePacket);
        String fileName = new String(fileNamePacket.getData(), 0, fileNamePacket.getLength());
        textArea.append("Tên file: " + fileName + "\n");

        // Nhận kích thước file
        DatagramPacket fileSizePacket = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(fileSizePacket);
        ByteArrayInputStream bais = new ByteArrayInputStream(fileSizePacket.getData());
        DataInputStream dis = new DataInputStream(bais);
        long fileSize = dis.readLong();
        textArea.append("Kích thước file: " + fileSize + " bytes\n");

        // Nhận dữ liệu file
        FileOutputStream fos = new FileOutputStream("received_" + fileName);
        long receivedBytes = 0;
        while (receivedBytes < fileSize) {
            DatagramPacket fileDataPacket = new DatagramPacket(buffer, buffer.length);
            serverSocket.receive(fileDataPacket);
            fos.write(fileDataPacket.getData(), 0, fileDataPacket.getLength());
            receivedBytes += fileDataPacket.getLength();
            textArea.append("Đã nhận: " + receivedBytes + " bytes\n");
        }
        
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        textArea.append("Đã nhận file lúc: " + timestamp + "\n\n");
        textArea.append("File đã được nhận thành công!");
        fos.close();
        serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            textArea.append("Lỗi: " + e.getMessage() + "\n");
        }
    }
}
