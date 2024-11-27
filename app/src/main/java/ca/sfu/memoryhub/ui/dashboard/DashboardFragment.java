package ca.sfu.memoryhub.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.sfu.memoryhub.R;

public class DashboardFragment extends Fragment {

    private StorageReference storageReference;
    private Uri imageUri;
    private MaterialButton uploadButton;
    private RecyclerView recyclerViewGallery;
    private GalleryAdapter galleryAdapter;
    private final List<String> imageUrls = new ArrayList<>();

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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext());
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize views
        uploadButton = view.findViewById(R.id.uploadButton);
        recyclerViewGallery = view.findViewById(R.id.recyclerViewGallery);

        // Set up RecyclerView
        recyclerViewGallery.setLayoutManager(new LinearLayoutManager(requireContext()));
        galleryAdapter = new GalleryAdapter(requireContext(), imageUrls);
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

        // Get the current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference for the image in Firebase Storage
        StorageReference ref = storageReference.child("images/" + userId + "/" + UUID.randomUUID().toString());

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    // Refresh the gallery to show the newly uploaded image
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrls.add(uri.toString());
                        galleryAdapter.notifyDataSetChanged();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Load images uploaded by the current user
    private void loadUserImages() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference userImagesRef = storageReference.child("images/" + userId);

        userImagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            galleryAdapter.notifyDataSetChanged();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load images: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
