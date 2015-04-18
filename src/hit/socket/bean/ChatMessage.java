package hit.socket.bean;

public class ChatMessage {
	private String user;
	private String message;
	private String time;
		
	public ChatMessage(String user, String message, String time) {
		this.user = user;
		this.message = message;
		this.time = time;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return user + " " + time + "\n" + message; 
	}

}
