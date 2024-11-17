package ca.sfu.memoryhub.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import ca.sfu.memoryhub.puzzle;
import ca.sfu.memoryhub.StartPage;
import ca.sfu.memoryhub.MatchGame;
import ca.sfu.memoryhub.R;
import ca.sfu.memoryhub.databinding.FragmentNotificationsBinding;
import ca.sfu.memoryhub.databinding.PuzzleGameBinding;

public class NotificationsFragment extends Fragment {

    String[] difficulties = {"Easy", "Medium", "Hard"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    FirebaseDatabase db;
    DatabaseReference reference;

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnPuzzleGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), puzzle.class);
                startActivity(i);
            }
        });

        binding.btnMatchGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MatchGame.class);
                startActivity(i);
            }
        });


        //Set up firebase access to difficulty setting

        db = FirebaseDatabase.getInstance();

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        reference = db.getReference("Users").child(uid).child("difficulty");

        //Setup dropdown menu for difficulty setting
        autoCompleteTextView = binding.autoCompleteText;
        adapterItems = new ArrayAdapter<String>(requireContext(), R.layout.match_dropdown, difficulties);

        //Set default text to what is stored in firebase
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int difficulty = task.getResult().getValue(Integer.class);
                //set the textview to be saved preference
                autoCompleteTextView.setText(difficulties[difficulty]);
                autoCompleteTextView.setAdapter(adapterItems);
            }
        });



        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                switch (item) {
                    case "Easy":
                        reference.setValue(0).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successfully updated the difficulty
                                Toast.makeText(getContext(), "Difficulty set to Easy", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the error
                                Toast.makeText(getContext(), "Failed to set difficulty", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "Medium":
                        reference.setValue(1).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successfully updated the difficulty
                                Toast.makeText(getContext(), "Difficulty set to Medium", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the error
                                Toast.makeText(getContext(), "Failed to set difficulty", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "Hard":
                        reference.setValue(2).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successfully updated the difficulty
                                Toast.makeText(getContext(), "Difficulty set to Hard", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the error
                                Toast.makeText(getContext(), "Failed to set difficulty", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        });
//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}