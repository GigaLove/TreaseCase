package hit.socket.thread;

import hit.socket.bean.ChatMessage;

import java.io.OutputStream;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.util.Log;

public class SendThread implements Runnable {
	private String IP;
	private int port;
	private ChatMessage message;
	
	public SendThread(String IP, int port, ChatMessage message) {
		this.IP = IP;
		this.port = port;
		this.message = message;
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Log.d("sockt", "in send thread");
			Socket s = new Socket(IP, port);
			Log.d("socket", "send thread connect successful");
			OutputStream os = s.getOutputStream();
			os.write(message.toString().getBytes("utf-8"));
			os.close();
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
