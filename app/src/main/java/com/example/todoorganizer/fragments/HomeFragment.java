package com.example.todoorganizer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        //get data from firebase
        getTaskFromFirebase();

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
        database.child(toDoData.getTaskId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
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
