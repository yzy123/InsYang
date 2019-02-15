package com.yang.insyang.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yang.insyang.Model.Post;
import com.yang.insyang.Model.User;
import com.yang.insyang.R;

import java.util.List;


/**
 *  PostAdapter
 *  utilized in home page to display photos
 */


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mPost;
    public static String TAG = "TEST_YANG";


    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Post post = mPost.get(i);
        if (post.getDescription().equals("")){
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }

        publisherInfo(post.getPublisher(), post, viewHolder);

    }


    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image;
        public TextView username, publisher, description;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
        }
    }

    private void publisherInfo(String userid, final Post post, final ViewHolder viewHolder){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: user information has been changed");
                User user = dataSnapshot.getValue(User.class);
                try{
                    Glide.with(mContext).load(post.getPostimage()).into(viewHolder.post_image);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    Glide.with(mContext).load(user.getImageurl()).into(viewHolder.image_profile);
                }catch (Exception e){
                    e.printStackTrace();
                }
                viewHolder.username.setText(user.getUsername());
                viewHolder.publisher.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage() );
            }
        });

    }
}
