package com.yang.insyang.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yang.insyang.EditProfileActivity;
import com.yang.insyang.LoginActivity;
import com.yang.insyang.Model.User;
import com.yang.insyang.R;

/**
 * Profile page mainly show the user information
 */
public class ProfileFragment extends Fragment {

    public static String TAG = "YANG_TEST";
    private TextView username, fullname, bio;
    private ImageView userProfile;
    private String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Button logout, editproifle;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        username = view.findViewById(R.id.username);
        fullname = view.findViewById(R.id.fullname);
        userProfile = view.findViewById(R.id.image_profile);
        editproifle = view.findViewById(R.id.editprofile);
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
        bio = view.findViewById(R.id.bio);
        editproifle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                String biostr = user.getBio();
                bio.setText(biostr.equals("") ? "Currently no biography" : biostr);
                try {
                    Glide.with(ProfileFragment.this).load(user.getImageurl()).into(userProfile);
                }catch (NullPointerException e){
                    Log.e(TAG, "onDataChange: " + e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}
