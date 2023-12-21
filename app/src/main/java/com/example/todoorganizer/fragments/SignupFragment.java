package com.example.todoorganizer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.todoorganizer.R;
import com.example.todoorganizer.databinding.FragmentSignupBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignupFragment extends Fragment {

    private NavController navController;
    private FirebaseAuth mAuth;
    private FragmentSignupBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        binding.textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signupFragment_to_signinFragment);
            }
        });

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailEt.getText().toString();
                String pass = binding.passEt.getText().toString();
                String verifyPass = binding.verifyPassEt.getText().toString();

                if (!email.isEmpty() && !pass.isEmpty() && !verifyPass.isEmpty()) {
                    if (pass.equals(verifyPass)) {
                        registerUser(email, pass);
                    } else {
                        Toast.makeText(requireContext(), "Password is not the same", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Empty fields are not allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                navController.navigate(R.id.action_signupFragment_to_homeFragment);
            } else {
                Toast.makeText(requireContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(View view) {
        navController = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();
    }
}
