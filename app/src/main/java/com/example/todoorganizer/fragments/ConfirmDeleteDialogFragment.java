package com.example.todoorganizer.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class ConfirmDeleteDialogFragment extends DialogFragment {

    private static final String ARG_TASK_ID = "taskId";

    public static ConfirmDeleteDialogFragment newInstance(String taskId) {
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes, delete the task
                        String taskId = getArguments().getString(ARG_TASK_ID);
                        ((HomeFragment) getParentFragment()).deleteTask(taskId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No, do nothing
                    }
                });

        return builder.create();
    }
}



