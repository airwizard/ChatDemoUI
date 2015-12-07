package moodle.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.MainActivity;
import com.easemob.chatuidemo.db.RegisterDao;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.RegisteredUser;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.exceptions.EaseMobException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import moodle.util.L;
import moodle.util.PreferenceUtils;

public class LoginActivity extends Activity implements View.OnClickListener {

	private EditText txt_UserName, txt_UserPW;
	private Button btn_Login;
	private RegisterDao registerDao;
	private boolean autoLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 如果用户名密码都有，直接进入主页面
		if (DemoHXSDKHelper.getInstance().isLogined()) {
			autoLogin = true;
			startActivity(new Intent(LoginActivity.this, MainActivity.class));

			return;
		}
		setContentView(R.layout.activity_moodle_login);
		initView();

		if (DemoApplication.getInstance().getUserName() != null) {
			txt_UserName.setText(DemoApplication.getInstance().getUserName());
		}

		doTrustToCertificates();
		CookieHandler.setDefault(new CookieManager());
		registerDao = new RegisterDao(this);
	}

	public void initView() {
		btn_Login = (Button) findViewById(R.id.btn_Login);
		txt_UserName = (EditText) findViewById(R.id.txt_UserName);
		txt_UserPW = (EditText) findViewById(R.id.txt_UserPW);

		// Register the Login button to click listener
		// Whenever the button is clicked, onClick is called
		btn_Login.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// btn_Login is the ID of Login button defined in the layout
		if (v.getId() == R.id.btn_Login) {
			String uname = txt_UserName.getText().toString();
			String upassword = txt_UserPW.getText().toString();
			connect(uname, upassword );
		}
	}

	public String ReadBufferedHTML(BufferedReader reader, char [] htmlBuffer, int bufSz) throws java.io.IOException
	{
		htmlBuffer[0] = '\0';
		int offset = 0;
		do {
			int cnt = reader.read(htmlBuffer, offset, bufSz - offset);
			if (cnt > 0) {
				offset += cnt;
			} else {
				break;
			}
		} while (true);
		return new String(htmlBuffer);
	}

	public String getMoodleFirstPage( String userName, String userPW )
	{
		HttpsURLConnection conn_portal = null;
		HttpURLConnection conn_moodle = null;
		final int HTML_BUFFER_SIZE = 2*1024*1024;
		char htmlBuffer[] = new char[HTML_BUFFER_SIZE];

		try {
			/////////////////////////////////// HKU portal //////////////////////////////////////
			URL url_portal = new
					URL("https://hkuportal.hku.hk/cas/login?service=http://moodle.hku.hk/login/index.php?authCAS=CAS&username="
					+ userName + "&password=" + userPW);
			conn_portal = (HttpsURLConnection) url_portal.openConnection();

			BufferedReader reader_portal = new BufferedReader(new InputStreamReader(conn_portal.getInputStream()));
			String HTMLSource = ReadBufferedHTML(reader_portal, htmlBuffer, HTML_BUFFER_SIZE);
			int ticketIDStartPosition = HTMLSource.indexOf("ticket=") + 7;
			String ticketID = HTMLSource.substring(ticketIDStartPosition, HTMLSource.indexOf("\";", ticketIDStartPosition));
			reader_portal.close();
			/////////////////////////////////// HKU portal //////////////////////////////////////

			/////////////////////////////////// Moodle //////////////////////////////////////
			URL url_moodle = new URL("http://moodle.hku.hk/login/index.php?authCAS=CAS&ticket=" + ticketID);
			conn_moodle = (HttpURLConnection) url_moodle.openConnection();
			conn_moodle.setInstanceFollowRedirects(true);

			BufferedReader reader_moodle = new BufferedReader(new InputStreamReader(conn_moodle.getInputStream()));
			HTMLSource = ReadBufferedHTML(reader_moodle, htmlBuffer, HTML_BUFFER_SIZE);
			reader_moodle.close();
			//L.e(HTMLSource);
			return HTMLSource;
			/////////////////////////////////// Moodle //////////////////////////////////////

		} catch(Exception e) {
			return "Fail to login";
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			if(conn_portal != null){
				conn_portal.disconnect();
			}
			if(conn_moodle != null){
				conn_moodle.disconnect();
			}
		}
	}

	// trusting all certificate
	public void doTrustToCertificates() {
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers()
					{
						return null;
					}
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
					{
					}
					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
					{
					}
				}
		};

		try {
			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void alert(String title, String mymessage){
		new AlertDialog.Builder(this)
				.setMessage(mymessage)
				.setTitle(title)
				.setCancelable(true)
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton){}
						}
				)
				.show();
	}

	public void parse_HTML_Source_and_Switch_Activity( String HTMLsource ){
		Pattern p_coursename = Pattern.compile("<h3 class=\"coursename\">.*?>(.*?)</a>");
		Matcher m_course = p_coursename.matcher(HTMLsource);

		ArrayList<String> cname = new ArrayList<String>();
		ArrayList<String> cteachers = new ArrayList<String>();

		while(m_course.find()){
			String course_name =  m_course.group(1);
			cname.add(course_name);
		}

		Pattern p_teachercandidates = Pattern.compile("<ul class=\"teachers\"><li>Teacher(.*?)</ul>");
		Matcher m_teachercandidates = p_teachercandidates.matcher(HTMLsource);

		while(m_teachercandidates.find()){
			String string_teachername = m_teachercandidates.group(1);
			int nameStartPosition = string_teachername.indexOf(">")+1;
			int nameEndPosition = string_teachername.indexOf("</a>");
			String teacher_name = string_teachername.substring(nameStartPosition, nameEndPosition);
			cteachers.add(teacher_name);
		}

/*		Intent intent = new Intent(getBaseContext(), CourseListActivity.class);
		intent.putStringArrayListExtra("CourseName", cname);
		intent.putStringArrayListExtra("Teachers", cteachers);
		startActivity(intent);*/
	}

	public void connect( final String userName, final String userPW ){
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		final ProgressDialog pdialog = new ProgressDialog(this);

		pdialog.setCancelable(false);
		pdialog.setMessage("Logging in ...");
		pdialog.show();

		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
			boolean success;
			String moodlePageContent;

			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				success = true;
				moodlePageContent = getMoodleFirstPage(userName, userPW);

				if( moodlePageContent.equals("Fail to login") )
					success = false;

				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				if (success) {
					if (registerDao.getregisteredUser(userName) != null) {
						//parse_HTML_Source_and_Switch_Activity( moodlePageContent );
						PreferenceUtils.setPrefString(LoginActivity.this, moodle.util.Constant.MOODLE_INFO, moodlePageContent);
						L.e("already registered!");
						login(userName,userPW);

					} else {
						register(userName, userPW);
						RegisteredUser registeredUser = new RegisteredUser();
						registeredUser.setRegisteredUserName(userName);
						registerDao.saveRegisteredUser(registeredUser);
					}

				} else {
					alert( "Error", "Fail to login" );
				}
				pdialog.hide();
			}

		}.execute("");
	}




	public void register(final String username, final String pwd) {
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			/*final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage(getResources().getString(R.string.Is_the_registered));
			pd.show();*/
			new Thread(new Runnable() {
				public void run() {
					try {
						// 调用sdk注册方法
						EMChatManager.getInstance().createAccountOnServer(username, pwd);
						runOnUiThread(new Runnable() {
							public void run() {
								/*if (!LoginActivity.this.isFinishing())
									pd.dismiss();*/
								// 保存用户名
								DemoApplication.getInstance().setUserName(username);
								L.e(getResources().getString(R.string.Registered_successfully));
								//Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
								login(username, pwd);
								//finish();
							}
						});
					} catch (final EaseMobException e) {
						runOnUiThread(new Runnable() {
							public void run() {
							/*	if (!LoginActivity.this.isFinishing())
									pd.dismiss();*/
								int errorCode=e.getErrorCode();
								if(errorCode== EMError.NONETWORK_ERROR){
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
								}else if(errorCode == EMError.USER_ALREADY_EXISTS){
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
								}else if(errorCode == EMError.UNAUTHORIZED){
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
								}else if(errorCode == EMError.ILLEGAL_USER_NAME){
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			}).start();

		}
	}




	/**
	 * 登录
	 *
	 * @param
	 */
	public void login(final String username, final String pwd) {

		final ProgressDialog pdialog = new ProgressDialog(this);

		pdialog.setCancelable(false);
		pdialog.setMessage("Logging in ...");
		pdialog.show();
		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(username, pwd, new EMCallBack() {

			@Override
			public void onSuccess() {

				// 登陆成功，保存用户名密码
				DemoApplication.getInstance().setUserName(username);
				DemoApplication.getInstance().setPassword(pwd);

				try {
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					// ** manually load all local groups and
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					// 处理好友和群组
					initializeContacts();
				} catch (Exception e) {
					e.printStackTrace();
					// 取好友或者群聊失败，不让进入主页面
					runOnUiThread(new Runnable() {
						public void run() {
							pdialog.dismiss();
							DemoHXSDKHelper.getInstance().logout(true,null);
							Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_LONG).show();
						}
					});
					return;
				}
				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
						DemoApplication.currentUserNick.trim());
				if (!updatenick) {
					Log.e("LoginActivity", "update current user nick fail");
				}
				if (!LoginActivity.this.isFinishing() && pdialog.isShowing()) {
					pdialog.dismiss();
				}
				// 进入主页面
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);

				finish();
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {

				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}



	private void initializeContacts() {
		Map<String, User> userlist = new HashMap<String, User>();
		// 添加user"申请与通知"
		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		String strChat = getResources().getString(
				R.string.Application_and_notify);
		newFriends.setNick(strChat);

		userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
		// 添加"群聊"
		User groupUser = new User();
		String strGroup = getResources().getString(R.string.group_chat);
		groupUser.setUsername(Constant.GROUP_USERNAME);
		groupUser.setNick(strGroup);
		groupUser.setHeader("");
		userlist.put(Constant.GROUP_USERNAME, groupUser);

		// 添加"Robot"
		User robotUser = new User();
		String strRobot = getResources().getString(R.string.robot_chat);
		robotUser.setUsername(Constant.CHAT_ROBOT);
		robotUser.setNick(strRobot);
		robotUser.setHeader("");
		userlist.put(Constant.CHAT_ROBOT, robotUser);

		// 存入内存
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(LoginActivity.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
	}
}
