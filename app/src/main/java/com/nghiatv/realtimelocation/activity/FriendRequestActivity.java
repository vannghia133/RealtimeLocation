package com.nghiatv.realtimelocation.activity;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.nghiatv.realtimelocation.IFirebaseLoadDone;
import com.nghiatv.realtimelocation.IRecyclerItemClickListener;
import com.nghiatv.realtimelocation.R;
import com.nghiatv.realtimelocation.model.User;
import com.nghiatv.realtimelocation.utils.Common;
import com.nghiatv.realtimelocation.viewholder.FriendRequestViewHolder;
import com.nghiatv.realtimelocation.viewholder.UserViewHolder;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestActivity extends AppCompatActivity implements IFirebaseLoadDone {
    FirebaseRecyclerAdapter<User, FriendRequestViewHolder> adapter, searchAdapter;
    RecyclerView recyclerAllUser;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //Init view
        searchBar = (MaterialSearchBar) findViewById(R.id.materialSearchBar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase())) {
                        suggest.add(search);
                    }
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    if (adapter != null) {
                        //If close search, restore default
                        recyclerAllUser.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recyclerAllUser = (RecyclerView) findViewById(R.id.recyclerAllPeople);
        recyclerAllUser.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerAllUser.setLayoutManager(layoutManager);
        recyclerAllUser.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));

        firebaseLoadDone = this;

        loadFriendRequestList();
        loadSearchData();
    }

    private void loadFriendRequestList() {
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position, @NonNull final User model) {
                holder.txtUserEmail.setText(model.getEmail());
                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(model, false);
                        addToAcceptList(model);
                        addUserToFriendContact(model);

                    }
                });
                holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(model, true);
                    }
                });
            }

            @NonNull
            @Override
            public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_friend_request, viewGroup, false);
                return new FriendRequestViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerAllUser.setAdapter(adapter);

    }

    private void addUserToFriendContact(User model) {
        //Friend add user
        DatabaseReference acceptList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(model.getUid())
                .child(Common.ACCEPT_LIST);

        acceptList.child(model.getUid()).setValue(Common.loggedUser);
    }

    private void addToAcceptList(User model) {
        //User add friend
        DatabaseReference acceptList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);

        acceptList.child(model.getUid()).setValue(model);
    }

    private void deleteFriendRequest(final User model, final boolean isShowMessage) {
        DatabaseReference friendRequest = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST);

        friendRequest.child(model.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (isShowMessage) {
                            Toast.makeText(FriendRequestActivity.this, "Remove!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        if (adapter != null) {
            adapter.stopListening();
        }
        if (searchAdapter != null) {
            searchAdapter.stopListening();
        }
        super.onStop();
    }

    private void startSearch(String searchValue) {
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST)
                .orderByChild("name")
                .startAt(searchValue);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position, @NonNull final User model) {
                holder.txtUserEmail.setText(model.getEmail());
                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(model, false);
                        addToAcceptList(model);
                        addUserToFriendContact(model);

                    }
                });
                holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(model, true);
                    }
                });
            }

            @NonNull
            @Override
            public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_friend_request, viewGroup, false);
                return new FriendRequestViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recyclerAllUser.setAdapter(searchAdapter);
    }

    private void loadSearchData() {
        final List<String> lstUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST);
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());
                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lstUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
