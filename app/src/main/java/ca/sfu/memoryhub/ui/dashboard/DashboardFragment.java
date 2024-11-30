package ca.sfu.memoryhub.ui.dashboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ca.sfu.memoryhub.R;

public class DashboardFragment extends Fragment {

    private StorageReference storageReference;
    private Uri imageUri;
    private MaterialButton uploadButton;
    private RecyclerView recyclerViewGallery;
    private GalleryAdapter galleryAdapter;
    private SearchView searchBar;
    private final List<String> imageUrls = new ArrayList<>();
    private List<String> imageDescriptions = new ArrayList<>();
    private List<String> imageTitles = new ArrayList<>();

    // Register activity result launcher to handle image selection
    private final androidx.activity.result.ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData(); // Set the imageUri when an image is picked
                    uploadImage();
                } else {
                    Toast.makeText(getContext(), "Couldn't upload image", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate your fragment layout
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
//    THEIR VERSION
//    public void searchList(String text){
//        ArrayList<DataClass> searchList = new ArrayList<DataClass>();
//        for(DatClass dataclass : datalist){
//            if(dataClass.getDataTitle().toLowerCase().contains(text.toLowerCase())){
//                searchList.add(dataclass);
//            }
//        }
//        galleryAdapter.searchDataList(searchList);
//    }

//    MY VERSION
    public void searchList(String text){
        ArrayList<String> searchList = new ArrayList<String>();
        for(String title : imageTitles){
            if(title.toLowerCase().contains(text.toLowerCase())){
                searchList.add(title);
            }
        }
        galleryAdapter.searchDataList(searchList);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext());
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize views
        uploadButton = view.findViewById(R.id.uploadButton);
        recyclerViewGallery = view.findViewById(R.id.recyclerViewGallery);
        searchBar = view.findViewById(R.id.search);
        searchBar.clearFocus();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        // Set up RecyclerView
        recyclerViewGallery.setLayoutManager(new LinearLayoutManager(requireContext()));
        galleryAdapter = new GalleryAdapter(requireContext(), imageUrls, imageDescriptions);
        recyclerViewGallery.setAdapter(galleryAdapter);

        // Button to upload the selected image
        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        // Load images uploaded by the current user
        loadUserImages();
    }

    // Upload the selected image to Firebase Storage under the current user's UID
    private void uploadImage() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a dialog for the user to input a title and description
        Dialog metadataDialog = new Dialog(requireContext());
        metadataDialog.setContentView(R.layout.dialog_add_title);
        Objects.requireNonNull(metadataDialog.getWindow())
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView titleInput = metadataDialog.findViewById(R.id.titleInput);
        TextView descriptionInput = metadataDialog.findViewById(R.id.descriptionInput);
        MaterialButton saveButton = metadataDialog.findViewById(R.id.saveButton);
        MaterialButton cancelButton = metadataDialog.findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Title cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            metadataDialog.dismiss();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference ref = storageReference.child("images/" + userId + "/" + UUID.randomUUID().toString());

            // Create metadata with the title and description
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("title", title)
                    .setCustomMetadata("description", description)
                    .build();

            ref.putFile(imageUri, metadata)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "Image uploaded successfully with title!", Toast.LENGTH_SHORT).show();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            imageDescriptions.add("Title: " + title + "\nDescription: " + description);
                            imageTitles.add(title.toLowerCase());
                            galleryAdapter.notifyDataSetChanged();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        cancelButton.setOnClickListener(v -> metadataDialog.dismiss());

        metadataDialog.show();
    }

    // Load images uploaded by the current user
    private void loadUserImages() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference userImagesRef = storageReference.child("images/" + userId);

        userImagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {

                        // Get downloadUrl and image's metadata.
                        Task<Uri> downloadUrlTask = item.getDownloadUrl();
                        Task<StorageMetadata> metadataTask = item.getMetadata();

                        // Wait until both tasks are completed
                        Tasks.whenAllSuccess(downloadUrlTask, metadataTask).addOnSuccessListener(tasks -> {
                            Uri uri = (Uri) tasks.get(0);  // Download URL is the first task
                            StorageMetadata metadata = (StorageMetadata) tasks.get(1);  // Metadata is the second task

                            // Retrieve title and description from metadata
                            String title = metadata.getCustomMetadata("title");
                            if (title == null) {
                                title = "No title available";
                            }
                            String description = metadata.getCustomMetadata("description");
                            if (description == null) {
                                description = "No description available";
                            }

                            // Combine title and description
                            String combinedMetadata = "Title: " + title + "\nDescription: " + description;

                            // Add data to lists
                            imageUrls.add(uri.toString());
                            imageDescriptions.add(combinedMetadata);
                            imageTitles.add(title.toLowerCase());

                            // Notify adapter about data changes
                            galleryAdapter.notifyDataSetChanged();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load images: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


    }
}
