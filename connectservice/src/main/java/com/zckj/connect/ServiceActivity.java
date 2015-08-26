package com.zckj.connect;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;


public class ServiceActivity extends Service {
	private Thread connectThread;
	private boolean isdown = false;
	private boolean isthread;
	private String recvMessageClient = "";
	private static Process process;
	private String username;
	private String clitype = "android";
	Message message = null;
	public String tempDir;
	private String cmd_install = "pm install -r ";
	private String cmd_uninstall = "pm uninstall ";//静默卸载命令
	private String downhost;
	private static final Vector<dldata> dl = new Vector<dldata>();// 待下载数据队列
	private String macAddr = "";
	private Boolean isconnected;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("Robi", "service 创建");
		isthread = false;
		macAddr = getMacAddress();
		Log.e("Robi", "本地mac :" + macAddr);

		Thread t = new Thread(r);
		isdown = true;
		t.start();
	}


	public String getMacAddress() {
		try {
			return loadFileAsString("/sys/class/net/wlan0/address")
					.toUpperCase().substring(0, 17);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String loadFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	Runnable r = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			//打印新建线程信息
			while (isdown) {
				try {
					dldata downdata = dl.remove(0);

					String addr = downdata.getAddress();
					String port = downdata.getport();
					String name = downdata.getName();
					Boolean isInstall = downdata.getInstall();

					String url = "http://" + addr + ":" + port + "/" + name;
					Log.e("Robi", "Down Url" + url);
					download l = new download(url);
					String downloadfile = l.down2sd("downtemp/", name, l.new downhandler() {
						@Override
						public void setSize(int size) {
							message = new Message();
							message.arg1 = 2;
							message.arg2 = size;
							MainActivity.handler.sendMessage(message);
						}
					});
					if (downloadfile.length() <= 0) {
						connect.write(("msg|" + downloadfile + " download file\n\t"), false);
					} else if (isInstall) {
						String installpath = cmd_install + downloadfile;
						if (excuteSuCMD(installpath) != -1) {
							connect.write(("msg|" + downloadfile + " installed\n\t"), false);
						} else {
							connect.write(("msg|" + downloadfile + " not installed\n\t"), false);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					//connect.write(("msg|"+name+" not found\n\t"),false);
//					Log.e("Robi", "----" + e.toString());
				}
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	};

	SocketConnect connect = new SocketConnect(new SocketCallback() {
		@Override
        public void receive(byte[] buffer) {
			connect.settimeout(300);
			recvMessageClient = new String(buffer);

//			Log.e("Robi", "receive :" + recvMessageClient);

        	/*message = new Message();
            message.arg1 = 1;
			message.obj = recvMessageClient;
			MainActivity.handler.sendMessage(message);*/

			final String[] cmd = recvMessageClient.split("\\|");

			if (cmd[0].equals("isrun")) {
				try {
					if (isRunningApp(ServiceActivity.this, cmd[1])) {
						connect.write(("msg|" + macAddr + "|" + cmd[1] + " is running\n\t"), false);
					} else {
						connect.write(("msg|" + macAddr + "|" + cmd[1] + " is not run\n\t"), false);
					}
				} catch (Exception e) {
					connect.write(("msg|" + macAddr + "|" + cmd[1] + " not found\n\t"), false);
				}

			} else if (cmd[0].equals("start")) {
				try {
					Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(cmd[1]);
					startActivity(LaunchIntent);
					connect.write(("msg|" + macAddr + "|" + cmd[1] + " is starting\n\t"), false);
				} catch (Exception e) {
					// TODO: handle exception
					connect.write(("msg|" + macAddr + "|" + cmd[1] + " not found\n\t"), false);
				}

			} else if (cmd[0].equals("delfile")) {
				//删除文件
				//File file = new File("/mnt/sdcard/download/"+cmd[1]);
				try {
					if (cmd[1] != "") {
						File file = new File(cmd[1]);
						deleteFiles(file);
					}
				} catch (Exception e) {
					connect.write(("msg|" + macAddr + "|" + cmd[1] + " not found\n\t"), false);
				}

			} else if (cmd[0].equals("kill")) {
				try {
					kill(cmd[1]);
					connect.write(("msg|" + macAddr + "|" + cmd[1] + " is closeing\n\t"), false);
				} catch (Exception e) {
					// TODO: handle exception
					connect.write(("msg|" + macAddr + "|" + cmd[1] + " not found\n\t"), false);
				}
			} else if (cmd[0].equals("install")) {
				message = new Message();
				message.arg1 = 1;
				message.obj = recvMessageClient;
				MainActivity.handler.sendMessage(message);
				dldata dd = new dldata();
				dd.setName(cmd[1]);//第二个参数名称
				dd.setport(cmd[2]);//第三个参数端口
				dd.setAddress(downhost);
				dd.setInstall(true);
				dl.add(dd);
			} else if (cmd[0].equals("uninstall")) {
				try {
					if ("GetAll".equalsIgnoreCase(cmd[1])) {
						Log.e("Robi", "Get All");
						loadApps();
						StringBuilder strMsg = new StringBuilder("\n\r");
						for (ResolveInfo info : mApps) {
							strMsg.append(info.activityInfo.loadLabel(getPackageManager()) + "|" + info.activityInfo.packageName);
							strMsg.append("\n\r");
						}
						Log.e("Robi", "Get All \n" + strMsg);
						connect.writes(strMsg.toString().getBytes());

					} else {
						String uninstallpath = cmd_uninstall + cmd[1];
						if (excuteSuCMD(uninstallpath) != -1) {
							connect.write(("msg|" + macAddr + "|" + cmd[1] + " uninstalled\n\t"), false);
						} else {
							connect.write(("msg|" + macAddr + "|" + cmd[1] + " not uninstalled\n\t"), false);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					connect.write(("msg|" + macAddr + "|" + cmd[1] + " not found\n\t"), false);
				}
			} else if (cmd[0].equals("file")) {
				dldata dd = new dldata();
				dd.setName(cmd[1]);//下载地址
				dd.setport(cmd[2]);//文件名
				dd.setAddress(downhost);
				dd.setInstall(false);
				dl.add(dd);
			}
        }

		@Override
        public void disconnect() {
			isconnected = false;
			recvMessageClient = "网络断开";
			message = new Message();
			message.arg1 = 1;
			message.obj = recvMessageClient;
			MainActivity.handler.sendMessage(message);
        }

		@Override
        public void connected() {
			isconnected = true;
			recvMessageClient = "网络已成功连接";
			message = new Message();
			message.arg1 = 1;
			message.obj = recvMessageClient;
			MainActivity.handler.sendMessage(message);
			connect.write(("androiduser|" + macAddr + "|" + username + "\n\t"), false);

		}

		@Override
		public void Status(String i) {
			// TODO Auto-generated method stub
			message = new Message();
			message.arg1 = 3;
			message.obj = i;
			MainActivity.handler.sendMessage(message);
		}
    });

	public static void kill(String packageName) {
		initProcess();
		killProcess(packageName);
		close();
	}

	private static void initProcess() {
		if (process == null)
			try {
				process = Runtime.getRuntime().exec("su");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private static void killProcess(String packageName) {
		OutputStream out = process.getOutputStream();
		String cmd = "am force-stop " + packageName + " \n";
		try {
			out.write(cmd.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void close() {
		if (process != null)
			try {
				process.getOutputStream().close();
				process = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static boolean isRunningApp(Context context, String packageName) {
		boolean isAppRunning = false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
				isAppRunning = true;
				// find it, break
				break;
			}
		}
		return isAppRunning;
	}


	public void AssetsInstall(String apkname) {

		try {
			tempDir = Environment.getExternalStorageDirectory().getPath();
			InputStream is = getAssets().open(apkname);
			FileOutputStream fos = new FileOutputStream(tempDir
					+ "/" + apkname);

			byte[] buffer = new byte[1024];
			int readLen = 0;
			while ((readLen = is.read(buffer, 0, 1024)) >= 0) {
				fos.write(buffer, 0, readLen);
			}
			fos.close();
			is.close();
			//  String ins=	install(tempDir + "/app.apk");
			String cmd = cmd_install + tempDir + "/" + apkname;
			excuteSuCMD(cmd);
			//Toast.makeText(getApplicationContext(), ins, 0).show();
		} catch (Exception e) {
			//Toast.makeText(getApplicationContext(), "安装文件读取错误", 0).show();
		}
	}

	protected int excuteSuCMD(String cmd) {
		try {
			Process process = Runtime.getRuntime().exec("su");
			DataOutputStream dos = new DataOutputStream(
					(OutputStream) process.getOutputStream());
			// 部分手机Root之后Library path 丢失，导入library path可解决该问题
			dos.writeBytes((String) "export LD_LIBRARY_PATH=/vendor/lib:/system/lib\n");
			cmd = String.valueOf(cmd);
			dos.writeBytes((String) (cmd + "\n"));
			dos.flush();
			dos.writeBytes("exit\n");
			dos.flush();
			process.waitFor();
			int result = process.exitValue();
			return (Integer) result;
		} catch (Exception localException) {
			localException.printStackTrace();
			return -1;
		}
	}


	public void deleteFiles(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFiles(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			//
			Log.v("tag", "文件不存在");
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//isrun = false;
		connect.stop();
		super.onDestroy();
		Log.e("Robi", "服务销毁了");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.e("Robi", "服务绑定");
		return new myBinder();
	}

	public class myBinder extends Binder implements IService {
		@Override
		public void Connect(String host, int port) {
			// TODO Auto-generated method stub
			// x = java.net.InetAddress.getByName("qq38829979.eicp.net");
			// ip = x.getHostAddress();// 得到字符串形式的ip地址
			downhost = host;
			connect.setRemoteAddress(host, port, macAddr);
			if (!isthread) {
				isthread = true;
				connectThread = new Thread(connect);//启动网络连接
				connectThread.start();
			}
			connect.setcmd(1);
		}

		@Override
		public void Disconnect() {
			// TODO Auto-generated method stub
			connect.setcmd(2);
		}

		@Override
		public void sendmsg(String msg) {
			// TODO Auto-generated method stub
			connect.write("msg|" + macAddr + "|" + msg + "\n\t", false);
		}

		@Override
		public void setusername(String name) {
			// TODO Auto-generated method stub
			username = name;
			//connect.write(("androiduser|"+username+"\n\t"),false);
			connect.write(("androiduser|" + macAddr + "|" + username + "\n\t"), false);
		}

		@Override
		public void installapk(String apkname) {
			// TODO Auto-generated method stub
			AssetsInstall(apkname);
		}

		@Override
		public void uninstallpackage(String packagename) {
			// TODO Auto-generated method stub
			String uninstallpath = cmd_uninstall + packagename;
			excuteSuCMD(uninstallpath);
		}

		@Override
		public void setclienttype(String clienttype) {
			// TODO Auto-generated method stub
			clitype = clienttype;
			connect.write(("clienttype|" + macAddr + "|" + clienttype + "\n\t"), false);

		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e("Robi", "解除绑定服务");
		return super.onUnbind(intent);
	}

	public void change(String name) {
		//Toast.makeText(getApplicationContext(), name, 0).show();
		Log.e("Robi", "服务方法被调用");
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			//Log.v("tag", "网络已连接");
			//Toast.makeText(context, "网络已连接", 0).show();
			return true;
		}
		//Log.v("tag", "网络未连接");
		//Toast.makeText(context, "网络未连接", 0).show();
		return false;
	}

	List<ResolveInfo> mApps;

	public void loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
	}


}
