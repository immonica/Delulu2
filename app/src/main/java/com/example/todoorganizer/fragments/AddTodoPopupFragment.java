package com.example.todoorganizer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.example.todoorganizer.databinding.FragmentAddTodoPopupBinding;
import com.example.todoorganizer.utils.ToDoData;
import com.google.android.material.textfield.TextInputEditText;

public class AddTodoPopupFragment extends DialogFragment {

    private FragmentAddTodoPopupBinding binding;
    private OnDialogNextBtnClickListener listener;
    private ToDoData toDoData;

    public void setListener(OnDialogNextBtnClickListener listener) {
        this.listener = listener;
    }

    public interface OnDialogNextBtnClickListener {
        void saveTask(String todoTask, TextInputEditText todoEdit);

        void updateTask(ToDoData toDoData, TextInputEditText todoEdit);
    }

    public static final String TAG = "DialogFragment";

    public static AddTodoPopupFragment newInstance(String taskId, String task) {
        AddTodoPopupFragment fragment = new AddTodoPopupFragment();
        Bundle args = new Bundle();
        args.putString("taskId", taskId);
        args.putString("task", task);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddTodoPopupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            toDoData = new ToDoData(getArguments().getString("taskId"), getArguments().getString("task"));
            binding.todoEt.setText(toDoData.getTask());
        }

        binding.todoClose.setOnClickListener(v -> dismiss());

        binding.todoNextBtn.setOnClickListener(v -> {
            String todoTask = binding.todoEt.getText().toString();
            if (!todoTask.isEmpty()) {
                if (toDoData == null) {
                    listener.saveTask(todoTask, binding.todoEt);
                } else {
                    toDoData.setTask(todoTask);
                    listener.updateTask(toDoData, binding.todoEt);
                }
            }
        });
    }
}
