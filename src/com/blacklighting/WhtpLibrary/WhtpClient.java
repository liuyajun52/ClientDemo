package com.blacklighting.WhtpLibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WhtpClient {

    Socket server;
    String url;
    String ip;
    int port;
    String contralString;
    BufferedInputStream in;
    BufferedOutputStream out;
    String headersLine;
    int responseCode;
    String contentType;
    Properties header = new Properties();
    String userAgent = "PC-DEMO";
    String connectionStates = "closed";
    String contentName = "null";
    long contentLength = 0;
    ReceiveFilePrgressListener receiveFileProgressListener = new ReceiveFilePrgressListener() {
        @Override
        public void updateReceiveFileProgressbar(int process) {
            System.out.println("" + process + "MB");
        }
    };
    SendFileProgressListener sendFileProgressListener = new SendFileProgressListener() {

        @Override
        public void updateSendFileProgressBar(int process) {
            System.out.println("" + process + "MB");
        }

    };

    public WhtpClient(String url) {
        this.url = url;
        initalArgs();
    }

    /**
     * ��ʼ������
     */
    protected void initalArgs() {
        int i, j;

        i = url.indexOf("//") + 2;
        j = url.indexOf(':', i);

        ip = url.substring(i, j);
        i = url.indexOf('/', j);
        port = Integer
                .parseInt(url.substring(j + 1, i == -1 ? url.length() : i));
        contralString = (i == -1 ? url.substring(i) : "");
        header.clear();
    }

    public void openServer() throws UnknownHostException, IOException {
        server = new Socket(ip, port);
        sendRequireLineAndHeader();
        receiveResponseLineAndHeader();
        // server.setSoTimeout(1000);
    }

    public void sendStringRequireBody(String body) throws IOException {
        out.write(body.getBytes("utf-8"));
        out.flush();
        out.close();
    }

    public void sendFileAsrequireBody(File data) throws FileNotFoundException {
        new SentFileThread(data).start();
    }

    protected void sendRequireLineAndHeader() throws IOException {
        out = new BufferedOutputStream(server.getOutputStream());
        out.write((contralString + "\nConnection:" + connectionStates
                + "\nUser-Agent:" + userAgent + "\nContent-Type:" + contentType + "\nContent-Length:" + contentLength + "\nContent-Name:" + contentName + "\n\n")
                .getBytes("utf-8"));
        out.flush();
    }

    protected void receiveResponseLineAndHeader() throws IOException {

        in = new BufferedInputStream(server.getInputStream());

        while (in.available() == 0) {
        }

        headersLine = readLine();
        int loc = headersLine.indexOf(' ') + 1; // code��ʼλ��
        responseCode = Integer.parseInt(headersLine.substring(loc,
                headersLine.indexOf(' ', loc)));

        if (responseCode != 200 || in.available() == 0) {
            return;
        }

        for (String temp = readLine(); temp != null; temp = readLine()) {
            header.put(temp.substring(0, temp.indexOf(':')),
                    temp.substring(temp.indexOf(':') + 1));

        }

    }

    public String receiveStringResponBody() throws IOException {
        int temp, length = 0;
        byte[] body = new byte[128];

        while ((temp = in.read()) != -1) {
            if (length == body.length) {
                body = addCapacity(body);
            }
            body[length++] = (byte) temp;

        }
        return new String(body, 0, length, "utf-8");
    }

    public void receiveFileResponseBody(String fileName) throws IOException {
        new ReceiveFileThread(fileName).start();
    }

    public BufferedOutputStream getOut() {
        return out;
    }

    public String getResponseHeader() {
        return header.toString();
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseContentType() {

        return header.getProperty("Content-Type");
    }

    public String getResponserContentEncoding() {
        return header.getProperty("Content-Encoding");
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setConnectionStates(String connectionStates) {
        this.connectionStates = connectionStates;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public void setReceiveFileProgressListener(ReceiveFilePrgressListener receiveFileProgressListener) {
        this.receiveFileProgressListener = receiveFileProgressListener;
    }

    public void setSendFileProgressListener(SendFileProgressListener sendFileProgressListener) {
        this.sendFileProgressListener = sendFileProgressListener;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    /**
     * �����ֽ����鳤�ȵĺ���
     *
     * @param rece
     * @return
     */
    private byte[] addCapacity(byte rece[]) {

        byte temp[] = new byte[rece.length + 128];

        System.arraycopy(rece, 0, temp, 0, rece.length);

        return temp;

    }

    private String readLine() throws IOException {
        int temp = 0, count = 0;
        byte[] message = new byte[128];
        while (true) {
            temp = in.read();

            if (temp == '\n' || temp == -1) {
                break;
            }

            if (count == message.length) {
                message = addCapacity(message);
            }

            message[count++] = (byte) temp;

        }

        if (count == 0) {
            return null;
        }

        return new String(message, 0, count, "utf-8");
    }

    class SentFileThread extends Thread {

        File data;
        int size = 0;
        FileInputStream fileIn;

        public SentFileThread(File data) throws FileNotFoundException {
            this.data = data;
            fileIn = new FileInputStream(data);
        }

        @Override
        public void run() {
            super.run();
            try {
                if (server.isClosed()) {
                    throw new IOException();
                }
                out = new BufferedOutputStream(server.getOutputStream());
                byte[] temp = new byte[1024];

                for (;;) {

                    int length = fileIn.read(temp);
                    if (length == -1) {
                        break;
                    } else {
                        out.write(temp, 0, length);
                        size++;

                        sendFileProgressListener.updateSendFileProgressBar(size);

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileIn.close();
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class ReceiveFileThread extends Thread {

        File data;
        long sizeEveryTime;
        BufferedOutputStream fileOut;
        int size = 0;
        long time = System.currentTimeMillis();

        public ReceiveFileThread(String fileName) throws IOException {
            data = new File(fileName);
            fileOut = new BufferedOutputStream(new FileOutputStream(data));
        }

        @Override
        public void run() {
            super.run();

            try {

                byte[] temp = new byte[1024];

                for (;;) {
                    int length = in.read(temp);
                    if (length == -1) {
                        break;
                    } else {
                        fileOut.write(temp, 0, length);

                        size++;
                        System.out.println(size + "KB");
                        receiveFileProgressListener.updateReceiveFileProgressbar(size);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    time -= System.currentTimeMillis();
                    fileOut.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void killClient() {
        try {
            in.close();
            out.close();
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(WhtpClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public interface ReceiveFilePrgressListener {

        void updateReceiveFileProgressbar(int process);
    }

    public interface SendFileProgressListener {

        void updateSendFileProgressBar(int process);
    }
}
