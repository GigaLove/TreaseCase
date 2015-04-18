package hit.treasure.activity;


import hit.socket.bean.ChatMessage;
import hit.socket.bean.MessageType;
import hit.socket.thread.ReceiveThread;
import hit.socket.thread.SendThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ClientActivity extends Activity {

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		
		// 获取界面控件
		initView();
		Log.d("socket", "init View");
		getServerSetting();	
		
		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.what == MessageType.RECEIVE_MSG) {
					historyText.append("\r\n" + msg.obj.toString());
				}
				
				historyScrollView.post(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						historyScrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});				
			}			
		};		
		
		serverThread = new Thread(new ServerAcceptThread());
		serverThread.start();
	}
	
	/**
	 * 初始化界面控件，设置按钮监听事件
	 */
	private void initView() {
		sendButton = (Button)findViewById(R.id.sendButton);
		historyText = (TextView)findViewById(R.id.historyText);
		sendEditText = (EditText)findViewById(R.id.sendEditText);
		historyScrollView = (ScrollView)findViewById(R.id.historyScrollView);
		
		sendButton.setOnClickListener(new OnClickListener() {			
			@SuppressLint("SimpleDateFormat")
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String dateStr = df.format(new Date());
					ChatMessage chatMsg = new ChatMessage(userName, 
							sendEditText.getText().toString(), dateStr);
					new Thread(new SendThread(IP, remotePort, chatMsg)).start();
					historyText.append("\n" + chatMsg);
					sendEditText.setText("");
					historyScrollView.post(new Runnable() {					
						@Override
						public void run() {
							// TODO Auto-generated method stub
							historyScrollView.fullScroll(ScrollView.FOCUS_DOWN);
						}
					});		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		initSettingLayout();
	}
	
	/**
	 * 初始化设置界面，添加按钮监听事件
	 */
	private void initSettingLayout() {
		settingLayout = (TableLayout) getLayoutInflater()
				.inflate(R.layout.setting, null);
		IPEditText = (EditText)settingLayout.findViewById(R.id.IPEditText);
		portEditText = (EditText)settingLayout.findViewById(R.id.portEditText);
		localPortEditText = (EditText)settingLayout.findViewById(
				R.id.localPortEditText);
		
		Builder builder = new AlertDialog.Builder(this).
				setTitle("服务器设置"). setView(settingLayout);
		
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int whichButton) {
				// 将设置信息存储到xml文件中
				writeConfiguration();
				serverThread.interrupt();
				serverThread = new Thread(new ServerAcceptThread());
				serverThread.start();
				Toast.makeText(ClientActivity.this, "设置完成，开始连接", 
						Toast.LENGTH_LONG).show();
			}
        });
		
	    builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog,int whichButton) {
	          }
	    });
	    
	    dialog = builder.create();
	}
	
	/**
	 * 通过SharedPreferences机制，向xml文件中写入内容
	 */
	private void writeConfiguration() {
		// 获得setting.xml文件的读写权限
		SharedPreferences shaPreferences = getSharedPreferences("setting", 
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = shaPreferences.edit();
		
		String IPStr = IPEditText.getText().toString();
		String portStr = portEditText.getText().toString();
		String localPortStr = localPortEditText.getText().toString();
		Log.d("socket" ,IPStr + portStr + localPortStr);
		
		if (IPStr.equals("") || portStr.equals("") || localPortStr.equals("")) {
			Toast.makeText(ClientActivity.this, "请设置有效端口及IP地址", 
					Toast.LENGTH_LONG).show();
			return;
		}
		// 写入数据，并提交
		try {
			remotePort = Integer.parseInt(portStr);
			localPort = Integer.parseInt(localPortStr);
			IP = IPStr;
			editor.putInt("localPort", localPort);
			editor.putString("IP", IPStr.trim());
			editor.putInt("port", remotePort);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
	}
	
	/**
	 * 读取xml配置，获得IP地址和端口号
	 */
	private void getServerSetting() {
		SharedPreferences sharedPreferences = getSharedPreferences("setting", 
				Activity.MODE_PRIVATE);
		IP = sharedPreferences.getString("IP", "127.0.0.1");
		remotePort = sharedPreferences.getInt("port", 5678);
		localPort = sharedPreferences.getInt("localPort", 5679);
		IPEditText.setText(IP);
		localPortEditText.setText("" + localPort);
		portEditText.setText("" + remotePort);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.client, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int itemID = item.getItemId(); 
		
		switch(itemID) {
		case R.id.action_settings:
			dialog.show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (ss != null) {
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ss = null;
		}
	}
	
	class ServerAcceptThread implements Runnable {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				ss = new ServerSocket(localPort);
				while(true) {
					Log.d("socket", "in server send thread");
					s = ss.accept();
					Log.d("socket", "socket connect successful");
					new Thread(new ReceiveThread(s, handler)).start();	
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}				
	}
	
	private Thread serverThread;
	private ScrollView historyScrollView;
	private ServerSocket ss;
	private AlertDialog dialog;
	private TableLayout settingLayout;
	private Button sendButton;
	private TextView historyText;
	private EditText sendEditText;
	private EditText IPEditText;
	private EditText portEditText;
	private EditText localPortEditText;
	private String IP;
	private int remotePort;
	private int localPort;
	private Handler handler;
	private Socket s;
	private String userName = "Bob";
}
