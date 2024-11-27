package ca.sfu.memoryhub;

import static java.lang.Math.abs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.util.DisplayMetrics;


import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintLayout;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class puzzle extends AppCompatActivity implements View.OnTouchListener{
    // id used to extract difficulty setting from games fragment
    private static final String GAME_DIFFICULTY = "game difficulty";
    //puzzlePieces stores the bitmaps of all puzzle pieces
    private ArrayList<Bitmap> puzzlePieces;
    //puzzlePieceStartingLocations stores all the coordinates of where the pieces should start on the screen
    private ArrayList<Float> puzzlePieceStartingLocations = new ArrayList<>();

    // puzzleDim is the Length & Width of the puzzle. Puzzle lengths will always be even
    private int puzzleDim = 3;
    //numPiecesCorrect stores the amount of pieces that have been put in the correct spot
    private int numPiecesCorrect = 0;
    //totalNumPieces stores the total number of pieces in the puzzle
    private int totalNumPieces = getPuzzleDim()*getPuzzleDim();
    //solutionCoords stores all the correct x & y coordinates for each puzzle piece. the coordinates are the top-left of each piece
    private ArrayList<Float> solutionCoords = new ArrayList<>();
    private int difficulty;
    private String imageUrl;
    private String imageDescription;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.puzzle_game); // sets the puzzle layout
//        setBackgroundImage();
        Button exitButton = findViewById(R.id.exitButton); // gets the exit button

        //exits the puzzle game if user clicks on the exit button
        exitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // Extract the difficulty setting from games fragment intent
        extractDifficulty();
        // Easy difficulty (0) -> puzzleDim = 2
        // Medium difficulty (1) -> puzzleDim = 3
        // Hard difficulty (2) -> puzzleDim = 4
        puzzleDim = difficulty + 2;
        totalNumPieces = getPuzzleDim()*getPuzzleDim();

        //gets the relative layout used in the .xml

