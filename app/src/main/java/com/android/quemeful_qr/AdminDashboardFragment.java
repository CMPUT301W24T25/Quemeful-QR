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
 * This fragment class is used to create the administrator dashboard view.
 */
public class AdminDashboardFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdminDashboardAdapter adapter;

    /**
     * This is a default constructor (no parameters).
     */
    public AdminDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * This method creates a new instance of the class.
     * @return the new AdminDashboardFragment instance created.
     */
    public static AdminDashboardFragment newInstance() {
        return new AdminDashboardFragment();
    }

    /**
     * This onCreateView() is used to create the recyclerview for admin dashboard.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return the admin dashboard view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_dashboard, container, false);
        recyclerView = view.findViewById(R.id.admin_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Initialize the adapter with the context and an empty list
        adapter = new AdminDashboardAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        fetchUsers();
        return view;
    }

    /**
     * This method is used to retrieve all non-admin users from the firebase collection.
     * (also counts the number of non-admin users)
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

} // fragment closing
