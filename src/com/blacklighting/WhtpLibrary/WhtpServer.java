package com.blacklighting.WhtpLibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class WhtpServer {

    ServerSocket server;
    int defaultPort = 8080;

    ReceiveFilePrgressListener receiveFileProcessListener = new ReceiveFilePrgressListener() {
        @Override
        public void updateReceiveFileProgressbar(int process) {
            // TODO Auto-generated method stub
            System.out.println("" + process + "MB");
        }
    };
    SentFileProgressListener sendFileProcessListener = new SentFileProgressListener() {
        @Override
        public void updateSentFileProgressBar(int process) {
            // TODO Auto-generated method stub
            System.out.println("" + process + "MB");
        }
    };

    public WhtpServer() throws IOException {
        server = new ServerSocket(defaultPort);

    }

    public WhtpServer(int defaultPort) throws IOException {
        server = new ServerSocket(defaultPort);
    }

    public Cilent getClient() throws IOException {
        Socket client = server.accept();
        return new Cilent(client);
    }

    protected void onClientContected(Socket client) {
        System.out.println(client.getInetAddress().getHostName());
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public void setReceiveFileProcessListener(
            ReceiveFilePrgressListener receiveFileProcessListener) {
        this.receiveFileProcessListener = receiveFileProcessListener;
    }

    public void setSendFileProcessListener(
            SentFileProgressListener sendFileProcessListener) {
        this.sendFileProcessListener = sendFileProcessListener;
    }

    class SentFileThread extends Thread {

        private BufferedInputStream fileIn;
        private int size = 0;
        BufferedOutputStream out;

        public SentFileThread(File data, BufferedOutputStream out)
                throws FileNotFoundException {
            fileIn = new BufferedInputStream(new FileInputStream(data));
            this.out = out;
        }

        @Override
        public void run() {
            super.run();

            try {

                byte[] temp = new byte[1024];

                for (;;) {

                    int length = fileIn.read(temp);
                    if (length == -1) {
                        break;
                    } else {
                        out.write(temp, 0, length);
                        size++;

                        sendFileProcessListener
                                .updateSentFileProgressBar(size);

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
        int size = 0;
        FileOutputStream fileOut;
        BufferedInputStream in;

        public ReceiveFileThread(File data, BufferedInputStream in)
                throws FileNotFoundException {
            fileOut = new FileOutputStream(data);
            this.in = in;
        }

        public ReceiveFileThread(String fileName, BufferedInputStream in)
                throws IOException {
            data = new File(fileName);
            fileOut = new FileOutputStream(data);
            this.in = in;
        }

        public ReceiveFileThread(FileOutputStream data, BufferedInputStream in) {
            fileOut = data;
            this.in = in;
        }

        @Override
        public void run() {
            super.run();

            try {
                while (in.available() == 0) {
                }

                byte[] temp = new byte[1024];

                for (;;) {

                    int length = in.read(temp);
                    if (length == -1) {
                        break;
                    } else {
                        fileOut.write(temp, 0, length);
                        size++;

                        receiveFileProcessListener
                                .updateReceiveFileProgressbar(size);

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public class Cilent {

        private Socket client;
        private BufferedInputStream in;
        private BufferedOutputStream out;
        private String requireLine;
        private Properties requireHeader = new Properties();
        String userAgent = "PC-DEMO";
        String contentName = "null";
        private String contentType;
        long contentLength = 0;

        public Cilent(Socket client) throws IOException {
            this.client = client;
            onClientContected(client);
            in = new BufferedInputStream(client.getInputStream());
            out = new BufferedOutputStream(client.getOutputStream());
            requireHeader.clear();
            receiveRequireLineAndHeader();
        }

        private void receiveRequireLineAndHeader() throws IOException {
            in = new BufferedInputStream(client.getInputStream());
            requireLine = readLine();

            for (String temp = readLine(); temp != null; temp = readLine()) {
                requireHeader.put(temp.substring(0, temp.indexOf(':')),
                        temp.substring(temp.indexOf(':') + 1));

            }
        }

        public String receiveStringRequirebody() throws IOException {
            int temp, length = 0;
            byte[] body = new byte[128];

            while ((temp = in.read()) != -1) {
                if (length == body.length) {
                    body = addCapacity(body);
                }
                body[length++] = (byte) temp;

            }
            return new String(body, 0, length);

        }

        public void receiveFileRequireBody(FileOutputStream dataOut) {
            new ReceiveFileThread(dataOut, in).start();
        }

        public void receiveFileRequireBody(String fileName) throws IOException {
            new ReceiveFileThread(fileName, in).start();
        }

        public void receiveFileRequireBody(File data) throws IOException {
            new ReceiveFileThread(data, in).start();
        }

        public void sendOKResponseLine() throws IOException {
            out = new BufferedOutputStream(client.getOutputStream());
            out.write(("Whtp/1.0 200 OK\nUser-Agent:" + userAgent
                    + "\nContent-Type:" + contentType + "\nContent-Length:"
                    + contentLength + "\nContent-Name:" + contentName + "\n\n")
                    .getBytes());
            out.flush();
        }

        public void sendRefuseResponseLine() throws IOException {
            out = new BufferedOutputStream(client.getOutputStream());
            out.write(new String("Whtp/1.0 400 Refuse\n").getBytes());
        }

        public void sendStringResponseBody(String body) throws IOException {
            out.write(("\n" + body).getBytes());
            out.flush();
            out.close();
        }

        public void sendFileAsResponseBody(File body) throws IOException {
            out.write("\n".getBytes());
            new SentFileThread(body, out).start();
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public void setContentName(String contentName) {
            this.contentName = contentName;
        }

        public void setContentLength(long contentLength) {
            this.contentLength = contentLength;
        }

        public String getRequireLine() {
            return requireLine;
        }

        public BufferedInputStream getIn() {
            return in;
        }

        public BufferedOutputStream getOut() {
            return out;
        }

        public Properties getRequireHeader() {
            return requireHeader;
        }

        public String getRequireUserAgent() {
            return requireHeader.getProperty("User-Agent");
        }

        public String getRequireContentType() {
            return requireHeader.getProperty("Content-Type");
        }

        public String getRequireContentLength() {
            return requireHeader.getProperty("Content-Length");
        }

        public String getCurrentClientName() {
            return client.getInetAddress().getHostName();
        }

        public String getRequireContentName() {
            return requireHeader.getProperty("Content-Name");
        }

        public void endResponse() throws IOException {
            out.flush();
            out.close();
        }

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

    }

    public void stopServer() throws IOException {
        server.close();
    }

    public interface ReceiveFilePrgressListener {

        void updateReceiveFileProgressbar(int process);
    }

    public interface SentFileProgressListener {

        void updateSentFileProgressBar(int process);
    }

}
