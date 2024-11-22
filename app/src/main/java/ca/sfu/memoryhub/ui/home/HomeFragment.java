package ca.sfu.memoryhub.ui.home;

import ca.sfu.memoryhub.MainActivity;
import ca.sfu.memoryhub.R;
import ca.sfu.memoryhub.Settings;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import ca.sfu.memoryhub.MatchGame;
import ca.sfu.memoryhub.StartPage;
import ca.sfu.memoryhub.Users;
import ca.sfu.memoryhub.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the text view objects
        TextView usernameText = binding.usernameTextView;
        TextView emailText = binding.emailTextView;

        // Navigate to settings page when settings button is clicked
        binding.btnProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), Settings.class);
                startActivity(i);
            }
        });

        // Get reference to firebase user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String uid = currentUser.getUid();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        userReference.get().addOnCompleteListener(task -> {
            // Successfully fetched user data
            DataSnapshot snapshot = task.getResult();
            if (snapshot.exists()) {
                String email = currentUser.getEmail();
                String username = snapshot.child("username").getValue(String.class);

                // Set text views to stored data from firebase
                usernameText.setText(username);
                if(!(currentUser.getEmail() == null)){
                    emailText.setText(email);
                }

            }
        });


        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}