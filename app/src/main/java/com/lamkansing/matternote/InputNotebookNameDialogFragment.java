package com.lamkansing.matternote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class InputNotebookNameDialogFragment extends DialogFragment {
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(String notebookName);

    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {

            mListener = (NoticeDialogListener) activity;

        } catch (ClassCastException e) {

            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_notebookname, null);
        final EditText edittext = (EditText)dialogView.findViewById(R.id.editText_notebookname);

        builder.setView(dialogView)
                .setTitle(R.string.dialog_title_notebookname)
                .setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String input = edittext.getText().toString();
                        if (input != null && !input.equals(""))
                            mListener.onDialogPositiveClick(input);
                        else {
                            Toast.makeText(getActivity(), R.string.toast_empty_notebookname,Toast.LENGTH_LONG).show();
                            dismiss();
                        }

                    }
                })
                .setNegativeButton(R.string.button_cancell, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
