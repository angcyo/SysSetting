package com.zckj.connect;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Button btnStart, btnStop, btnBind, btnUnBind, btnconnect, btndisconnect, btnsend, btnsetuser, btninstall, btnuninstall, btnsetclienttype;
	Intent intent;
	private IService mybinder;
	private myconn testconn;
	private String recString;
	private TextView recvText;
	private EditText edtip, edtuser, edtmsg;
	public static Handler handler;
	private ProgressBar pb;
	private int i = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		intent = new Intent(getApplicationContext(), ServiceActivity.class);

		btnStart = (Button) findViewById(R.id.button1);
		btnStart.setOnClickListener(startbtn);

		btnStop = (Button) findViewById(R.id.button2);
		btnStop.setOnClickListener(stopbtn);

		btnBind = (Button) findViewById(R.id.button3);
		btnBind.setOnClickListener(bindbtn);

		btnUnBind = (Button) findViewById(R.id.button4);
		btnUnBind.setOnClickListener(unbindbtn);

		btnconnect = (Button) findViewById(R.id.button5);
		btnconnect.setOnClickListener(connectbtn);

		btndisconnect = (Button) findViewById(R.id.button6);
		btndisconnect.setOnClickListener(disconnectbtn);

		btnsend = (Button) findViewById(R.id.button7);
		btnsend.setOnClickListener(sendbtn);

		btnsetuser = (Button) findViewById(R.id.button8);
		btnsetuser.setOnClickListener(setuserbtn);

		btninstall = (Button) findViewById(R.id.button9);
		btninstall.setOnClickListener(installbtn);

		btnuninstall = (Button) findViewById(R.id.button10);
		btnuninstall.setOnClickListener(uninstallbtn);

		btnsetclienttype = (Button) findViewById(R.id.button11);
		btnsetclienttype.setOnClickListener(setclienttypebtn);

		//	pb = (ProgressBar) findViewById(R.id.progressBar1);
		//	pb.setMax(100);

		recvText = (TextView) findViewById(R.id.textView1);
		edtip = (EditText) findViewById(R.id.edtIP);
		edtmsg = (EditText) findViewById(R.id.edtmsg);
		edtuser = (EditText) findViewById(R.id.edtuser);


		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.arg1) {
					case 1:
						recvText.append("\n" + msg.obj.toString() );
						break;

					case 2:
						//pb.setProgress(msg.arg2);
						recvText.setText("下载百分比是:" + msg.arg2);
						//recvText.append("下载百分比是:"+msg.arg2+"\n");
						break;

					case 3:
						if (i < 60) {
							recvText.append("当前状态是:" + msg.obj.toString());
							i += 1;
						} else {
							recvText.setText("当前状态是:" + msg.obj.toString());
							i = 0;
						}
						break;
					default:
						break;
				}
				super.handleMessage(msg);
			}
		};

		testconn = new myconn();
		startService(intent);//开启服务
        /*Toast.makeText(this, "服务开始在后台运行", 0).show();
		finish();*/

	}


	class splashhandler implements Runnable {

		public void run() {
			mybinder.Connect(edtip.getText().toString(), 9925);
		}

	}

	@Override
	protected void onStart() {
		//startService(intent);
		bindService(intent, testconn, BIND_AUTO_CREATE);/**/

		super.onStart();

	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			unbindService(testconn);
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onDestroy();
	}

	private OnClickListener startbtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//使用方法
			//connect.write("msg|1".getBytes(),false);
			startService(intent);
//			bindService(intent, testconn, BIND_AUTO_CREATE);/**/

			goBack();
		}
	};


	private OnClickListener stopbtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//使用方法
			//connect.write("msg|1".getBytes(),false);
			try {
				mybinder.Disconnect();
//				unbindService(testconn);
				stopService(intent);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	};

	private OnClickListener bindbtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//使用方法
			//connect.write("msg|1".getBytes(),false);
			bindService(intent, testconn, BIND_AUTO_CREATE);
		}
	};

	private OnClickListener unbindbtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//使用方法
			//connect.write("msg|1".getBytes(),false);
			//bindService(intent, new myconn(),BIND_AUTO_CREATE);
			try {
				unbindService(testconn);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	};

	private OnClickListener connectbtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			mybinder.setusername(edtuser.getText().toString());
			mybinder.Connect(edtip.getText().toString(), 9925);
		}
	};

	private OnClickListener disconnectbtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//	mybinder.test("测试");
			mybinder.Disconnect();
		}
	};


	private OnClickListener setuserbtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//	mybinder.test("测试");
			mybinder.setusername(edtuser.getText().toString());
		}
	};


	private OnClickListener installbtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//	mybinder.test("测试");
			mybinder.installapk(edtmsg.getText().toString());
		}
	};

	private OnClickListener uninstallbtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//	mybinder.test("测试");
			mybinder.uninstallpackage(edtmsg.getText().toString());
		}
	};

	//
	private OnClickListener setclienttypebtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//	mybinder.test("测试");
			mybinder.setclienttype(edtmsg.getText().toString());
		}
	};


	private OnClickListener sendbtn = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//	mybinder.test("测试");
			//发送数据;
			//	edt_send.getText().toString().getBytes()
			mybinder.sendmsg(edtmsg.getText().toString());
		}
	};

	private class myconn implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.e("Robi", "service 已连接");
			mybinder = (IService) service;
			mybinder.setusername(edtuser.getText().toString());
			Handler x = new Handler();
//			x.postDelayed(new splashhandler(), 5000);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			//程序异常中断的时候会调用这个函数
			Log.e("Robi", "service 已断开");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	String TAG = MainActivity.class.getSimpleName();

	void goBack(){
		Log.e("Robi", "GoBack");

		moveTaskToBack(true);

		/*PackageManager pm = getPackageManager();
		ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_HOME), 0);
		ActivityInfo ai = homeInfo.activityInfo;
		Intent startIntent = new Intent(Intent.ACTION_MAIN);
		startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		startIntent.setComponent(new ComponentName(ai.packageName,ai.name));
		startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(startIntent);
		} catch (ActivityNotFoundException e) {
			Log.i(TAG,"not found Activity error="+e.getMessage());
		} catch (SecurityException e) {
			Log.i(TAG,"not found Activity error="+e.getMessage());
			Log.e(TAG,"Launcher does not have the permission to launch "+ startIntent
							+ ". Make sure to create a MAIN intent-filter for the corresponding activity "+ "or use the exported attribute for this activity.",
					e);
		}*/
	}

}
