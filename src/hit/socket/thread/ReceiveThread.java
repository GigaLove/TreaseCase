package hit.socket.thread;

import hit.socket.bean.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ReceiveThread implements Runnable {
	private Socket s;
	private Handler handler;
	private BufferedReader br;
	
	
	public ReceiveThread(Socket s, Handler handler) throws IOException{
		this.s = s;
		this.handler = handler;
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			String content = null;
			while((content = br.readLine()) != null) {
				Message msg = new Message();
				msg.what = MessageType.RECEIVE_MSG;
				msg.obj = content;
				Log.d("socket", "receive content" + content);
				handler.sendMessage(msg);				
			}
			br.close();
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
