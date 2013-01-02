package com.android.xbmccontentcompare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class RemoteXbmcDialog extends DialogFragment {

	public interface NoticeDialogListener {
		public void onRemoteXbmcDialogPositiveClick(DialogFragment dialog, String ip,
				String port);
	}

	// Use this instance of the interface to deliver action events
	NoticeDialogListener mListener;

	// Override the Fragment.onAttach() method to instantiate the
	// NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (NoticeDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View remoteXbmcView = inflater
				.inflate(R.layout.dialog_xbmc_information, null); 
		builder.setView(remoteXbmcView);
		builder.setMessage("Remote XBMC Information");
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Send the positive button event back to the
				// host activity
		
				EditText ip_edit = (EditText) remoteXbmcView.findViewById(R.id.ip);
				String iptext = ip_edit.getText().toString();
				EditText port_edit = (EditText) remoteXbmcView.findViewById(R.id.port);
				String porttext = port_edit.getText().toString();
				mListener.onRemoteXbmcDialogPositiveClick(RemoteXbmcDialog.this, iptext,
						porttext);
			}
		}).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		return builder.create();
	}
}
