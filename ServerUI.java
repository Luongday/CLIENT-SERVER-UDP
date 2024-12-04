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
    private static JFrame frame = new JFrame("Server - Hệ Thống Nhận File");
    private static JTextArea textArea = new JTextArea(20, 50);
    private static int port = 9999;
    
    private static void receiveData()
    {
            textArea.append("Server đang chạy trên port: " + port + "\n\n");
            while (true)
            {
                try (DatagramSocket serverSocket = new DatagramSocket(port)) {

                byte[] buffer = new byte[4096];

                textArea.append("[ Notification ] : Đang Chờ Nhận File \n");
                // Nhận thời gian thi
                DatagramPacket timePacket = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(timePacket);
                String examTime = new String(timePacket.getData(), 0, timePacket.getLength());
                textArea.append("Ca thi: " + examTime + "\n");
                String examTime_Folder = examTime.replace(":","H");
                File directory = new File(examTime_Folder);
                if (!directory.exists())
                {
                    directory.mkdir();
                }
                
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
                File file = new File(examTime_Folder+"/"+fileName);
                FileOutputStream fos = new FileOutputStream(file);
                long receivedBytes = 0;
                byte[] bufferr = new byte[65507];
                while (receivedBytes<fileSize)
                {
                    DatagramPacket fileDataPacket = new DatagramPacket(bufferr, bufferr.length);
                    serverSocket.receive(fileDataPacket);
                    fos.write(fileDataPacket.getData(), 0, fileDataPacket.getLength());
                    receivedBytes += fileDataPacket.getLength();
                    textArea.append("Đã nhận: " + receivedBytes + " bytes/"+fileSize+" bytes\n");
                }

                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                textArea.append("Đã nhận file lúc: " + timestamp + "\n");
                
                textArea.append("[ Success ] : Nhận File Thành Công !\n\n");
                fos.close();
                serverSocket.close();
                } catch (IOException e) {
                    textArea.append("Lỗi: " + e.getMessage() + "\n");
                }
            }
    }
    public static void main(String[] args) throws IOException {
        
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        new Thread(() -> receiveData()).start();
    }
}
