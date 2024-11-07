package ca.sfu.memoryhub;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MatchGame extends AppCompatActivity {

    int widthOfScreen, heightOfScreen, noOfCardsX = 3, noOfCardsY = 4,
            widthOfCard, heightOfCard, padding;
    Button exitGame;
    int noOfCards = noOfCardsX * noOfCardsY; //MUST BE EVEN
    int correctCounter = 0;

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
        exitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        //For creating delay
        Handler handler = new Handler();

        final int[] numCardsFlipped = {0};
        final int[] previousFlipped = new int[1];
        //Set on click listener for cards
        for(int i = 0; i < noOfCards; i++){
            int finalI = i;
            cards.get(i).getImg().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Game logic
                    if(! cards.get(finalI).isShowCard()){
                        //Tapped card is faced down
                        cards.get(finalI).flipImg();
                        numCardsFlipped[0] += 1;
                        if(numCardsFlipped[0] == 2){
                            if(cards.get(finalI).getLoc() == cards.get(previousFlipped[0]).getMatchingLoc()){
                                // Cards do match
                                cards.get(finalI).setCorrect(true);
                                cards.get(previousFlipped[0]).setCorrect(true);
                                correctCounter += 2;
                                Toast.makeText(MatchGame.this, "Correct!", Toast.LENGTH_SHORT).show();
                            }else{
                                // Cards don't match, flip them back after a short delay
                                Toast.makeText(MatchGame.this, "Incorrect", Toast.LENGTH_SHORT).show();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        cards.get(finalI).flipImg();
                                        cards.get(previousFlipped[0]).flipImg();
                                    }
                                }, 1000); // Delay in milliseconds (e.g., 1 second)
                            }
                            numCardsFlipped[0] = 0;
                        }
                        if(numCardsFlipped[0] == 1){
                            previousFlipped[0] = finalI;
                        }
                    }
                    // Check for game completion
                    if(correctCounter >= noOfCards){
                        Toast.makeText(MatchGame.this, "YOU WIN!", Toast.LENGTH_SHORT).show();
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
        cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cardImg.setImageResource(R.drawable.sample_card);

        return cardImg;
    }
}