//        final RelativeLayout layout = findViewById(R.id.puzzle_game_relative_layout);
        final ConstraintLayout l = findViewById(R.id.puzzle_game);


        final RelativeLayout layout = findViewById(R.id.puzzle_game_relative_layout);

        ImageView puzzleImage = findViewById(R.id.puzzleImage);
        //this will load the ImageView with a image from the users upload photos
        Glide.with(puzzle.this).load(imageUrl).placeholder(R.drawable.test2).listener(new RequestListener<Drawable>() {
            //this function checks if the load failed and sends a message
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Toast.makeText(puzzle.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                return false;
            }
            //this function will execute the other commands if and only if the image is correctly loaded into the imageView
            @Override
            public boolean onResourceReady(@NonNull Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                puzzleImage.post(new Runnable() {
                    @Override
                    public void run() {
                        puzzlePieces = createPieces();
                        // stores the scaled puzzle pieces that are ready to be added to the screen
                        ArrayList<ImageView> piecesToAdd = scalePieces();

                        getSoltution(puzzleImage);

                        //idNum is used to index the solutionCoords to find the correct coordinates for a puzzle piece
                        int idNum =0;
                        setPieceStartingLocations();
                        //loops through all the puzzle pieces and adds them to the screen
                        for( ImageView piece: piecesToAdd){
                            //sol stores all the information that is needed for a puzzle piece that other functions use
                            double[] sol = {solutionCoords.get(idNum), solutionCoords.get(idNum+1), 0.0, totalNumPieces};
                            //sets sol as a tag to each puzzle piece so each piece has a unique array of information
                            piece.setTag(sol);

                            //allows users to move the puzzle pieces
                            piece.setOnTouchListener(puzzle.this);

                            // randomly chooses a starting postion for a puzzle piece and sets it
                            ArrayList<Float> setCoords = getRandomCoords();
                            piece.setX(setCoords.get(0));
                            piece.setY(setCoords.get(1));
                            layout.addView(piece);
                            // incrememnts idNum for the next puzzle piece -> increments by 2 because the solutionCoords stores both x and y of a piece
                            idNum+=2;
                        }
                }


                });
                return false;

            }
        }).into(puzzleImage);


    }

    //adds different starting locations for the puzzle pieces
    private void setPieceStartingLocations(){
        puzzlePieceStartingLocations.clear();//done as a pre-caution-> not necessary
        ImageView puzzleImage = findViewById(R.id.puzzleImage);
        BitmapDrawable drawable = (BitmapDrawable) puzzleImage.getDrawable();
        Bitmap bitmap = (Bitmap) drawable.getBitmap();


        int[] coords = new int[2];
        //gets the onscreen x and y coordinates (top-left) of the imageView
       puzzleImage.getLocationOnScreen(coords);
       //gap between the puzzle pieces when starting will be 40 px
       int gap = 80/puzzleDim;
       //gets the scale that should be used
       float scale = getScale();
       //calculates the number of gaps that should exist
       int numOfGaps = puzzleDim-1;
       //calculates the scaled width and hieght of the image
       int scaledImageWidth = (int) (scale * bitmap.getWidth());
       int puzzleImageViewWidth = puzzleImage.getWidth();
       //calculates the total difference between the imageView width and image width
       int totalDiff = abs(scaledImageWidth - puzzleImageViewWidth);
       //calculates offset
       int xOffset = (totalDiff/2);

       //calculates the necessary puzzle piece widths and heights after applying a scale
       float puzzlePieceWidth = (float) bitmap.getWidth() /puzzleDim;
       float puzzlePieceHeight = (float) bitmap.getHeight() /puzzleDim;
       int scaledWidth = (int) (puzzlePieceWidth*scale);
       int scaledHeight = (int)(puzzlePieceHeight*scale);

       //calculates the actual starting coordinates and adds them to the array
       int xStart = (coords[0]+xOffset)-((gap*(numOfGaps))/2);
       for(int x = 0; x<puzzleDim;x++){
           int yStart = (coords[1] + puzzleImage.getHeight()) ;
           for(int y = 0; y<puzzleDim; y++){
               puzzlePieceStartingLocations.add((float) xStart);//x coordinate
               puzzlePieceStartingLocations.add((float) yStart);//y coordinate
               yStart+= (scaledHeight+gap);
            }
            xStart+= (scaledWidth + gap);
        }
    }

    //randomly chooses a starting position for a puzzle piece
    private ArrayList<Float> getRandomCoords (){
        Random rand = new Random();
        int pos = rand.nextInt(puzzlePieceStartingLocations.size());
        if(pos %2 ==1){
            pos-=1;
        }
        ArrayList<Float> ret = new ArrayList<>();
        ret.add(puzzlePieceStartingLocations.get(pos));
        ret.add(puzzlePieceStartingLocations.get(pos+1));
//                {pieceStartingLocations.get(pos), pieceStartingLocations.get(pos + 1)};
        puzzlePieceStartingLocations.remove(pos+1);
        puzzlePieceStartingLocations.remove(pos);
        return ret;
    }

    //calculates the scale that Android Studio uses to fit a image inside a imageview
    private float getScale(){
        ImageView puzzleImage = (ImageView) findViewById(R.id.puzzleImage);
        BitmapDrawable imageDrawable = (BitmapDrawable) puzzleImage.getDrawable();
        Bitmap bitmap = imageDrawable.getBitmap();

        int[] onScreenCoords = new int[2];
        int[] imageViewCoords = new int[2];
        puzzleImage.getLocationOnScreen(imageViewCoords);
        puzzleImage.getLocationOnScreen(onScreenCoords);


        int puzzleImageViewWidth = puzzleImage.getWidth();
        int puzzleImageViewHeight = puzzleImage.getHeight();
        int imageWidth = (int) (bitmap.getWidth());
        int imageHeight = (int) (bitmap.getHeight());

        float scale = 0;
        //calculates the scale depending on of the image is vertical, horizontal or square
        if(imageHeight> imageWidth){
            scale = (float) puzzleImageViewHeight /imageHeight;
        }
        else{
            scale = (float) puzzleImageViewWidth /imageWidth;
        }
        return scale;
    }

    //gets the solution coordinates of each puzzle piece and adds them to the solutionCoords arrayList
    private void getSoltution(View v){

        ImageView puzzleImage = (ImageView) findViewById(R.id.puzzleImage);
        BitmapDrawable imageDrawable = (BitmapDrawable) puzzleImage.getDrawable();
        Bitmap bitmap = imageDrawable.getBitmap();


        int[] onScreenCoords = new int[2];
        v.getLocationOnScreen(onScreenCoords);

        int puzzleImageViewWidth = puzzleImage.getWidth();
        int puzzleImageViewHeight = puzzleImage.getHeight();
        int imageWidth = (int) (bitmap.getWidth());
        int imageHeight = (int) (bitmap.getHeight());

        int yOffset = 0;
        int xOffset = 0;
        float scale = getScale();

        int scaledImageHeight = (int)(imageHeight*scale);
        int scaledImageWidth = (int)(imageWidth*scale);

        //checks if the image is horizontal and calculates the offset that should be applied
        if(scaledImageHeight < puzzleImageViewHeight){
            int totalDiff = abs(scaledImageHeight-puzzleImageViewHeight);
            yOffset = (totalDiff/2);
        }
        //checks if the image is vertical and calculates the offset that should be applied
        if(scaledImageWidth < puzzleImageViewWidth){
            int totalDiff = abs(scaledImageWidth - puzzleImageViewWidth);
            xOffset = (totalDiff/2);
        }

        //loops through and calculate/add the solution coordinates
        int y = yOffset;
        for(int row = 0; row< puzzleDim; row++){
            int x = xOffset;
            for(int col =0; col<puzzleDim; col++){
                solutionCoords.add((float) (onScreenCoords[0]+ x));
                solutionCoords.add((float) (onScreenCoords[1]+ y));
                x+= (scaledImageWidth/puzzleDim);
            }
            y+= scaledImageHeight/puzzleDim;
        }
    }
    private void setBackgroundImage(){
        ImageView puzzleImage = findViewById(R.id.puzzleImage);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        float screenHeight = displayMetrics.heightPixels;
        float screenWidth = displayMetrics.widthPixels;

//        float heightOffset = (float) ((0.25 * screenHeight)/2);
        float widthOffset = (float) (((0.5 * screenWidth)));
        Log.i("SCREEN WIDTH", String.valueOf(widthOffset));
        float setPuzzleImageWidthHeight = (float) ((0.75*screenWidth));

        ViewGroup.LayoutParams layoutParams = puzzleImage.getLayoutParams();
        layoutParams.height = (int) setPuzzleImageWidthHeight;
        layoutParams.width = (int) setPuzzleImageWidthHeight;
        puzzleImage.setLayoutParams(layoutParams);
        puzzleImage.setX(widthOffset);
        Log.i("PUZZLE IMAGE X:", String.valueOf(puzzleImage.getX()));
    }


    //creates the unscaled puzzle pieces
    protected ArrayList<Bitmap> createPieces(){
        //load the imageView
        ImageView puzzleImage = findViewById(R.id.puzzleImage);
        puzzlePieces = new ArrayList<>(totalNumPieces);
        BitmapDrawable imageBitmap = (BitmapDrawable) puzzleImage.getDrawable();
        Bitmap bitmap = imageBitmap.getBitmap();
        //gets the unscaled width and height of the pieces
        int puzzlePieceWidth = bitmap.getWidth()/puzzleDim;
        int puzzlePieceHeight = bitmap.getHeight()/puzzleDim;
        //loops through and creates the custom bitmaps. then adds these bitmaps to puzzlePieces
        int y = 0;
        for(int row = 0; row< puzzleDim; row++){
            int x = 0;
            for(int col =0; col<puzzleDim; col++){
                Bitmap temp = Bitmap.createBitmap(bitmap, x,y,puzzlePieceWidth, puzzlePieceHeight);
                puzzlePieces.add(temp);
                x+= puzzlePieceWidth;
            }
            y+= puzzlePieceHeight;
        }
        return puzzlePieces;
    }

    //scales the unscaled puzzle pieces
    protected ArrayList<ImageView> scalePieces(){
        ArrayList<ImageView> ret = new ArrayList<ImageView>(totalNumPieces);
        ImageView puzzleImage = (ImageView) findViewById(R.id.puzzleImage);
        BitmapDrawable imageBitmap = (BitmapDrawable) puzzleImage.getDrawable();
        Bitmap bitmap = imageBitmap.getBitmap();

        int puzzlePieceWidth = bitmap.getWidth()/puzzleDim;
        int puzzlePieceHeight = bitmap.getHeight()/puzzleDim;
        for(int i =0; i< totalNumPieces; i++){
            Bitmap piece = puzzlePieces.get(i);
            //gets the scale from image matrix and applies it to create the scaled pieces
            float[] f = new float[9];
            puzzleImage.getImageMatrix().getValues(f);

            // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
            final float scaleX = f[Matrix.MSCALE_X];
            final float scaleY = f[Matrix.MSCALE_Y];
            Bitmap resized = Bitmap.createScaledBitmap(piece, (int) (puzzlePieceWidth*scaleX), (int) (puzzlePieceHeight * scaleY), true);
            ImageView temp = new ImageView(getApplicationContext());
            temp.setImageBitmap(resized);

            ret.add(temp);

    }
    return ret;
    }

    public int getPuzzleDim() {
        return puzzleDim;
    }

    // allows users to move the puzzle pieces and checks if the piece is in the correct position. also checks if the puzzle is complete
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        float xDelta = 0;
        float yDelta = 0;


        double[] sol = (double[]) v.getTag();
        final double setx = sol[0];
        final double sety = sol[1];
        int stop = (int) sol[2];
