package com.example.todoorganizer.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.example.todoorganizer.R;
import com.example.todoorganizer.databinding.FragmentAddTodoPopupBinding;
import com.example.todoorganizer.utils.ToDoData;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddTodoPopupFragment extends DialogFragment {

    private FragmentAddTodoPopupBinding binding;
    private OnDialogNextBtnClickListener listener;
    private ToDoData toDoData;

    private String selectedDueDate = "";
    public void setListener(OnDialogNextBtnClickListener listener) {
        this.listener = listener;
    }

    // Define the interface here
    public interface OnDialogNextBtnClickListener {
        void saveTask(String todoTask, String dueDate, TextInputEditText todoEdit);
        void updateTask(ToDoData toDoData, TextInputEditText todoEdit);
    }

    public static final String TAG = "DialogFragment";

    public String getSelectedDueDate() {
        return selectedDueDate;
    }


    public static AddTodoPopupFragment newInstance(ToDoData toDoData) {
        AddTodoPopupFragment fragment = new AddTodoPopupFragment();
        Bundle args = new Bundle();
        args.putString("taskId", toDoData.getTaskId());
        args.putString("task", toDoData.getTask());
        args.putString("dueDate", toDoData.getDueDate());
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

        // Find the "X" button by ID
        ImageView closeBtn = view.findViewById(R.id.todoClose);

        // Set an OnClickListener for the "X" button
        closeBtn.setOnClickListener(v -> dismiss());

        // Set an OnClickListener for "Set Due Date" TextView
        binding.setDueTv.setOnClickListener(v -> showDatePickerDialog());

        if (getArguments() != null) {
            toDoData = new ToDoData(
                    getArguments().getString("taskId"),
                    getArguments().getString("task"),
                    getArguments().getString("dueDate")
            );

            if (toDoData != null) {
                binding.todoEt.setText(toDoData.getTask());
                // Set other UI elements based on toDoData
            }
        }

        binding.todoNextBtn.setOnClickListener(v -> {
            String todoTask = binding.todoEt.getText().toString();
            if (!todoTask.isEmpty()) {
                if (toDoData == null) {
                    listener.saveTask(todoTask, selectedDueDate, binding.todoEt);
                } else {
                    toDoData.setTask(todoTask);
                    listener.updateTask(toDoData, binding.todoEt);
                }
            }
        });
    }



    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Update the selected due date
                    selectedDueDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    // Update the "Set Due Date" TextView with the selected date
                    binding.setDueTv.setText(selectedDueDate);
                },
                year, month, day
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
}