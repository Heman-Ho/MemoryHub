package ca.sfu.memoryhub.ui.gamepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import ca.sfu.memoryhub.R;
import ca.sfu.memoryhub.databinding.FragmentGamePageBinding;

public class GamePageFragment extends Fragment {

    FragmentGamePageBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GamePageViewModel gamePageViewModel =
                new ViewModelProvider(this).get(GamePageViewModel.class);

        binding = FragmentGamePageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGame;
        gamePageViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}