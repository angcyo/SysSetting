package com.zckj.connect;

import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * 连接服务器线程类
 *
 * @author Esa
 */
public class SocketConnect implements Runnable {
	private boolean isrun = true;
	private int status = 0;//0:空闲    2:已连接已登陆4:异常断开
	private int cmd = 0;//cmd=0：无需求 1:请求连接 2:断开连接 3重新连接 4收到心跳或数据
	private int hearttimeout = 300;

	private boolean isConnect = false;// 是否连接服务器
    private boolean isWrite = false;// 是否发送数据
    private static final Vector<byte[]> datas = new Vector<byte[]>();// 待发送数据队列
    private boolean issendname = true;
    private boolean issendtype = true;


	private SocketBase mSocket;// socket连接
    private WriteRunnable writeRunnable;// 发送数据线程
    private ReadRunnable readRunnable;
    private String ip = null;
    private int port = -1;
	private int max = 99;
    private String mac = "";
    java.net.InetAddress x;


	/**
	 * 创建连接
	 *
	 * @param callback 回调接口
	 */
    public SocketConnect(SocketCallback callback) {
        mSocket = new SocketBase(callback);// 创建socket连接
//        writeRunnable = new WriteRunnable();// 创建发送线程
        readRunnable = new ReadRunnable();
    }
     
    /*   public void run() {
        if (ip == null || port == -1) {
            throw new NullPointerException("not set address");
        }
        isConnect = true;
        while (isConnect) {
            synchronized (this) {
                try {
                    mSocket.connect(ip, port);// 连接服务器
                } catch (Exception e) {
                    try {
                        mSocket.disconnect();// 断开连接
                        this.wait(6000);
                        continue;
                    } catch (InterruptedException e1) {
                        continue;
                    }
                }
            }
            isWrite = true;// 设置可发送数据
            new Thread(writeRunnable).start();// 启动发送线程
            try {
                mSocket.read();// 获取数据
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                writeRunnable.stop();
                mSocket.disconnect();
            }
        }
    }*/

	public void setcmd(int value) {
		cmd = value;
	}

	public void settimeout(int value) {
		hearttimeout = value;
		cmd = 4;
    }

	//在run中,执行...根据当前状态,执行相应的操作
	private void process(int fstatus) {
		String s = fstatus + ",";
		String c = cmd + "";
		//	mSocket.SetStatus(s+c);

		switch (fstatus) {
			case 0://空闲，正在连接
				//status=0:空闲    2:已连接 3:已登陆 4:异常断开
				//cmd=0：无需求 1:请求连接 2:断开连接 3重新连接;
				clear();
				if (cmd == 1) {
					try {
						mSocket.connect(ip, port);// 连接服务器
						status = 2;
					} catch (Exception e) {
						System.out.println("连接失败");
					}
				}
				break;
			case 4://异常断开
				clear();
				switch (cmd) {
					case 2:
						try {
							mSocket.disconnect();
							status = 0;
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println("断开失败");
						}
						break;

					default:
						try {
							mSocket.disconnect();
							mSocket.connect(ip, port);// 连接服务器
							status = 2;
						} catch (Exception e) {
							System.out.println("连接失败");
						}
						break;
				}

				break;
			case 2://已连接已登陆
				switch (cmd) {
					case 2://手动断开
						try {
							mSocket.disconnect();
							status = 0;
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println("断开失败");
						}
						break;
					case 4:
						hearttimeout = 300;
						cmd = 0;
						break;

					default:
						if (datas.size() > 0) {
							byte[] buffer = datas.remove(0);// 获取一条发送数据
							try {
								boolean ret = writes(buffer);
								if (ret) {
									status = 2;
								} else {
									status = 4;
								}
								max = 99;
								// 发送数据
							} catch (Exception e) {
								status = 4;
							}
						}
						if (hearttimeout > 0) {
							hearttimeout -= 1;
							if (max > 0) {
								max -= 1;
							} else {
								//write("heart|1".getBytes(),true);
								write("heart|" + mac + "|\n\t", true);
								max = 99;
							}
						} else {
							status = 4;
							hearttimeout = 300;
						}
						//		 s=status+",";
						//   		 c=cmd+"";
						//   		 mSocket.SetStatus("执行完的值是:"+s+c);
						break;
				}
				break;

			default:
				break;
		}
	}


	//Scoket Run
	public void run() {
		try {
			x = java.net.InetAddress.getByName(ip);
//				Log.e("Robi", "--ip-1-" + ip);
			ip = x.getHostAddress();// 得到字符串形式的ip地址
//				Log.e("Robi", "--ip-2-" + ip);

		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e("Robi", "--run--" + e1.toString());
		}

		if (ip == null || port == -1) {
			throw new NullPointerException("not set address");
		}
		new Thread(readRunnable).start();//读取
		while (isrun) {
			synchronized (this) {
				process(status);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}


	/**
     * 关闭服务器连接
     */
    public synchronized void disconnect() {
        isConnect = false;
        this.notify();
        resetConnect();
    }

	/**
     * 重置连接
     */
    public void resetConnect() {
		//   writeRunnable.stop();// 发送停止信息
        mSocket.disconnect();
    }

	/**
     * 向发送线程写入发送数据
     */
   /* public void write(byte[] buffer,Boolean isheart) {
        //  writeRunnable.write(buffer);
    	if (isheart){
    		if (status==2){
    		 datas.add(buffer);// 将发送数据添加到发送队列       	
    		}
    	}else {
    		datas.add(buffer);// 将发送数据添加到发送队列 
		}    	         
    }*/
	public void write(String buffer, Boolean isheart) {
		//  writeRunnable.write(buffer);
		if (isheart) {
			if (status == 2) {
				datas.add(buffer.getBytes());// 将发送数据添加到发送队列
			}
		} else {
			datas.add(buffer.getBytes());// 将发送数据添加到发送队列
		}
	}

	public void clear() {
		datas.clear();
    }

	/**
	 * 设置IP和端口
	 *
	 * @param host
	 * @param port
	 */
    public void setRemoteAddress(String host, int port, String mac) {
        this.ip = host;
        this.port = port;
        this.mac = mac;
    }

	public void stop() {
		isrun = false;
		//isConnect = false;
    }

	/**
     * 发送数据
     */
    public boolean writes(byte[] buffer) {
        try {
            mSocket.write(buffer);
            return true;
        } catch (Exception e) {
            resetConnect();
        }
        return false;
    }


	/**
     * 发送线程
     *
     * @author Esa
     */
    private class WriteRunnable implements Runnable {

		@Override
        public void run() {
            System.out.println(">TCP发送线程开启<");
            while (isWrite) {
                synchronized (this) {
                    if (datas.size() <= 0) {
                        try {
                            this.wait();// 等待发送数据
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                    while (datas.size() > 0) {
                        byte[] buffer = datas.remove(0);// 获取一条发送数据
                        if (isWrite) {
                            writes(buffer);// 发送数据
                        } else {
                            this.notify();
                        }
                    }
                }
            }
            System.out.println(">TCP发送线程结束<");
        }

		/**
		 * 添加数据到发送队列
		 *
		 * @param buffer 数据字节
		 */
        public synchronized void write(byte[] buffer) {
            datas.add(buffer);// 将发送数据添加到发送队列
            this.notify();// 取消等待
        }


		public synchronized void stop() {
            isWrite = false;
            this.notify();
        }
    }

	private class ReadRunnable implements Runnable {

		@Override
        public void run() {
			while (isrun) {
				if (status == 2) {
					try {
						mSocket.read();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
