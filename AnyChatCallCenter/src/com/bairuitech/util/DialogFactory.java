package com.bairuitech.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bairuitech.anychat.AnyChatDefine;
import com.bairuitech.bussinesscenter.BussinessCenter;
import com.bairuitech.bussinesscenter.UserItem;
import com.bairuitech.callcenter.R;
import com.wandou.login.LoginActivity;
import com.wandou.login.RegisterToServer;

public class DialogFactory {
	private Activity mContext;
	private static Dialog mDialog;
	private LayoutInflater mLayoutInlater;
	private TextView mTextViewTitle;
	private EditText mEditIP;
	private EditText mEditPort;
	private ConfigEntity configEntity;

	public static int mCurrentDialogId = 0;

	public static final int DIALOGID_CALLING = 1;
	public static final int DIALOGID_REQUEST = 2;
	public static final int DIALOGID_RESUME = 3;
	public static final int DIALOGID_CALLRESUME = 4;
	public static final int DIALOGID_ENDCALL = 5;
	public static final int DIALOGID_EXIT = 6;
	public static final int DIALOGID_CONFIG = 7;
	public static final int DIALOGID_MEETING_INVITE = 8;
	public static final int DIALOGID_REGISTER = 9;
	public static final int DIALOG_SERCLOSE = 1;
	public static final int DIALOG_AGAINLOGIN = 2;
	public static final int DIALOG_NETCLOSE = 3;
	public static DialogFactory mDialogFactory = new DialogFactory();

	private DialogFactory() {
	}

	public static DialogFactory getDialogFactory() {
		if (mDialogFactory == null) {
			mDialogFactory = new DialogFactory();
		}
		return mDialogFactory;
	}

	/**
	 * 获取指定类型的对话框实例
	 * 
	 * @param dwDialogId
	 *            对话框类型
	 * @param object
	 *            对话框数据
	 * @param context
	 *            对话框位于的上下文
	 * @return 对话框实例
	 */
	public static Dialog getDialog(int dwDialogId, Object object,
			Activity context) {
		mDialogFactory.initDialog(dwDialogId, object, context);
		return mDialog;
	}

	public static int getCurrentDialogId() {
		return mCurrentDialogId;
	}

