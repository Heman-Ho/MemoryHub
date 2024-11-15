package ca.sfu.memoryhub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;


public class MatchGame extends AppCompatActivity {
    // Variables to hold screen dimensions and card configurations
    int widthOfScreen, heightOfScreen, noOfCardsX = 2, noOfCardsY = 2,
            widthOfCard, heightOfCard, padding;
    Button exitGame;
    TextView hintText;
    int noOfCards = noOfCardsX * noOfCardsY; // Number of cards must be even
    int correctCounter = 0; // Counter for correct matches used to determine end of game
    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge layout for better UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_match_game);

        // Adjust padding to account for system bars like status bar and navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.matching_game_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Find ID's for UI components
        exitGame = findViewById(R.id.exitMatchGame);
        hintText = findViewById(R.id.hintTextViewMatch);

        //Get the current user's difficulty setting from firebase
        if(FirebaseAuth.getInstance().getCurrentUser()!= null) {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference("Users").child(uid).child("difficulty");
            reference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int difficulty = task.getResult().getValue(Integer.class);
                    //Change card layout based on users preferred difficulty:
                    if (difficulty == 0) {
                        //easy difficulty
                        noOfCardsX = 2;
                        noOfCardsY = 2;
                    } else if (difficulty == 1) {
                        //medium difficulty
                        noOfCardsX = 2;
                        noOfCardsY = 3;
                    } else {
                        //hard difficulty
                        noOfCardsX = 3;
                        noOfCardsY = 4;
                    }
                    noOfCards = noOfCardsX * noOfCardsY;
                    //Start the game
                    playGame();
                } else {
                    // Handle the retrieving user data error
                    Toast.makeText(MatchGame.this, "Failed to get user data",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        //Exit button to finish the game and return to game fragment
        exitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Function to initialize and start game
    private void playGame(){
        //Get dimensions of device for card layout
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthOfScreen = displayMetrics.widthPixels;
        heightOfScreen = displayMetrics.heightPixels;
        padding = (int)(widthOfScreen * 0.03);

        //Calculate card sizes based on screen dimensions
        widthOfCard = ((int) (widthOfScreen * 0.9) / noOfCardsX) - (padding);
        heightOfCard = ((int)(heightOfScreen * 0.8)/ noOfCardsY) - (padding);

        //Create random Card Array
        List<Card> cards = createCards();

        //Create the board
        createBoard(cards);

        //handler used for creating delay (used for flipping cards after x seconds)
        Handler handler = new Handler();
        final int[] numCardsFlipped = {0};
        final int[] previousFlipped = new int[1];
        boolean[] isInteractionEnabled = {true};

        //Set on click listener for cards
        for(int i = 0; i < noOfCards; i++){
            int finalI = i;
            cards.get(i).getImg().setOnClickListener(new View.OnClickListener() {
                // Suppress warning for setting text directly in code
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    // Only process if the clicked card is faced down and
                    // interaction is enabled (to prevent user from flipping cards too fast)
                    if(! cards.get(finalI).isShowCard() && isInteractionEnabled[0]){
                        isInteractionEnabled[0] = false;
                        //Flip the card face up
                        cards.get(finalI).flipImg();
                        numCardsFlipped[0] += 1;

                        // If two cards are flipped, then check if the images match
                        if(numCardsFlipped[0] == 2){
                            if(cards.get(finalI).getLoc() == cards.get(previousFlipped[0]).getMatchingLoc()){
                                // Cards do match, mark them as correct
                                cards.get(finalI).setCorrect(true);
                                cards.get(previousFlipped[0]).setCorrect(true);
                                correctCounter += 2;
                                hintText.setText("Correct!");
                                isInteractionEnabled[0] = true;
                            }else{
                                // Cards don't match, flip them back after a short delay
                                hintText.setText("Try Again!");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        cards.get(finalI).flipImg();
                                        cards.get(previousFlipped[0]).flipImg();
                                        isInteractionEnabled[0] = true;
                                    }
                                }, 1400); // Delay in milliseconds
                            }
                            // Reset number of cards flipped because one pair of guesses is complete
                            numCardsFlipped[0] = 0;
                        }else{
                            // One card has been flipped so far,
                            // store the index of the first card flipped
                            previousFlipped[0] = finalI;
                            isInteractionEnabled[0] = true;
                        }
                    }
                    // Check for game completion
                    if(correctCounter >= noOfCards){
                        hintText.setText("You Win!");
                    }
                }
            });
        }
    }

    // Function to create the cards with images and random positions
    // Returns a list of cards with the indices corresponding to the card's location.
    // Each card is given a random location and stores it's own location and
    // the location of it's matching pair
    private List<Card> createCards() {
        Random random = new Random();

        // Array of images for the cards (sample images as gallery is incomplete)
        int[] images = {
                R.drawable.sample_image01,
                R.drawable.sample_image02,
                R.drawable.sample_image03,
                R.drawable.sample_image04,
                R.drawable.sample_image05,
                R.drawable.sample_image06,
                R.drawable.sample_image07,
                R.drawable.sample_image01,
                R.drawable.sample_image02,
                R.drawable.sample_image03,
                R.drawable.sample_image04,
                R.drawable.sample_image05,
                R.drawable.sample_image06,
                R.drawable.sample_image07,
        };

        //Create an array of noOfCard Cards with null objects
        List<Card> cards = new ArrayList<>(Collections.nCopies(noOfCards, null));

        //create a map that tracks whether a location is empty
        int[] locEmpty = new int[noOfCards];
        Arrays.fill(locEmpty, 1);

        for(int i = 0; i < noOfCards/2; i++) {
            // Find random unclaimed location for a pair of matching cards
            int randomLoc1 = random.nextInt(noOfCards);
            while(locEmpty[randomLoc1] == 0){
                randomLoc1 = (randomLoc1 + 1) % noOfCards;
            }
            locEmpty[randomLoc1] = 0;
            int randomLoc2 = random.nextInt(noOfCards);
            while(locEmpty[randomLoc2] == 0){
                randomLoc2 = (randomLoc2 + 1) % noOfCards;
            }
            locEmpty[randomLoc2] = 0;

            // Create two ImageViews for pair of cards
            ImageView cardImgView1 = createCardImgView(i * 2);
            ImageView cardImgView2 = createCardImgView(i * 2 + 1);

            // Create two Card objects for matching pair of cards
            Card card1 = new Card(randomLoc1, randomLoc2, cardImgView1, images[i],false, false);
            Card card2 = new Card(randomLoc2, randomLoc1, cardImgView2, images[i], false, false);

            // Adds the card into it's random location as the index of the cards list
            cards.set(randomLoc1, card1);
            cards.set(randomLoc2, card2);
        }
        return cards;
    }

    // Function to initialize the game board layout and add the cards
    private void createBoard(List<Card> cards) {

        GridLayout gameBoard = findViewById(R.id.match_board);
        gameBoard.setRowCount(noOfCardsY);
        gameBoard.setColumnCount(noOfCardsX);
        gameBoard.getLayoutParams().height = (int)(heightOfScreen * 0.8) + padding;
        gameBoard.getLayoutParams().width = (int)(widthOfScreen * 0.9) + padding;

        //Add cards to grid
        for(int i = 0; i < noOfCards; i ++){
            gameBoard.addView(cards.get(i).getImg());

        }

    }

    //Function to create ImageView for a card
    private ImageView createCardImgView(int id){
        // Create Cards
        ImageView cardImg = new ImageView(this);
        cardImg.setId(id);
        // Set parameters for card's ImageView
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = widthOfCard;
        params.height = heightOfCard;
        params.setMargins(padding, padding, 0, 0);
        cardImg.setLayoutParams(params);
        cardImg.setMaxHeight(heightOfCard);
        cardImg.setMaxWidth(widthOfCard);
        cardImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        cardImg.setImageResource(R.drawable.sharpcornercard);
        cardImg.setBackgroundResource(R.drawable.rounded_corners);


        return cardImg;
    }

}