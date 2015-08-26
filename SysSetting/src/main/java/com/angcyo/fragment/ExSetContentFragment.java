package com.angcyo.fragment;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.fragment.adapter.ExSetFilePathAdapter;
import com.angcyo.fragment.adapter.node.FileNote;
import com.angcyo.fragment.holder.IconTreeItemHolder;
import com.angcyo.fragment.port.OnTreeNodeClickListener;
import com.angcyo.fragment.task.AsyncGetFilePath;
import com.angcyo.syssetting.R;
import com.angcyo.util.Logger;
import com.angcyo.util.Setting;
import com.angcyo.util.Util;
import com.angcyo.util.XmlSetting;
import com.angcyo.util.file.FileUtil;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExSetContentFragment extends BaseFragment implements OnTreeNodeClickListener {

	public static final int STYLE_EX_DISPLAY = 0x003001;
	public static final int STYLE_EX_STORAGE = 0x003002;
	public static final int STYLE_EX_PASSWORD = 0x003003;
	public static final int STYLE_EX_UPDATE_PATH = 0x003004;
	public static final int STYLE_EX_CLEAR_FETE = 0x003005;

	public static final int ROTATE_0 = 1;
	public static final int ROTATE_90 = 2;
	public static final int ROTATE_180 = 3;
	public static final int ROTATE_270 = 4;

	private boolean isFirstRun = true;

	public ExSetContentFragment() {
		default_layout_style = STYLE_EX_DISPLAY;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		switch (style) {
			case STYLE_EX_DISPLAY:
				rootView = inflater.inflate(R.layout.layout_ex_display, container,
						false);
				initExDisplayLayout();
				break;
			case STYLE_EX_PASSWORD:
				rootView = inflater.inflate(R.layout.layout_ex_password, container,
						false);
				initExPassWordLayout();
				break;
			case STYLE_EX_STORAGE:
//				rootView = inflater.inflate(R.layout.layout_ex_storage, container,
//						false);
				rootView = inflater.inflate(R.layout.layout_update_path, container,
						false);
				initExStorageLayout();
				break;
			case STYLE_EX_UPDATE_PATH:
//				rootView = inflater.inflate(R.layout.layout_ex_update_path,
//						container, false);
				rootView = inflater.inflate(R.layout.layout_update_path,
						container, false);
				initExUpdatePathLayout();
				break;
			case STYLE_EX_CLEAR_FETE:
				rootView = inflater.inflate(R.layout.layout_ex_clear_fete,
						container, false);
				initExClearFeteLayout();
				break;
			default:
				rootView = inflater.inflate(R.layout.layout_ex_display, container,
						false);
				initExDisplayLayout();
				break;
		}
		return rootView;
	}

	void initExClearFeteLayout() {

	}

	RadioGroup rgRotate;

	void initExDisplayLayout() {
		rgRotate = (RadioGroup) rootView.findViewById(R.id.id_ex_rotate_radiogroup);
		rgRotate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.id_ex_rb_rotate_0:
						setRotate(ROTATE_0);
						break;
					case R.id.id_ex_rb_rotate_90:
						setRotate(ROTATE_90);
						break;
					case R.id.id_ex_rb_rotate_180:
						setRotate(ROTATE_180);
						break;
					case R.id.id_ex_rb_rotate_270:
						setRotate(ROTATE_270);
						break;
					default:
						break;
				}
			}
		});

		initExDisplayLayoutData();
	}

	void initExDisplayLayoutData() {
		int rotate = Setting.getRotate(context);
		switch (rotate) {
			case ROTATE_0:
				((RadioButton) rgRotate.findViewById(R.id.id_ex_rb_rotate_0)).setChecked(true);
				break;
			case ROTATE_90:
				((RadioButton) rgRotate.findViewById(R.id.id_ex_rb_rotate_90)).setChecked(true);
				break;
			case ROTATE_180:
				((RadioButton) rgRotate.findViewById(R.id.id_ex_rb_rotate_180)).setChecked(true);
				break;
			case ROTATE_270:
				((RadioButton) rgRotate.findViewById(R.id.id_ex_rb_rotate_270)).setChecked(true);
				break;
			default:
				break;
		}
		setRotate(rotate);
	}

	void setRotate(int rotate) {
//		Logger.e("准备旋转屏幕::" + rotate);

		try {
			int screenChange = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
			if (screenChange == 0) {
				Logger.e("已关闭重力感应");
			}
			if (screenChange == -1) {
				Logger.e("不支持重力感应");
			}
			if (screenChange != 1) {//自动打开重力感应
				Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1); //设置打开
			}
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}

		switch (rotate) {
			case ROTATE_0:
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				Setting.setRotate(context, ROTATE_0);
				XmlSetting.setXmlRotateAngle(ROTATE_0 + "");
				break;
			case ROTATE_90:
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				Setting.setRotate(context, ROTATE_90);
				XmlSetting.setXmlRotateAngle(ROTATE_90 + "");
				break;
			case ROTATE_180:
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				Setting.setRotate(context, ROTATE_180);
				XmlSetting.setXmlRotateAngle(ROTATE_180 + "");
				break;
			case ROTATE_270:
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				Setting.setRotate(context, ROTATE_270);
				XmlSetting.setXmlRotateAngle(ROTATE_270 + "");
				break;
			default:
				break;
		}
	}

	void initExPassWordLayout() {

	}

	TextView currentPathText;
	ListView filePathList;
	ProgressBar loadBar;
	Button btSave;

	/**
	 * 初始化 存储路径 布局
	 */
	void initExStorageLayout() {
        /*currentPathText = (TextView) rootView.findViewById(R.id.id_ex_current_path);
        filePathList = (ListView) rootView.findViewById(R.id.id_ex_file_path);
		loadBar = (ProgressBar) rootView.findViewById(R.id.id_ex_file_path_load_bar);
		btSave = (Button) rootView.findViewById(R.id.id_ex_file_path_save);
		listEmptyHeaderView = rootView.findViewById(R.id.id_layout_list_empty);

		currentPathText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentFilePath = FileUtil.getCurrentPathPrev(currentFilePath);
				updateCurrentFilePathText(currentFilePath);
				updateListViewData(currentFilePath);
			}
		});
		filePathList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Logger.e("position:::" + position + ":::id::::" + id);
				Logger.e("currentFilePath:::::" + currentFilePath);
				if (ExSetContentFragment.this.fileNote != null) {
					currentFilePath = ExSetContentFragment.this.fileNote.fileFolderPath.get(position);
					Logger.e("newFilePath:::::" + currentFilePath);
					updateCurrentFilePathText(currentFilePath);
					updateListViewData(currentFilePath);
				} else {
					setFocusToCurrentPath(currentPathText);//返回焦点...
				}
			}
		});
		btSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (style == STYLE_EX_STORAGE) {
					XmlSetting.setXmlStoragePath(currentPathText.getText().toString());
					Util.showPostMsg("已设置路径:" + currentPathText.getText().toString());
				} else if (style == STYLE_EX_UPDATE_PATH) {
					XmlSetting.setXxmlUpdatePath(currentPathText.getText().toString());
					Util.showPostMsg("已更新路径:" + currentPathText.getText().toString());
				}
			}
		});

		initExStorageLayoutData();*/

		initExUpdatePathLayout();
	}

	class SetFocusToPathView implements Runnable {
		@Override
		public void run() {
			setFocusToCurrentPath(currentPathText);
		}
	}

	void setFocusToCurrentPath(TextView currentPathText) {
		if (isFirstRun) {
			isFirstRun = false;
			return;
		}
		setFocus(currentPathText);
	}

	FileNote fileNote;
	String currentFilePath = "/";

	/**
	 * 初始化 存储设置 布局数据
	 */
	private void initExStorageLayoutData() {
		String path;
		if (style == STYLE_EX_STORAGE) {
			path = XmlSetting.getXmlStoragePath();
			if (Util.isEmpty(path)) {
				currentFilePath = FileUtil.getSDPath();
			} else {
				currentFilePath = path;
			}
		} else if (style == STYLE_EX_UPDATE_PATH) {
			path = XmlSetting.getXxmlUpdatePath();
			if (Util.isEmpty(path)) {
				currentFilePath = FileUtil.getSDPath();
			} else {
				currentFilePath = path;
			}
		}

//		FileNote fileNote = FileUtil.getFileNote(currentFilePath);
		updateCurrentFilePathText(currentFilePath);
		updateListViewData(currentFilePath);
	}

	void updateCurrentFilePathText(String filePath) {
		if (currentPathText != null)
			currentPathText.setText(filePath);
	}

	void updateListViewData(String filePath) {
		File temp = new File(filePath);
		if (!temp.canRead()) {
			updateListViewData(FileUtil.getCurrentPathPrev(filePath));
			updateCurrentFilePathText(FileUtil.getCurrentPathPrev(filePath));
			Util.showPostMsg(filePath + " 路径不可读");
			return;
		}

		if (loadBar != null)
			loadBar.setVisibility(View.VISIBLE);
		new AsyncGetFilePath() {
			@Override
			public void onGetFilePath(FileNote fileNote) {
				Logger.e(fileNote.currentFilePath + " 含文件夹数 " + fileNote.fileFolderPath.size());

				ExSetContentFragment.this.fileNote = fileNote;
				if (ExSetContentFragment.this.fileNote != null
						&& filePathList != null
						&& ExSetContentFragment.this.fileNote.fileFolderPath.size() > 0) {
					hideEmptyPathLayout();
					filePathList.setAdapter(new ExSetFilePathAdapter(context, ExSetContentFragment.this.fileNote));
				} else {
					Logger.e("返回焦点");
					showEmptyPathLayout();
					filePathList.setAdapter(null);
					setFocusToCurrentPath(currentPathText);//返回焦点...
				}
				if (loadBar != null)
					loadBar.setVisibility(View.INVISIBLE);
			}
		}.execute(filePath);
	}

	View listEmptyHeaderView;

	void showEmptyPathLayout() {
		listEmptyHeaderView.setVisibility(View.VISIBLE);
	}

	void hideEmptyPathLayout() {
		if (listEmptyHeaderView != null) {
			listEmptyHeaderView.setVisibility(View.GONE);
		}
	}

	private void setFocus(View v) {
//		v.setFocusable(true);
//		v.setFocusableInTouchMode(true);
		v.requestFocus();
//		v.requestFocusFromTouch();
	}

	RelativeLayout layoutRoot;
	AndroidTreeView treeView;
	TextView tvCurrentPath;
	String strCurrentPath = FileUtil.getSDPath();//
	TreeNode rootNode = TreeNode.root();
	Button btSaveUpdatePath;
	ProgressBar pbLoadView;

	void initExUpdatePathLayout() {
//		initExStorageLayout();

		layoutRoot = (RelativeLayout) rootView.findViewById(R.id.id_layout_update_path);
		tvCurrentPath = (TextView) rootView.findViewById(R.id.id_ex_current_path);
		btSaveUpdatePath = (Button) rootView.findViewById(R.id.id_ex_file_path_save);
		pbLoadView = (ProgressBar) rootView.findViewById(R.id.id_ex_file_path_load_bar);

		btSaveUpdatePath.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (style == STYLE_EX_STORAGE) {
					XmlSetting.setXmlStoragePath(tvCurrentPath.getText().toString());
					Util.showPostMsg("已设置路径:" + tvCurrentPath.getText().toString());
				} else if (style == STYLE_EX_UPDATE_PATH) {
					XmlSetting.setXxmlUpdatePath(tvCurrentPath.getText().toString());
					Util.showPostMsg("已更新路径:" + tvCurrentPath.getText().toString());
				}
			}
		});

		showTreeView();
		initExUpdatePathLayoutData();
	}

	private void showTreeView() {
//		if (treeView != null){
//			layoutRoot.removeView(treeView.getView());
//		}
		treeView = new AndroidTreeView(context, rootNode);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutRoot.addView(treeView.getView(), params);
	}

	private void initExUpdatePathLayoutData() {
		String path;
		if (style == STYLE_EX_STORAGE) {
			path = XmlSetting.getXmlStoragePath();
			if (Util.isEmpty(path)) {
				strCurrentPath = "/";
			} else {
				strCurrentPath = path;
			}
		} else if (style == STYLE_EX_UPDATE_PATH) {
			path = XmlSetting.getXxmlUpdatePath();
			if (Util.isEmpty(path)) {
				strCurrentPath = FileUtil.getSDPath();
			} else {
				strCurrentPath = path;
			}
		}

		setUpdateCurrentFilePathText(strCurrentPath);
		setUpdateTreeNodeData(null, strCurrentPath);
		expandTreeViewToPath(strCurrentPath);
	}

	Handler handler = new Handler();

	/**
	 * 依次展开路径
	 *
	 * @param strCurrentPath
	 */
	private void expandTreeViewToPath(final String strCurrentPath) {
		if (Util.isEmpty(strCurrentPath)) {
			return;
		}

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
                /*List<TreeNode> lists = rootNode.getChildren();//所有node
                String[] strs = strCurrentPath.split("/");
				List<String> listLevelPath = new ArrayList<String>();//需要匹配路径
				for (int i = 0; i < strs.length; i++) {
					if (i <= 0) {
						listLevelPath.add("");
					} else {
						listLevelPath.add(listLevelPath.get(i - 1) + "/" + strs[i]);
					}
				}

				int count = 0;
				String path;
				for (TreeNode node : lists) {
					IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) node.getValue();

					int i = count + 1;
					if (i >= listLevelPath.size()) {
						return;
					}
					path = listLevelPath.get(i);
					if (item.fileNote.fileFolderPath.get(item.index) == path) {
						treeView.expandNode(node);
					}

					count++;
				}*/
			}
		}, 300);
	}

	//获取当前路径的所有node
	List<TreeNode> getAllNodeFromCurrentPath(String path) {
		if (Util.isEmpty(path)) {
			return null;
		}

		File tempFile = new File(path);
		if (!tempFile.exists() || !tempFile.isDirectory() || !tempFile.canRead()) {
			Util.showPostMsg(path + " 无效的路径");
			return null;
		}

		FileNote fileNote = FileUtil.getFileNote(path);
		List<TreeNode> list = new ArrayList<>();

		if (fileNote != null && fileNote.fileFolderName.size() > 0) {
//			Logger.e("数量" + fileNote.fileFolderPath.size());
			int count = 0;
			for (String name : fileNote.fileFolderName) {
				TreeNode treeNode = createTreeNodeView(count, fileNote);//创建node
                List<File> listFolder = FileUtil.listFolder(fileNote.fileFolderPath.get(count));//判断文件夹是否含有子文件夹
				if (listFolder == null || listFolder.size() < 1) {
					((IconTreeItemHolder.IconTreeItem) treeNode.getValue()).hasChildFolder = false;
                } else {
//                    Logger.e("路径" + fileNote.fileFolderPath.get(count) + "  含有子文件夹" + listFolder.size());
					((IconTreeItemHolder.IconTreeItem) treeNode.getValue()).hasChildFolder = true;
                }
				list.add(treeNode);
				count++;
			}
		}
		return list;
	}

	//创建一个tree Item
	TreeNode createTreeNodeView(int index, FileNote fileNote) {
		TreeNode node = new TreeNode(new IconTreeItemHolder.IconTreeItem(index, fileNote))
				.setViewHolder(new IconTreeItemHolder(context, this));
		return node;
	}

	@Override
	public void onTreeItemClick(TreeNode node, IconTreeItemHolder.IconTreeItem item) {
		if (!node.isExpanded()) {
			treeView.expandNode(node);//展开node
			if (!item.isLoad) {//如果没有装载过数据
				setUpdateTreeNodeData(node, item.fileNote.fileFolderPath.get(item.index));//更新数据
				item.isLoad = true;
			}
		} else {
			treeView.collapseNode(node);
		}

		setUpdateCurrentFilePathText(item.fileNote.fileFolderPath.get(item.index));
	}

	void setUpdateCurrentFilePathText(String path) {
		if (tvCurrentPath != null) {
			tvCurrentPath.setText(path);
		}
	}

	//将所有node,添加到指定的node中,如果不指定,表示添加到根node
	synchronized void setUpdateTreeNodeData(TreeNode node, String path) {
		final TreeNode tempNode;
		String tempPath;
		if (node == null) {
			tempNode = rootNode;
			tempPath = "/";
		} else {
			tempNode = node;
			tempPath = new String(path);
		}

		pbLoadView.setVisibility(View.VISIBLE);
		new AsyncTask<String, Void, List<TreeNode>>() {
			@Override
			protected List<TreeNode> doInBackground(String... params) {
				List<TreeNode> listNode = getAllNodeFromCurrentPath(params[0]);
				return listNode;
			}

			@Override
			protected void onPostExecute(List<TreeNode> treeNodes) {
				super.onPostExecute(treeNodes);
				if (treeNodes != null && treeNodes.size() > 0) {
					for (TreeNode node1 : treeNodes) {
						treeView.addNode(tempNode, node1);
					}
				}
				pbLoadView.setVisibility(View.INVISIBLE);
			}
		}.execute(tempPath);
	}

	void removeAllTreeNode(TreeNode node) {
		if (node != null) {
			List<TreeNode> nodeList = node.getChildren();
			if (nodeList == null || nodeList.size() <= 0) {
				return;
			}

			Logger.e("需要删除数量:" + nodeList.size());
			for (TreeNode node1 : nodeList) {
				node.deleteChild(node1);
			}
		}
	}
}
