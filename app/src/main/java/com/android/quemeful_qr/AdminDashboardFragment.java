package com.android.quemeful_qr;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * contains the admin view's dashboard
 */
public class AdminDashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private admin_dashboard_adapter adapter;

    /**
     * constructor with empty parameters
     */
    public AdminDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * creates new instance of the class
     * @return AdminDashboardFragment
     */
    public static AdminDashboardFragment newInstance() {
        return new AdminDashboardFragment();
    }

    /**
     * shows recyclerview for admin dashboard and takes all nonadmin users and keeps track of count
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_dashboard, container, false);

        recyclerView = view.findViewById(R.id.admin_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Initialize the adapter with the context and an empty list
        adapter = new admin_dashboard_adapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        fetchUsers();

        return view;
    }

    /**
     * gets all users from firebase that are not admins
     */
    private void fetchUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Map<String, Object>> users = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> userData = new HashMap<>(document.getData());
                    // Check if the user is not an admin before adding to the list
                    if (!Boolean.TRUE.equals(userData.get("Admin"))) {
                        userData.put("docId", document.getId());  // Include the document ID
                        users.add(userData);
                        Log.d("AdminDashboardFragment", "Non-admin User: " + userData);
                    }
                }
                adapter.updateDataList(users);
                Log.d("AdminDashboardFragment", "Number of non-admin users fetched: " + users.size());
            } else {
                Log.e("AdminDashboardFragment", "Error fetching users: ", task.getException());
            }
        });
    }


}
