package moodle.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;

import com.easemob.chatuidemo.R;

public class NoticeDialog {

	public static void alert(Context context, String message) {
		alert(context, message, null);
	}

	public static void alert(Context context, String message,
			OnClickListener listener) {
		Resources r = context.getResources();
		AlertDialog.Builder dialog = new AlertDialog.Builder(context)
				// .setIcon(R.drawable.ic_launcher)
				.setTitle(r.getString(R.string.prompt)).setMessage(message)
				.setPositiveButton(r.getString(R.string.ok), listener);
		dialog.create().show();
	}
	
	public static void alert(Context context, String message,
			OnClickListener listener,boolean isCancel) {
		Resources r = context.getResources();
		AlertDialog.Builder dialog = new AlertDialog.Builder(context)
				// .setIcon(R.drawable.ic_launcher)
				.setTitle(r.getString(R.string.prompt)).setMessage(message)
				.setPositiveButton(r.getString(R.string.ok), listener).setCancelable(isCancel);
		dialog.create().show();
	}

	public static void confirm(Context context, String message,
			OnClickListener yesListener, OnClickListener cancelListener) {
		Resources r = context.getResources();
		new AlertDialog.Builder(context)
				.setTitle(r.getString(R.string.prompt))
				.setMessage(message)
				.setPositiveButton(r.getString(R.string.ok), yesListener)
				.setNegativeButton(r.getString(R.string.cancel), cancelListener)
				.show();
	}

}
