package com.example.todoorganizer.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.todoorganizer.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashFragment extends Fragment {

    private FirebaseAuth mAuth;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        boolean isLogin = mAuth.getCurrentUser() != null;

        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin) {
                    navController.navigate(R.id.action_splashFragment_to_homeFragment);
                } else {
                    navController.navigate(R.id.action_splashFragment_to_signinFragment);
                }

                //navController.navigate(R.id.action_splashFragment_to_signinFragment);
            }
        }, 2000);
    }

    private void init(View view) {
        mAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);
    }
}