	public void initDialog(int dwDialogId, Object object, Activity context) {
		if (mContext != context) {
			mContext = context;
			mLayoutInlater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		mCurrentDialogId = dwDialogId;
		mDialog = new Dialog(mContext, R.style.CommonDialog);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
		switch (dwDialogId) {
		case DIALOGID_CALLING:
			initCallingDialog(mDialog, object);
			break;
		case DIALOGID_CALLRESUME:
			initCallResume(mDialog, object);
			break;
		case DIALOGID_ENDCALL:
			initEndSessionResumeDialg1(mDialog, object);
			break;
		case DIALOGID_EXIT:
			initQuitResumeDialg(mDialog);
			break;
		case DIALOGID_REQUEST:
			initCallReceivedDialg(mDialog, object);
			mDialog.setCancelable(false);
			break;
		case DIALOGID_RESUME:
			initResumeDialg(mDialog, object);
			break;
		case DIALOGID_CONFIG:
			initConfigDialog(mDialog, object);
			break;
		case DIALOGID_REGISTER:
			initRegisterDialog(mDialog,context);
		}

	}

	
	public static void releaseDialog() {
		mCurrentDialogId = 0;
		mDialog = null;
		mDialogFactory = null;
	}

	private void initRegisterDialog(final Dialog mDialog, final Activity context) {
		// TODO Auto-generated method stub
		View view = mLayoutInlater.inflate(R.layout.register_layout, null);
		 final EditText name;
		final EditText psd;
		final EditText repsd;
		  Button reset, register,cancel;
		
		name = (EditText)view.findViewById(R.id.name);
		psd = (EditText) view.findViewById(R.id.psd);
		repsd = (EditText)view.findViewById(R.id.repsd);
		reset = (Button) view.findViewById(R.id.reset);
		register = (Button)view.findViewById(R.id.register);
		cancel = (Button)view.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
			}
		});
		reset.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				name.setText("");
				psd.setText("");
				repsd.setText("");
			}
		});
		register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String checkResult = checkInfo();
				if (checkResult != null) {
					Toast.makeText(context, checkResult,1000).show();					
				} else {
					final Handler myHandler = new Handler() {
						public void handleMessage(Message msg) {
							boolean b = (Boolean) msg.obj;
							if (b) {
								Toast.makeText(context, "注册成功,返回登录",
										1000).show();
						 mDialog.dismiss();
							} else {
								Toast.makeText(context, "用户名已被注册,注册失败",1000).show();									
							}
						}
					};
					new Thread() {
						public void run() {
					boolean b = false;
							RegisterToServer registerToServer = new RegisterToServer();
							String response = registerToServer.doGet(name
									.getText().toString(), psd.getText()
									.toString());
							if ("true".equals(response)) {
								b = true;
							} else {
								b = false;
							}
							Message message = new Message();
							message.obj = b;
							myHandler.sendMessage(message);
						}
					}.start();
				}
			}
			private String checkInfo() {
				if (name.getText().toString() == null
						|| name.getText().toString().equals("")) {
					return "用户名不能为空";
				}
				if (psd.getText().toString().trim().length() < 3
						|| psd.getText().toString().trim().length() > 15) {
					return "密码只能在3-15个字符之间";
				}
				if (!psd.getText().toString().equals(repsd.getText().toString())) {
					return "前后两次密码不同，请重新输入";
				}
				return null;
			}
		});
		Log.e("TAG", "fuck");
		mDialog.setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT,	LayoutParams.WRAP_CONTENT));
	}
	
	
	
	// 初始化设置对话框
	public void initConfigDialog(final Dialog dialog, final Object object) {
		configEntity = (ConfigEntity) object;
		View view = mLayoutInlater.inflate(
				com.bairuitech.callcenter.R.layout.dialog_config, null);
		mEditPort = (EditText) view.findViewById(R.id.edit_serverport);
		mEditPort.setText(configEntity.port + "");
		mEditIP = (EditText) view.findViewById(R.id.edit_serverip);
		mEditIP.setText(configEntity.ip);
		ImageView imageView = (ImageView) view.findViewById(R.id.image_cancel);
		Button buttonR = (Button) view.findViewById(R.id.btn_resume);
		buttonR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String strServerIP = mEditIP.getText().toString();
				final String strPort = mEditPort.getText().toString();
				String strMessage = mContext
						.getString(R.string.str_serveripinput);
				if (strServerIP.length() == 0) {
					strMessage = mContext.getString(R.string.str_serveripinput);
					BaseMethod.showToast(strMessage, mContext);
					return;
				}
				if (strPort.length() == 0) {
					strMessage = mContext
							.getString(R.string.str_serverportinput);
					BaseMethod.showToast(strMessage, mContext);
					return;
				}
				configEntity.ip = strServerIP;
				configEntity.port = Integer.valueOf(strPort);
				ConfigService.SaveConfig(mContext,
						configEntity);
				dialog.dismiss();

			}
		});
		Button buttonC = (Button) view.findViewById(R.id.btn_cancel);
		buttonC.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});

		dialog.setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT,	LayoutParams.WRAP_CONTENT));
	}

	/***
	 * 初始化呼叫对话框
	 * @param dialog
	 * @param object
	 */
	public void initCallingDialog(final Dialog dialog, Object object) {
		final int userId = (Integer) object;
		View view = mLayoutInlater.inflate(R.layout.dialog_calling, null);
		ImageView buttonCancel = (ImageView) view.findViewById(R.id.btn_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BussinessCenter.VideoCallControl(
								AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY,
								userId,
								AnyChatDefine.BRAC_ERRORCODE_SESSION_QUIT,
								0, 0, "");
				dialog.dismiss();
			}
		});
		String strTitle = mContext.getString(R.string.str_calling);
		initDialogTitle(view, strTitle, userId);
		dialog.setContentView(view);
	}

	/***
	 * 初始化呼叫确认对话框
	 * 
	 * @param dialog
	 * @param object
	 */
	public void initCallResume(final Dialog dialog, Object object) {
		final int userId = (Integer) object;
		View view = mLayoutInlater.inflate(R.layout.dialog_call_resume, null);
		Button btnCall = (Button) view.findViewById(R.id.btn_call);
		btnCall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generate)d method stub
				BussinessCenter.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST, userId, 0,	0, 0, "");
				mDialog.dismiss();
			}
		});
		String strTitle = "";
		UserItem userItem = BussinessCenter.getBussinessCenter().getUserItemByUserId(userId);
		if (userItem != null)
			strTitle = "准备向" + userItem.getUserName() + "发起视频会话";
		initDialogTitle(view, strTitle);
		dialog.setContentView(view);
	}

	/***
	 * 初始化确认对话框
	 * 
	 * @param dialog
	 * @param object
	 */
	public void initResumeDialg(final Dialog dialog, final Object object) {
		final int dwTag = (Integer) object;
		View view = mLayoutInlater.inflate(R.layout.dialog_resume, null);
		Button buttonResume = (Button) view.findViewById(R.id.btn_resume);
		buttonResume.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent;
				switch (dwTag) {
				case DIALOG_AGAINLOGIN:
					mContext.stopService(new Intent(BaseConst.ACTION_BACK_SERVICE));
					intent = new Intent();
					intent.putExtra("INTENT", BaseConst.APP_EXIT);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(mContext, LoginActivity.class);
					mContext.startActivity(intent);
					break;
				case DIALOG_SERCLOSE:
					mContext.stopService(new Intent(BaseConst.ACTION_BACK_SERVICE));
					intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(mContext, LoginActivity.class);
					mContext.startActivity(intent);
					break;
				case DIALOG_NETCLOSE:
					Intent intentSetting = new Intent();
					intentSetting.setAction(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					mContext.startActivity(intentSetting);
					break;
				default:
					break;
				}
				dialog.dismiss();
			}
		});

		String strTitle = "";
		switch (dwTag) {
		case DIALOG_AGAINLOGIN:
			strTitle = mContext.getString(R.string.str_againlogin);
			break;
		case DIALOG_SERCLOSE:
			break;
		case DIALOG_NETCLOSE:
			strTitle = mContext.getString(R.string.str_networkcheck);
			break;
		default:
			break;
		}
		initDialogTitle(view, strTitle);
		dialog.setContentView(view);
	}

	/***
	 * 初始化接收到呼叫请求对话框
	 * 
	 * @param dialog
	 * @param object
	 */
	public void initCallReceivedDialg(final Dialog dialog, final Object object) {
		final int userId = (Integer) object;
		View view = mLayoutInlater.inflate(R.layout.dialog_requesting, null);
		ImageView buttonAccept = (ImageView) view.findViewById(R.id.btn_accept);
		ImageView buttonRefuse = (ImageView) view.findViewById(R.id.btn_refuse);
		
		// buttonAccept.setText(mContext.getString(R.string.str_resume));
		buttonAccept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generate)d method stub
				BussinessCenter.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY, userId,
						AnyChatDefine.BRAC_ERRORCODE_SUCCESS, 0, 0, "");
				dialog.dismiss();
			}
		});
		
		buttonRefuse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BussinessCenter.VideoCallControl(
						AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY, userId,
						AnyChatDefine.BRAC_ERRORCODE_SESSION_REFUSE, 0, 0,
						"");
				dialog.dismiss();
				BussinessCenter.sessionItem = null;
				BussinessCenter.getBussinessCenter().stopSessionMusic();
			}
		});
		UserItem userItem = BussinessCenter.getBussinessCenter().getUserItemByUserId(userId);
		String strTitle = "";
		if (userItem != null)
			strTitle = userItem.getUserName() + mContext.getString(R.string.sessioning_reqite);
		initDialogTitle(view, strTitle, userId);
		dialog.setContentView(view);
	}

	/***
	 * 初始化退出程序对话框
	 * @param dialog
	 */
	public void initQuitResumeDialg(final Dialog dialog) {
		View view = mLayoutInlater
				.inflate(R.layout.dialog_resumeorcancel, null);
		Button buttonQuit = (Button) view.findViewById(R.id.btn_resume);
		Button buttonCancel = (Button) view.findViewById(R.id.btn_cancel);
		buttonQuit.setText(mContext.getString(R.string.str_exit));
		buttonCancel.setText(mContext.getString(R.string.str_cancel));
		buttonQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generate)d method stub
				Intent intent = new Intent();
				intent.setAction(BaseConst.ACTION_BACK_SERVICE);
				mContext.stopService(intent);
				//停止坐姿检测service
				Intent serviceIntent = new Intent();
				serviceIntent.setAction(BaseConst.ACTION_BACK_SITTINGSERVICE);
				mContext.stopService(serviceIntent);
				
				intent = new Intent();
				intent.putExtra("INTENT", BaseConst.APP_EXIT);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(mContext, LoginActivity.class);
				mContext.startActivity(intent);
				dialog.dismiss();
			}
		});
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		String strTitle = mContext.getString(R.string.str_exitresume);
		initDialogTitle(view, strTitle);
		dialog.setContentView(view);
	}

	/***
	 * 初始化通话结束确认对话框
	 * 
	 * @param dialog
	 * @param object
	 */
	public void initEndSessionResumeDialg1(final Dialog dialog,
			final Object object) {
		final int userId = (Integer) object;
		View view = mLayoutInlater
				.inflate(R.layout.dialog_resumeorcancel, null);
		Button buttonPuase = (Button) view.findViewById(R.id.btn_resume);
		Button buttonCancel = (Button) view.findViewById(R.id.btn_cancel);
		buttonPuase.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generate)d method stub
				BussinessCenter.VideoCallControl(AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH, userId, 0,
						0, BussinessCenter.selfUserId, "");
				if (BussinessCenter.mContext != null)
					BussinessCenter.mContext.finish();
			}
		});
		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		String strTitle = mContext.getString(R.string.str_endsession);
		initDialogTitle(view, strTitle);
		dialog.setContentView(view);
	}

	private void initDialogTitle(View view, final String strTitle) {
		ImageView imageView = (ImageView) view.findViewById(R.id.image_cancel);
		mTextViewTitle = (TextView) view.findViewById(R.id.txt_dialog_prompt);
		mTextViewTitle.setTextColor(Color.BLUE);
		mTextViewTitle.setTextSize(20);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mCurrentDialogId == DIALOGID_CALLING) {

				} else if (mCurrentDialogId == DIALOGID_REQUEST) {

				}
				mDialog.dismiss();
			}
		});
		mTextViewTitle.setText(strTitle);
	}

	private void initDialogTitle(View view, final String strTitle,
			final int userId) {
		ImageView imageView = (ImageView) view.findViewById(R.id.image_cancel);
		mTextViewTitle = (TextView) view.findViewById(R.id.txt_dialog_prompt);
		mTextViewTitle.setTextColor(Color.BLUE);
		mTextViewTitle.setTextSize(20);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mCurrentDialogId == DIALOGID_CALLING) {
					BussinessCenter.VideoCallControl(
							AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY, userId,
							AnyChatDefine.BRAC_ERRORCODE_SESSION_QUIT, 0,
							0, "");
				} else if (mCurrentDialogId == DIALOGID_REQUEST) {
					BussinessCenter.VideoCallControl(
							AnyChatDefine.BRAC_VIDEOCALL_EVENT_REPLY, userId,
							AnyChatDefine.BRAC_ERRORCODE_SESSION_REFUSE, 0,
							0, "");
					BussinessCenter.sessionItem = null;
					BussinessCenter.getBussinessCenter().stopSessionMusic();
				}
				mDialog.dismiss();
			}
		});
		mTextViewTitle.setText(strTitle);
	}

}
