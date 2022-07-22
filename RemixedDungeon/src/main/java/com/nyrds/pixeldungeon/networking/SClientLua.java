//Class for easy pairing SClient with lua
//Please use this instead of SClient.java
//You can do everything with this server
//Sample code:
//Initializing
//client = new SClientLua("<server_ip>", <port>).connect();
//Sending messages
//client.sendMessage(input.getText().toString());
//Receiving messages (for example, you can do this for each frame in lua code)
//if (client.canReceive()) output.setText(output.getText().toString() + client.receiveMessage() + "\n");

//You can also use SClientLua.anunknowip and SClientLua.anunknownport to connect to http://www.anunknown.site/ (but only if you name is Bogdan Kiykov)

//Optimized for work with .NET Server (https://github.com/anunknowperson/Simple-server)

//Coded by Sergey Kiselev in 2020
//Licensed by MIT License.

//Класс для простой связки Lua и SClient.java
//Пожалуйста, используй это вместо SClient.java
//Ты можешь делать всё что угодно с этим клиентом. Всё в твоих руках.
//Примеры:
//Инициализация и подключение
//client = new SClientLua("<server_ip>", <port>).connect();
//Отправка сообщений
//client.sendMessage(input.getText().toString());
//Получение сообщений (for example, you can do this for each frame in lua code)
//if (client.canReceive()) output.setText(output.getText().toString() + client.receiveMessage() + "\n");

//Вы также можете использовать SClientLua.anunknowip и SClientLua.anunknownport, чтобы подключиться к http://www.anunknown.site/ (Но только если вы - Богдан Кийков))

//Оптимизированно для работы с сервером .NET (https://github.com/anunknowperson/Simple-server)

//Создано Сергеем Киселёвым в 2020
//Лицензировано лицензией MIT.

package com.nyrds.pixeldungeon.networking;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.nyrds.LuaInterface;

import java.util.concurrent.ConcurrentLinkedQueue;

@LuaInterface
public class SClientLua {
    private String hostname;
    private int port;
    private ServerConnection serverConnection;

    public SClientLua(String hostname, int port){
        this.hostname = hostname;
        this.port = port;

    }

    @LuaInterface
    public static SClientLua createNew(String hostname, int port){ return new SClientLua(hostname, port); } //Function for lua...

    public SClientLua connect(){ //Connect to server
        serverConnection = new ServerConnection(hostname, port);
        return this;
    }

    public boolean loadCert(@NonNull String cert_path, @NonNull String cert_passw){
        return serverConnection.loadCert(cert_path, cert_passw);
    }

    public void send(String message){
        serverConnection.send(message);
    }

    public boolean getStatus() {
        return serverConnection.getStatus();
    }

    public int getToReceiveCount() {
        return serverConnection.getToReceiveCount();
    }

    public String getMessage() {
        return serverConnection.getMessage();
    }

    public boolean startConnection(){
        return serverConnection.startConnection();
    }

    public boolean stopConnection(){
        return serverConnection.stopConnection();
    }
}
