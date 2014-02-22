/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blacklighting;

import com.blacklighting.WhtpLibrary.WhtpClient;
import com.blacklighting.WhtpLibrary.WhtpServer;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.border.Border;

/**
 *
 * @author Happinesslight
 */
public class ClientDemo extends javax.swing.JFrame {

    String serverIp = "localhost";

    /**
     * Creates new form ClientDemo
     */
    public ClientDemo() {
        initComponents();
        try {
            localIp.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
        }
        setLocation(
                (Toolkit.getDefaultToolkit().getScreenSize().width - this
                .getSize().width) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - this
                .getSize().height) / 2);

    }

    public void runServer() {
        new Thread() {
            @Override
            public void run() {
                super.run(); //To change body of generated methods, choose Tools | Templates.
                try {
                    final WhtpServer server = new WhtpServer(8081);

                    while (true) {
                        try {
                            final WhtpServer.Cilent client = server.getClient();

                            String contentType = client.getRequireContentType();

                            if (contentType.matches("STRING/MESSAGE")) {
                                client.sendOKResponseLine();
                                JTextArea tempArea = stringMessageLogcat;
                                stringMessageLogcat.append(serverIp + "于" + getTime() + client
                                        .receiveStringRequirebody() + "\n");
                            } else if (contentType.startsWith("FILE/")) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run(); //To change body of generated methods, choose Tools | Templates.
                                        try {
                                            if (JOptionPane.showConfirmDialog(
                                                    ClientDemo.this,
                                                    client.getCurrentClientName() + "请求向你发送文件"
                                                    + client.getRequireContentName() + "是否接受？",
                                                    "文件传送提示", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                                                FileDialog dialog = new FileDialog(ClientDemo.this,
                                                        "保存文件", FileDialog.SAVE);
                                                dialog.setFile(client.getRequireContentName());
                                                dialog.setVisible(true);
                                                String data = dialog.getFile();
                                                if (data != null) {
                                                    client.sendOKResponseLine();
                                                    String length = client.getRequireContentLength();
                                                    final int fileSize = Integer.parseInt(length);
                                                    System.out.println(data+fileSize);
                                                    final JFrame processBarFrame = new JFrame("从"
                                                            + client.getCurrentClientName() + "接收文件");
                                                    Container content = processBarFrame
                                                            .getContentPane();
                                                    final JProgressBar receiveFileProgressBar = new JProgressBar(
                                                            0, fileSize);
                                                    receiveFileProgressBar.setValue(0);
                                                    receiveFileProgressBar.setStringPainted(true);
                                                    Border border = BorderFactory
                                                            .createTitledBorder("Receiving");
                                                    receiveFileProgressBar.setBorder(border);
                                                    content.add(receiveFileProgressBar,
                                                            BorderLayout.NORTH);
                                                    processBarFrame.setSize(300, 100);
                                                    processBarFrame.setLocation(
                                                            (Toolkit.getDefaultToolkit()
                                                            .getScreenSize().width - processBarFrame
                                                            .getSize().width) / 2, (Toolkit
                                                            .getDefaultToolkit()
                                                            .getScreenSize().height - processBarFrame
                                                            .getSize().height) / 2);
                                                    processBarFrame.setVisible(true);
                                                    final long time = System.currentTimeMillis();
                                                    server.setReceiveFileProcessListener(new WhtpServer.ReceiveFilePrgressListener() {
                                                        @Override
                                                        public void updateReceiveFileProgressbar(
                                                                int process) {
                                                            receiveFileProgressBar.setValue(process);
                                                            if (process == fileSize) {
                                                                processBarFrame.dispose();
                                                                fileLogcat.append(getTime()
                                                                        + "从"
                                                                        + client.getCurrentClientName()
                                                                        + "接收文件成功\n"
                                                                        + fileSize
                                                                        + "KB"
                                                                        + "耗时"
                                                                        + (System
                                                                        .currentTimeMillis() - time)
                                                                        / 1000 + "S\n");
                                                                System.out.println("从"
                                                                        + client.getCurrentClientName()
                                                                        + "接收文件成功\n"
                                                                        + fileSize
                                                                        + "KB"
                                                                        + "耗时"
                                                                        + (System
                                                                        .currentTimeMillis() - time)
                                                                        / 1000 + "S\n");
                                                            }
                                                        }
                                                    });

                                                    client.receiveFileRequireBody(dialog.getDirectory()
                                                            + data);
                                                } else {
                                                    client.sendRefuseResponseLine();
                                                    client.endResponse();
                                                }
                                            } else {
                                                client.sendRefuseResponseLine();
                                                client.endResponse();
                                            }
                                        } catch (IOException e) {
                                        }
                                    }
                                }.start();

                            }
                        } catch (IOException e) {
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClientDemo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        serverIpEdit = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        conformServerIpButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        localIp = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        stringMessageLogcat = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        sendStingMessageButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        stringMessageEdit = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileLogcat = new javax.swing.JTextArea();
        choiceFilebutton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CLIENT DEMO");
        setPreferredSize(new java.awt.Dimension(540, 489));

        titleLabel.setFont(new java.awt.Font("微软雅黑", 3, 18)); // NOI18N
        titleLabel.setText("DHCPAC LIBRARY CLIENT DEMO");
        titleLabel.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        serverIpEdit.setFont(new java.awt.Font("微软雅黑", 2, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("微软雅黑", 2, 14)); // NOI18N
        jLabel1.setText("服务器IP：");

        conformServerIpButton.setFont(new java.awt.Font("微软雅黑", 2, 14)); // NOI18N
        conformServerIpButton.setText("连接");
        conformServerIpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conformServerIpButtonActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("微软雅黑", 2, 14)); // NOI18N
        jLabel4.setText("本机：");

        localIp.setEditable(false);
        localIp.setColumns(2);
        localIp.setFont(new java.awt.Font("微软雅黑", 2, 14)); // NOI18N
        localIp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localIpActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("微软雅黑", 3, 14)); // NOI18N
        jLabel5.setText("Power By Blacklighting");

        jLabel6.setFont(new java.awt.Font("微软雅黑", 3, 14)); // NOI18N
        jLabel6.setText("Liu Yajun");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(serverIpEdit)
                            .addComponent(conformServerIpButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(localIp)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(10, 10, 10)
                .addComponent(serverIpEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(conformServerIpButton)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(localIp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(25, 25, 25))
        );

        jTabbedPane4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTabbedPane4.setFont(new java.awt.Font("微软雅黑", 1, 14)); // NOI18N

        stringMessageLogcat.setColumns(20);
        stringMessageLogcat.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        stringMessageLogcat.setLineWrap(true);
        stringMessageLogcat.setRows(5);
        jScrollPane2.setViewportView(stringMessageLogcat);

        jLabel2.setFont(new java.awt.Font("微软雅黑", 2, 14)); // NOI18N
        jLabel2.setText("文本日志");

        sendStingMessageButton.setFont(new java.awt.Font("微软雅黑", 2, 14)); // NOI18N
        sendStingMessageButton.setText("发送文字信息");
        sendStingMessageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendStingMessageButtonActionPerformed(evt);
            }
        });

        stringMessageEdit.setColumns(20);
        stringMessageEdit.setFont(new java.awt.Font("微软雅黑", 0, 14)); // NOI18N
        stringMessageEdit.setLineWrap(true);
        stringMessageEdit.setRows(5);
        jScrollPane3.setViewportView(stringMessageEdit);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendStingMessageButton)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendStingMessageButton))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("文本信息", jPanel2);

        jLabel3.setFont(new java.awt.Font("微软雅黑", 2, 14)); // NOI18N
        jLabel3.setText("文件日志");

        fileLogcat.setColumns(20);
        fileLogcat.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        fileLogcat.setLineWrap(true);
        fileLogcat.setRows(5);
        jScrollPane1.setViewportView(fileLogcat);

        choiceFilebutton.setFont(new java.awt.Font("微软雅黑", 2, 14)); // NOI18N
        choiceFilebutton.setText("选择文件...");
        choiceFilebutton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                choiceFilebuttonMouseClicked(evt);
            }
        });
        choiceFilebutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choiceFilebuttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(choiceFilebutton))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(choiceFilebutton)
                .addContainerGap())
        );

        jTabbedPane4.addTab("文件传送信息", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(6, 6, 6)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(jTabbedPane4, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void choiceFilebuttonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_choiceFilebuttonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_choiceFilebuttonMouseClicked

    private void sendStingMessageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendStingMessageButtonActionPerformed
        // TODO add your handling code here:
        new Thread() {
            @Override
            public void run() {
                super.run(); //To change body of generated methods, choose Tools | Templates.
                WhtpClient client = new WhtpClient("whtp://" + serverIp
                        + ":8080/WindowsClient");

                client.setContentType("STRING/MESSAGE");
                try {
                    client.openServer();
                    if (client.getResponseCode() == 200) {
                        String message = stringMessageEdit.getText();
                        stringMessageEdit.setText("");
                        client.sendStringRequireBody(message);
                        stringMessageLogcat.append("本机于" + getTime() + message + "\n");
                    } else {
                        stringMessageLogcat.append("对方拒绝接收信息");
                        JOptionPane.showMessageDialog(
                                ClientDemo.this,
                                "对方拒绝接收信息", "提示", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (UnknownHostException ex) {
                    JOptionPane.showMessageDialog(
                            ClientDemo.this,
                            "无法连接到" + serverIp, "错误", JOptionPane.ERROR);
                    Logger.getLogger(ClientDemo.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            ClientDemo.this,
                            "IO错误", "错误", JOptionPane.ERROR);
                    Logger.getLogger(ClientDemo.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    client.killClient();
                }
            }
        }.start();

    }//GEN-LAST:event_sendStingMessageButtonActionPerformed

    private void conformServerIpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conformServerIpButtonActionPerformed
        // TODO add your handling code here:
        serverIp = serverIpEdit.getText();
        System.out.println(serverIp);
        new Thread() {
            @Override
            public void run() {
                super.run(); //To change body of generated methods, choose Tools | Templates.
                WhtpClient client = new WhtpClient("whtp://" + serverIp
                        + ":8080/WindowsClient");

                client.setContentType("FIRSTCONTECT");
                try {
                    client.openServer();
                    client.sendStringRequireBody("FirstContect");
                    client.killClient();
                    JOptionPane.showMessageDialog(
                            ClientDemo.this,
                            "已经连接到" + serverIp, "提示", JOptionPane.INFORMATION_MESSAGE);
                } catch (UnknownHostException ex) {
                    JOptionPane.showMessageDialog(
                            ClientDemo.this,
                            "无法连接到" + serverIp, "错误", JOptionPane.ERROR);
                    Logger.getLogger(ClientDemo.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ClientDemo.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(
                            ClientDemo.this,
                            "IO错误", "错误", JOptionPane.ERROR);
                }
            }
        }.start();

    }//GEN-LAST:event_conformServerIpButtonActionPerformed

    private void choiceFilebuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choiceFilebuttonActionPerformed
        // TODO add your handling code here:
        FileDialog dialog = new FileDialog(ClientDemo.this,
                "选择文件", FileDialog.LOAD);
        dialog.setVisible(true);
        final String file = dialog.getFile();
        if (file != null) {
            File data = new File(dialog.getDirectory() + file);
            final int fileSize = (int) (data.length() / 1024);
            WhtpClient client = new WhtpClient("Whtp://" + serverIp + ":8080/test");

            client.setContentType("FILE/RMVB");
            client.setContentLength(fileSize);
            client.setContentName(file);
            try {
                client.openServer();

            } catch (UnknownHostException ex) {
                Logger.getLogger(ClientDemo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ClientDemo.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (client.getResponseCode() == 200) {
                try {
                    final JFrame processBarFrame = new JFrame("向"
                            + serverIp + "发送文件");
                    Container content = processBarFrame
                            .getContentPane();
                    final JProgressBar sendFileProgressBar = new JProgressBar(
                            0, fileSize);
                    sendFileProgressBar.setValue(0);
                    sendFileProgressBar.setStringPainted(true);
                    Border border = BorderFactory
                            .createTitledBorder("Sending");
                    sendFileProgressBar.setBorder(border);
                    content.add(sendFileProgressBar,
                            BorderLayout.NORTH);
                    processBarFrame.setSize(300, 100);
                    processBarFrame.setLocation(
                            (Toolkit.getDefaultToolkit()
                            .getScreenSize().width - this
                            .getSize().width) / 2, (Toolkit
                            .getDefaultToolkit()
                            .getScreenSize().height - this
                            .getSize().height) / 2);
                    processBarFrame.setVisible(true);
                    final long time = System.currentTimeMillis();
                    client.setSendFileProgressListener(new WhtpClient.SendFileProgressListener() {
                        @Override
                        public void updateSendFileProgressBar(int process) {
                            sendFileProgressBar.setValue(process);
                            if (process == fileSize) {
                                processBarFrame.dispose();
                                fileLogcat.append(getTime() + "向" + serverIp + "发送" + file + "成功\n大小：" + fileSize
                                        + "KB,耗时" + (System.currentTimeMillis() - time) / 1000 + "S");
                                System.out.println(getTime() + "向" + serverIp + "发送" + file + "成功\n大小：" + fileSize
                                        + "KB,耗时" + (System.currentTimeMillis() - time) / 1000 + "S");
                            }
                        }
                    });

                    client.sendFileAsrequireBody(data);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ClientDemo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_choiceFilebuttonActionPerformed

    private void localIpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localIpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_localIpActionPerformed
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientDemo.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClientDemo demo = new ClientDemo();
                demo.setVisible(true);
                demo.runServer();
            }
        });
    }

    private static String getTime() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)
                + "\n";

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton choiceFilebutton;
    private javax.swing.JButton conformServerIpButton;
    private javax.swing.JTextArea fileLogcat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTextField localIp;
    private javax.swing.JButton sendStingMessageButton;
    private javax.swing.JTextField serverIpEdit;
    private javax.swing.JTextArea stringMessageEdit;
    private javax.swing.JTextArea stringMessageLogcat;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
