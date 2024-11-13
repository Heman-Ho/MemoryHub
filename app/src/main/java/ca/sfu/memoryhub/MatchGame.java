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


    int widthOfScreen, heightOfScreen, noOfCardsX = 2, noOfCardsY = 2,
            widthOfCard, heightOfCard, padding;
    Button exitGame;
    TextView hintText;
    int noOfCards = noOfCardsX * noOfCardsY; //MUST BE EVEN
    int correctCounter = 0;
    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_match_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.matching_game_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Find ID's
        exitGame = findViewById(R.id.exitMatchGame);
        hintText = findViewById(R.id.hintTextViewMatch);

        //Get the current user object from firebase
        if(FirebaseAuth.getInstance().getCurrentUser()!= null) {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference("Users").child(uid).child("difficulty");
            reference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int difficulty = task.getResult().getValue(Integer.class);
                    //Change noOfCards based on users preferred difficulty:
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
                    playGame();
                } else {
                    // Handle the error
                    Toast.makeText(MatchGame.this, "Failed to get user data",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        //Exit button
        exitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void playGame(){
        //Get dimensions of device
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthOfScreen = displayMetrics.widthPixels;
        heightOfScreen = displayMetrics.heightPixels;
        padding = (int)(widthOfScreen * 0.03);

        /* Calculate card sizes */
        widthOfCard = ((int) (widthOfScreen * 0.9) / noOfCardsX) - (padding);
        heightOfCard = ((int)(heightOfScreen * 0.8)/ noOfCardsY) - (padding);

        //Create random Card Array
        List<Card> cards = createCards();

        //Create the board
        createBoard(cards);

        //handler used for creating delay
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
                    //Game logic
                    if(! cards.get(finalI).isShowCard() && isInteractionEnabled[0]){
                        isInteractionEnabled[0] = false;
                        //Tapped card is faced down
                        cards.get(finalI).flipImg();
                        numCardsFlipped[0] += 1;
                        if(numCardsFlipped[0] == 2){
                            if(cards.get(finalI).getLoc() == cards.get(previousFlipped[0]).getMatchingLoc()){
                                // Cards do match
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
                                }, 1400); // Delay in milliseconds (e.g., 1 second)
                            }
                            numCardsFlipped[0] = 0;
                        }else{
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
    private List<Card> createCards() {
        Random random = new Random();

        //Create images array from Gallery (gallery is not complete so placeholder)
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
            // Find random unclaimed location for two cards
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

            ImageView cardImgView1 = createCardImgView(i * 2);
            ImageView cardImgView2 = createCardImgView(i * 2 + 1);

            Card card1 = new Card(randomLoc1, randomLoc2, cardImgView1, images[i],false, false);
            Card card2 = new Card(randomLoc2, randomLoc1, cardImgView2, images[i], false, false);
            cards.set(randomLoc1, card1);
            cards.set(randomLoc2, card2);
        }
        return cards;
    }

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

    private ImageView createCardImgView(int id){
        //Create Cards
        ImageView cardImg = new ImageView(this);
        cardImg.setId(id);
        //Set parameters for card's ImageView
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