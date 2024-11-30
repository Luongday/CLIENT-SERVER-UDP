/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package NOPBAITHI;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author Admin
 */
public class ClientUI {
    public static void main(String[] args) {
        // Tạo giao diện
        JFrame frame = new JFrame("Client - Gửi File");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JTextField serverField = new JTextField("127.0.0.1");
        JTextField portField = new JTextField("9900");
        JTextArea logArea = new JTextArea(15, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        JButton chooseFileButton = new JButton("Chọn File");
        JButton sendFileButton = new JButton("Gửi File");
        JTextField filePathField = new JTextField();
        filePathField.setEditable(false);

        JPanel topPanel = new JPanel(new GridLayout(2, 2));
        topPanel.add(new JLabel("Server:"));
        topPanel.add(serverField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filePathField, BorderLayout.CENTER);
        centerPanel.add(chooseFileButton, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(sendFileButton, BorderLayout.SOUTH);
        frame.add(scrollPane, BorderLayout.EAST);

        frame.setVisible(true);

        // Action khi nhấn "Chọn File"
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
                logArea.append("Đã chọn file: " + selectedFile.getName() + "\n");
            }
        });

        // Action khi nhấn "Gửi File"
        sendFileButton.addActionListener(e -> {
            String serverAddress = serverField.getText();
            int port = Integer.parseInt(portField.getText());
            String filePath = filePathField.getText();

            if (filePath.isEmpty()) {
                logArea.append("Chưa chọn file để gửi!\n");
                return;
            }

            try (Socket socket = new Socket(serverAddress, port);
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 FileInputStream fis = new FileInputStream(filePath)) {

                File file = new File(filePath);
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());

                byte[] buffer = new byte[4096];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, read);
                }

                dos.flush();
                logArea.append("File đã được gửi thành công!\n");

                // Hiển thị thời gian gửi
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                logArea.append("Thời gian gửi file: " + timestamp + "\n");
            } catch (IOException ex) {
                logArea.append("Lỗi khi gửi file: " + ex.getMessage() + "\n");
            }
        });
    }
}
