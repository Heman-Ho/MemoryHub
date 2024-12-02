package ca.sfu.memoryhub.ui.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ca.sfu.memoryhub.puzzle;
import ca.sfu.memoryhub.MatchGame;
import ca.sfu.memoryhub.R;
import ca.sfu.memoryhub.databinding.FragmentNotificationsBinding;


public class NotificationsFragment extends Fragment {

    String[] difficulties = {"Easy", "Medium", "Hard"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    FirebaseDatabase db;
    DatabaseReference reference;
    FirebaseStorage storage;
    int difficulty = 1; // Default difficulty set to medium
    int matchGameNumImages = 6;

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
                difficulty = task.getResult().getValue(Integer.class);
            }
            //set the textview to be saved preference
            autoCompleteTextView.setText(difficulties[difficulty]);
            autoCompleteTextView.setAdapter(adapterItems);
        });

        // Create maps to keep track of descriptions of images
        Map<String, String> matchGameUrlToDescription = new HashMap<>();
        Map<String, String> puzzleGameUrlToDescription = new HashMap<>();

        // Retrieve List of 6 ImagesUrls from firebase storage for the match game
        List<String> imageUrls = new ArrayList<>();
        addGalleryImagesTo(imageUrls, matchGameNumImages, matchGameUrlToDescription);

        // Retrieve 1 random ImageUrl from firebase storage for puzzle game
        List<String> puzzleImageUrl = new ArrayList<>();
        addGalleryImagesTo(puzzleImageUrl, 1, puzzleGameUrlToDescription);

        //Set up onClickListeners for buttons to access games
        binding.btnPuzzleGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(puzzleImageUrl.size() == 1){
                    Intent i = puzzle.makeIntent(getContext(), difficulty, puzzleImageUrl.get(0), puzzleGameUrlToDescription);
                    startActivity(i);
                }else{
                    Toast.makeText(getContext(), getString(R.string.please_wait_while_images_are_loading), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnMatchGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUrls.size() == matchGameNumImages){
                    Intent i = MatchGame.makeIntent(getContext(), difficulty, imageUrls, matchGameUrlToDescription);
                    startActivity(i);
                }else{
                    Toast.makeText(getContext(), getString(R.string.please_wait_while_images_are_loading), Toast.LENGTH_SHORT).show();
                }
            }
        });





        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                switch (item) {
                    case "Easy":
                        difficulty = 0;
                        reference.setValue(0).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successfully updated the difficulty
                                Toast.makeText(getContext(), getString(R.string.difficulty_set_to_easy), Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the error
                                Toast.makeText(getContext(), getString(R.string.failed_to_set_difficulty), Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "Medium":
                        difficulty = 1;
                        reference.setValue(1).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successfully updated the difficulty
                                Toast.makeText(getContext(), getString(R.string.difficulty_set_to_medium), Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the error
                                Toast.makeText(getContext(), getString(R.string.failed_to_set_difficulty), Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "Hard":
                        difficulty = 2;
                        reference.setValue(2).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Successfully updated the difficulty
                                Toast.makeText(getContext(), getString(R.string.difficulty_set_to_hard), Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the error
                                Toast.makeText(getContext(), getString(R.string.failed_to_set_difficulty), Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        });
        return root;
    }

    // Takes a string list and adds images from the gallery as urls to the list.
    // If there are less than numImages images in the firebase storage, sample images are
    // added to the list instead. A maximum of 6 sample images are provided.
    // A map is created using each imageUrl as a key and the value is the corresponding
    // description that is taken from firebase storage.
    private void addGalleryImagesTo(List<String> images, int numImages, Map<String, String> map) {
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get the user id to access the current user's folder in firebase storage
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        StorageReference imageFolderRef = storageRef.child("images/").child(uid);
        StorageReference sampleFolderRef = storageRef.child("images/").child("samples/");
        final int[] i = {0};
        imageFolderRef.listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> items = listResult.getItems();

            // Shuffle the list of items to randomize the order
            Collections.shuffle(items);
            int numPhotosInGallery = items.size();
            // Create the list of image urls
            while(i[0] < numImages && i[0] < numPhotosInGallery){
                // Start both tasks to get the download URL and metadata at the same time
                Task<Uri> downloadUrlTask = items.get(i[0]).getDownloadUrl();
                Task<StorageMetadata> metadataTask = items.get(i[0]).getMetadata();

                // Wait until both tasks are completed
                Tasks.whenAllSuccess(downloadUrlTask, metadataTask).addOnSuccessListener(tasks -> {
                    Uri uri = (Uri) tasks.get(0);  // Download URL is the first task
                    StorageMetadata metadata = (StorageMetadata) tasks.get(1);  // Metadata is the second task

                    // Get the image URL and metadata
                    String imageDownloadUrl = uri.toString();
                    String description = metadata.getCustomMetadata("description");

                    // Add to images list and map
                    images.add(imageDownloadUrl);
                    map.put(imageDownloadUrl, description != null ? description : getString(R.string.no_description_available));
                });
                i[0]++;
            }

            // if there is not enough images in the user's gallery,
            // add sample images until the size of images list equals to numImages
            sampleFolderRef.listAll().addOnSuccessListener(sampleListResult -> {
                List<StorageReference> sampleItems = sampleListResult.getItems();
                while(i[0] < numImages){
                    sampleItems.get(i[0]).getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageDownloadUrl = uri.toString();
                        images.add(imageDownloadUrl);
                        map.put(imageDownloadUrl, getString(R.string.this_is_a_sample_image));
                    });
                    i[0] ++;
                }
            });


        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}