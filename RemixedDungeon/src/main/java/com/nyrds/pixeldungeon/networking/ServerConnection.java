package com.nyrds.pixeldungeon.networking;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ServerConnection extends Thread {
    private SSLSocket socket;

    private String tls_ver = "TLSv1.2"; //Android 4.1+ API 16+

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    public boolean is_disabled = true;
    private TimerTask hbTimerTask = new hbTimerTask(this);
    private Timer hbTimer = new Timer();

    private ConcurrentLinkedQueue<String> receivedMessages = new ConcurrentLinkedQueue<String>();

    private TimerTask disconnectTimerTask = new DisconnectTimerTask(this); //таймер отвала сервера
    private Timer disconnectTimer = new Timer();
    public long lastHBTime;

    private int port;
    private InetAddress addr;

    private String cert_path = null;
    private String cert_passw = "12345678";

    private KeyStore keyStore;

    public ServerConnection(String hostname, int port) {
        try {
            addr = InetAddress.getByName(hostname);
        }
        catch (Exception e)
        {
            Log.e("SClient", "hostname error: " + e);
        }
        this.port = port;

    }

    @Override
    public void run() {
        this.send("connection detected");
        this.disconnectTimer.schedule(this.disconnectTimerTask, 1000, 527); //установка таймера дисконнекта. Первая проверка через 1 сек, следующие через 0.527 сек
        this.hbTimer.schedule(hbTimerTask, 500, 500);
        try {
            while (!this.is_disabled) {
                String serverMessage = (String) objectInputStream.readObject();
                serverMessage = serverMessage.trim(); //обрезаем пробелы по краям
                String[] serverMessageCmd = serverMessage.split(" "); //разделение сообщения на команды

                if (serverMessageCmd[0].equals("connection") && serverMessageCmd.length == 2) //действия при ключевом слове connection и правильной длине (2)
                {
                    if (serverMessageCmd[1].equals("disabled")) //действие при втором слове disable и ключевом connection
                    {
                        this.stopConnection();
                    } else if (serverMessageCmd[1].equals("OK")) {
                        this.lastHBTime = System.currentTimeMillis() / 1000L;
                    }
                } else {
                    this.receivedMessages.add(serverMessage);
                }
            }
        } catch (Exception e) {
            stopConnection();
            Log.e("SClient", "mainThread error: " + e);
        }
    }

    public boolean loadCert(@NonNull String cert_path, @NonNull String cert_passw) {
        this.cert_path = cert_path;
        this.cert_passw = cert_passw;

        try {
            // Load the keystore
            this.keyStore = KeyStore.getInstance("PKCS12");
            this.keyStore.load(new FileInputStream(cert_path), cert_passw.toCharArray());
            return true;
        }
        catch (Exception e)
        {
            Log.e("SClient", "loadCert error: " + e);
        }
        return false;
    }

    public void send(String message) {
        try {
            objectOutputStream.writeObject(message);
        } catch (IOException ex) {
        }
    }

    public boolean getStatus() {
        return !is_disabled;
    }

    public int getToReceiveCount() {
        return this.receivedMessages.size();
    }

    public String getMessage() {
        return this.receivedMessages.poll();
    }

    public boolean startConnection() {
        if(is_disabled) {
            try {
                this.is_disabled = false;

                this.disconnectTimer = new Timer();
                this.hbTimer = new Timer();

                this.disconnectTimer.schedule(this.disconnectTimerTask, 1000, 527); //установка таймера дисконнекта. Первая проверка через 1 сек, следующие через 0.527 сек
                this.hbTimer.schedule(hbTimerTask, 500, 500);

                // Create a custom trust manager that accepts the server self-signed certificate
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                // Create the SSLContext for the SSLSocket to use
                SSLContext sslctx = SSLContext.getInstance(tls_ver);
                sslctx.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

                // Create SSLSocketFactory
                SSLSocketFactory socketFactory = sslctx.getSocketFactory();

                // Create socket using SSLSocketFactory
                SSLSocket l_socket = (SSLSocket) socketFactory.createSocket(addr, port);

                this.socket = l_socket;

                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                start();
                return true;
            }
            catch (Exception e)
            {
                stopConnection();
                Log.e("SClient", "startConnection error: " + e);
                return false;
            }
        }
        return false;
    }

    public boolean stopConnection() {
        if(!is_disabled) {
            this.is_disabled = true;

            this.disconnectTimer.cancel();
            this.hbTimer.cancel();
            this.disconnectTimer.purge();
            this.hbTimer.purge();
            this.disconnectTimer = null;
            this.hbTimer = null;
            return true;
        }
        return false;
    }
}
