package com.example.todoorganizer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todoorganizer.R;
import com.example.todoorganizer.databinding.FragmentHomeBinding;
import com.example.todoorganizer.utils.TaskAdapter;
import com.example.todoorganizer.utils.ToDoData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment implements AddTodoPopupFragment.OnDialogNextBtnClickListener, TaskAdapter.TaskAdapterInterface {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private DatabaseReference database;
    private AddTodoPopupFragment frag;
    private FirebaseAuth auth;
    private String authId;

    private TaskAdapter taskAdapter;
    private List<ToDoData> toDoItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Check if the user is authenticated
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // User is not authenticated, navigate to signinFragment
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_homeFragment_to_signinFragment);
            return binding.getRoot();  // Make sure to return here to avoid further execution
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        //get data from firebase
        getTaskFromFirebase();

        Button logoutBtn = binding.getRoot().findViewById(R.id.logoutBtn);
        NavController navController = Navigation.findNavController(requireView());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the confirmation dialog
                showLogoutConfirmationDialog();
            }
        });

        binding.addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (frag != null)
                    getChildFragmentManager().beginTransaction().remove(frag).commit();
                frag = new AddTodoPopupFragment();
                frag.setListener(HomeFragment.this);

                frag.show(getChildFragmentManager(), AddTodoPopupFragment.TAG);
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        ConfirmLogoutDialogFragment dialogFragment = new ConfirmLogoutDialogFragment();
        dialogFragment.show(getChildFragmentManager(), "confirmLogoutDialog");
    }

    public void logoutUser() {
        // Sign out the user
        FirebaseAuth.getInstance().signOut();

        // Navigate to signinFragment
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_homeFragment_to_signinFragment);
    }

    private void getTaskFromFirebase() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                toDoItemList.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    ToDoData todoTask = new ToDoData(taskSnapshot.getKey(), taskSnapshot.getValue().toString());
                    if (todoTask != null) {
                        toDoItemList.add(todoTask);
                    }
                }
                Log.d(TAG, "onDataChange: " + toDoItemList);
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        authId = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference().child("Tasks").child(authId);

        binding.mainRecyclerView.setHasFixedSize(true);
        binding.mainRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        toDoItemList = new ArrayList<>();
        taskAdapter = new TaskAdapter(toDoItemList);
        taskAdapter.setListener(HomeFragment.this);
        binding.mainRecyclerView.setAdapter(taskAdapter);
    }

    @Override
    public void saveTask(String todoTask, TextInputEditText todoEdit) {
        database.push().setValue(todoTask).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Task Added Successfully", Toast.LENGTH_SHORT).show();
                todoEdit.setText(null);
            } else {
                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
            frag.dismiss();
        });
    }

    @Override
    public void updateTask(ToDoData toDoData, TextInputEditText todoEdit) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(toDoData.getTaskId(), toDoData.getTask());
        database.updateChildren(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
            frag.dismiss();
        });
    }

    @Override
    public void onDeleteItemClicked(ToDoData toDoData, int position) {
        // Show the confirmation dialog for deleting the task
        showDeleteConfirmationDialog(toDoData.getTaskId());
    }

    private void showDeleteConfirmationDialog(String taskId) {
        ConfirmDeleteDialogFragment dialogFragment = ConfirmDeleteDialogFragment.newInstance(taskId);
        dialogFragment.show(getChildFragmentManager(), "confirmDeleteDialog");
    }


    public void deleteTask(String taskId) {
        // Remove the task from the database
        database.child(taskId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Task Deleted Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to delete task", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditItemClicked(ToDoData toDoData, int position) {
        if (frag != null)
            getChildFragmentManager().beginTransaction().remove(frag).commit();
        frag = AddTodoPopupFragment.newInstance(toDoData.getTaskId(), toDoData.getTask());
        frag.setListener(HomeFragment.this);
        frag.show(getChildFragmentManager(), AddTodoPopupFragment.TAG);
    }
}