//        }
        if (stop == 1) {
            return true;
        }

        // Get the actual position of the view on screen
        int[] screenCoords = new int[2];
        v.getLocationOnScreen(screenCoords);
        ArrayList<ImageView> ret = new ArrayList<ImageView>(totalNumPieces);
        ImageView puzzleImage = (ImageView) findViewById(R.id.puzzleImage);
        BitmapDrawable imageBitmap = (BitmapDrawable) puzzleImage.getDrawable();
        Bitmap bitmap = imageBitmap.getBitmap();

        int puzzlePieceWidth = bitmap.getWidth()/puzzleDim;
        int puzzlePieceHeight = bitmap.getHeight()/puzzleDim;
        float scale = getScale();
        int scaledWidth = (int) (scale*puzzlePieceWidth);
        int scaledHeight = (int) (scale*puzzlePieceHeight);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xDelta = x - screenCoords[0];
                yDelta = y - screenCoords[1];
                Log.i("on touch", String.valueOf(v.getX()) + " & " + String.valueOf(v.getY()));
                break;

            case MotionEvent.ACTION_MOVE:
                // Update the puzzle pieces position based on the new touch coordinates
                v.setX((x - xDelta)-((float) (scaledWidth /2)));
                v.setY(y - yDelta - ((float)(scaledHeight/2)));
                break;

            case MotionEvent.ACTION_UP:
                // whem the user lets go of a piece, checks the coordinates to see if its in the correct location
                int[] onScreenCoords = new int[2];
                v.getLocationOnScreen(onScreenCoords);
                double xDiff = abs(sol[0] - onScreenCoords[0]);
                double yDiff = abs(sol[1] - onScreenCoords[1]);
                int tolerance = 100/puzzleDim;
                //checks if piece is within a tolerance
                if (xDiff <= tolerance && yDiff <= tolerance) {
                    sol[2] = 1; // updates the puzzle pieces information to mark that it is in the correct place and shouldn't be moved again

                    //snaps the puzzle piece to the correct location
                    int statusbarResourceID = puzzle.this.getResources().getIdentifier("status_bar_height", "dimen", "android");;
                    int statusBarHeight = puzzle.this.getResources().getDimensionPixelSize(statusbarResourceID);
                    v.setX((float) setx);
                    v.setY((float) sety - statusBarHeight);
                    //updates the number of pieces correct
                    numPiecesCorrect++;

                    //correct puzzle pieces become part of the background so other pieces go over it instead of under
                    Drawable puzzlePieceDrawable = ((ImageView)v).getDrawable();
                    v.setBackground(puzzlePieceDrawable);

                    // Check if the puzzle is complete
                    if (numPiecesCorrect == totalNumPieces) {
                        Toast.makeText(puzzle.this, "Congrats! You Won the Game!", Toast.LENGTH_SHORT).show();
//                        try {
//                            Thread.sleep(200); // Wait for a moment before moving to next screen
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }


//                        Intent i = new Intent(puzzle.this, MainActivity.class);
//                        startActivity(i);
//                        ImageView puzzleImag = (ImageView) findViewById(R.id.puzzleImage);
                        Dialog mDialog;
                        mDialog = new Dialog(this);
                        ImageView fullscreenImageView;
                        TextView fullscreenTextView;

                        int widthOfScreen, heightOfScreen;
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        widthOfScreen = displayMetrics.widthPixels;
                        heightOfScreen = displayMetrics.heightPixels;

                        int widthOfDialog = (int) (0.8 * widthOfScreen);
                        int heightOfDialog = (int)(0.8*heightOfScreen);
                        float textSize = widthOfScreen * 0.02f;
//                        ImageView fullscreenImageView = (ImageView) findViewById(R.id.puzzleImage);
                        mDialog.setContentView(R.layout.fullscreen_image);
                        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        fullscreenImageView = mDialog.findViewById(R.id.fullscreenImageView);
                        fullscreenTextView = mDialog.findViewById(R.id.fullscreenTextView);

                        // Set up parameters for pop up based on screen dimensions
                        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                        layoutParams.width = widthOfDialog; // 0.8 * widthOfScreen
                        layoutParams.height = heightOfDialog; // 0.8 * widthOfScreen
                        mDialog.getWindow().setAttributes(layoutParams);

                        // Set up parameters for ImageView and TextView
                        fullscreenImageView.getLayoutParams().width = widthOfDialog;
                        fullscreenImageView.getLayoutParams().height = (int)(heightOfDialog * 0.8);
                        fullscreenTextView.getLayoutParams().width = widthOfDialog;
                        fullscreenTextView.getLayoutParams().height = (int)(heightOfDialog * 0.2);
                        fullscreenTextView.setTextSize(textSize);
                        fullscreenTextView.setText(imageDescription);
                        Drawable drawable = puzzleImage.getDrawable();
                        // Display the current card's image
                        fullscreenImageView.setImageDrawable(drawable);
                        mDialog.show();
                    }
                }
                break;
        }
        return true;
    }
    // Gets the game difficulty setting and image url passed by makeIntent method
    private void extractDifficulty() {
        Intent i = getIntent();
        difficulty = i.getIntExtra(GAME_DIFFICULTY, 1);
        imageUrl = i.getStringExtra("image url id");
        imageDescription = i.getStringExtra("image description id");
    }

    // Create custom make intent function to pass difficulty setting from games fragment
    public static Intent makeIntent(Context context, int difficulty, String imageUrl, Map<String, String> puzzleGameUrlToDescription){
        Intent i = new Intent(context, puzzle.class);
        i.putExtra(GAME_DIFFICULTY, difficulty);
        i.putExtra("image url id", imageUrl);
        String description = puzzleGameUrlToDescription.get(imageUrl);
        i.putExtra("image description id", description);
        return i;
    }
}
