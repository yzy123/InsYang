package com.yang.insyang.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yang.insyang.Adapter.PostAdapter;
import com.yang.insyang.Model.Post;
import com.yang.insyang.R;

import java.util.ArrayList;
import java.util.List;

/**
 *  Home page of this app, mainly show the photos uploaded by people user follows
 */

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    public static String TAG = "TEST_YANG";
    String selfId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private List<String> followingList;




    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        checkFollowing();
        return view;
    }
    /** check other users current user follows **/
    private void checkFollowing() {
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(selfId)
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                followingList.add(selfId);

                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage() );
            }
        });
    }

    /** add all the photos upload by others user follows **/
    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: new photo has been posted");
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id : followingList){
                        if (post.getPublisher().equals(id)){
                            postList.add(post);
                        }
                    }

                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

    }

}
