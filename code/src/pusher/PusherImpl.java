package pusher;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;

import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;

import db.AccessDB;
import gui.GUIinit;
import ki.Intelligence;

public class PusherImpl implements ConnectionEventListener, PrivateChannelEventListener, PusherController {
	static final String api_channel = "private-channel";
	static final String api_event_recieve = "MoveToAgent";
	static final String api_event_send = "client-event";
	static final String api_messageKey = "message";
	static String api_id = "141721";
	static String api_secret = "84a5def6024476b74abd";
	static String api_key = "f8c067c77292524f786e";

	private final Pusher pusher;
	private final PrivateChannel channel;

	public Boolean ready = false;

	public boolean connectionError = false;

	public PusherImpl(Intelligence ki, char[] key, char[] secret, char[] appId, AccessDB db) {

		PusherImpl.api_key = new String(key);
		PusherImpl.api_secret = new String(secret);
		PusherImpl.api_id = new String(appId);

		/*------------------------------------*\
			Authorization
		\*------------------------------------*/
		PusherOptions opt = new PusherOptions();
		opt.setEncrypted(true);
		Authorizer auth = new Authorizer() {
			@Override
			public String authorize(String arg0, String arg1) throws AuthorizationFailureException {
				return "{\"auth\":\"" + api_key + ":" + authenticate() + "\"}";
			}
		};
		opt.setAuthorizer(auth);

		/*------------------------------------*\
			Pusher Initialisation & Connect
		\*------------------------------------*/
		pusher = new Pusher(api_key, opt);
		pusher.disconnect();
		pusher.connect(this);

		/*------------------------------------*\
			Subscribe to Channel
		\*------------------------------------*/
		channel = pusher.subscribePrivate(api_channel, new PrivateChannelEventListener() {
			@Override
			public void onAuthenticationFailure(String message, Exception e) {
				System.out.println("Authentifizierungsfehler: ");
				e.printStackTrace();
			}

			@Override
			public void onEvent(String arg0, String arg1, String arg2) {
				System.out.println(arg0 + arg1 + arg2);
			}

			@Override
			public void onSubscriptionSucceeded(String arg0) {
				System.out.println("Subscription von Channel '" + api_channel + "' erfolgreich.");
			}
		});

		/*------------------------------------*\
			Watch for Events  (Client & Server)
		\*------------------------------------*/
		channel.bind(api_event_send, new PrivateChannelEventListener() {
			@Override
			public void onAuthenticationFailure(String arg0, Exception arg1) {
			}

			@Override
			public void onEvent(String channel, String event, String data) {
				System.out.println("Daten im falschen Event (" + event + ") empfangen: " + data);
			}

			@Override
			public void onSubscriptionSucceeded(String arg0) {
			}
		});
		channel.bind(api_event_recieve, new PrivateChannelEventListener() {
			@Override
			public void onAuthenticationFailure(String arg0, Exception arg1) {
				arg1.printStackTrace();
			}

			@Override
			public void onEvent(String channel, String event, String data) {

				if (!ki.spielBeendet()) {

					System.out.println("Daten im Event (" + event + ") empfangen: " + data);

					try {
						JSONObject wrapper = new JSONObject(data);
						String[] content = wrapper.getString(api_messageKey).split(" # ");
						for (String i : content) {
							i.trim();
						}

						long dif = System.currentTimeMillis();
						ki.handle(content);
						if (ki.spielBeendet()) {
							System.out.println("Spiel beendet. Pusher wird geschlossen.");
							pusher.disconnect();
						} else {
							if (ki.unserZug()) {
								System.out.println("Wir sind dran!");
								int tmp = ki.getZug();
								send(tmp);
								System.out.print("Dauer des Zugs: ");
								System.out.print((System.currentTimeMillis() - dif));
								System.out.print("ms\n");
							} else
								System.out.println("Wir sind nicht dran!");
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else
					System.out.println("Spiel bereits beendet, trotzdem Daten empfangen: " + data);
			}

			@Override
			public void onSubscriptionSucceeded(String arg0) {
			}
		});

		/*------------------------------------*\
			Main Job while Connection not ready
		\*------------------------------------*/
		while (!connectionReady()) {
			if (connectionError) {
				break;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		if (connectionError) {
			GUIinit.errorPusher();
			GUIinit.btnStart.setEnabled(true);
			GUIinit.btnEnde.setVisible(false);
			System.out.println(">> Setze Datenbank zurück <<");
			db.cleanSatzUndZuege(GUIinit.satz_id);
		} else {
			GUIinit.btnStart.setEnabled(false);
			GUIinit.btnEnde.setVisible(true);
		}
	}

	public Boolean connectionReady() {
		return pusher.getConnection().getState().toString() == "CONNECTED";
	}

	@Override
	public void onAuthenticationFailure(String arg0, Exception arg1) {
		System.out.println(arg0 + arg1.toString());
	}

	@Override
	public void onConnectionStateChange(ConnectionStateChange arg0) {
		System.out.println(arg0.getCurrentState().toString());
	}

	@Override
	public void onError(String arg0, String arg1, Exception arg2) {
		try {
			connectionError = true;
			throw new Exception(">> Pusher Connection Error <<");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void onEvent(String arg0, String arg1, String arg2) {
		System.out.println(arg0 + arg1 + arg2);
	}

	@Override
	public void onSubscriptionSucceeded(String arg0) {
		System.out.println(arg0);
	}

	@Override
	public void recieve() {

	}

	@Override
	public void send(int move) {
		/*------------------------------------*\
			Send to Pusher Server
		\*------------------------------------*/
		channel.trigger(api_event_send, "{\"move\": \"" + move + "\"}");
		System.out.println("Daten im Channel '" + api_event_send + "' gesendet: " + move);
		// if (ki.checkIfBeendet())
		// pusher.disconnect();
	}

	private String authenticate() {
		SecretKey sK = new SecretKeySpec(api_secret.getBytes(), "HmacSHA256");
		Mac mac;
		StringBuffer sb = new StringBuffer();

		try {
			mac = Mac.getInstance("HmacSHA256");
			mac.init(sK);

			byte[] sign = mac.doFinal((pusher.getConnection().getSocketId() + ":" + api_channel).getBytes());
			for (int i = 0; i < sign.length; ++i) {
				sb.append(Integer.toHexString((sign[i] >> 4) & 0xf));
				sb.append(Integer.toHexString(sign[i] & 0xf));
			}
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

}