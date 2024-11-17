package ca.sfu.memoryhub;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class puzzle extends AppCompatActivity implements View.OnTouchListener{
    //puzzlePieces stores the bitmaps of all puzzle pieces
    private ArrayList<Bitmap> puzzlePieces;
    //puzzlePieceStartingLocations stores all the coordinates of where the pieces should start on the screen
    private ArrayList<Float> puzzlePieceStartingLocations = new ArrayList<>();

    // puzzleDim is the Length & Width of the puzzle. Puzzle lengths will always be even
    private int puzzleDim = 3;
    //numPiecesCorrect stores the amount of pieces that have been put in the correct spot
    private int numPiecesCorrect = 0;
    //totalNumPieces stores the total number of pieces in the puzzle
    final private int totalNumPieces = getPuzzleDim()*getPuzzleDim();
    //solutionCoords stores all the correct x & y coordinates for each puzzle piece. the coordinates are the top-left of each piece
    private ArrayList<Float> solutionCoords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_game); // sets the puzzle layout

        Button exitButton = findViewById(R.id.exitButton); // gets the exit button

        //exits the puzzle game if user clicks on the exit button
        exitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //gets the relative layout used in the .xml
        final RelativeLayout layout = findViewById(R.id.layout);

        ImageView puzzleImage = (ImageView) findViewById(R.id.puzzleImage);

        puzzleImage.post(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
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


    }
    //adds different starting locations for the puzzle pieces
    private void setPieceStartingLocations(){
        puzzlePieceStartingLocations.clear();//done as a pre-caution-> not necessary
        ImageView imageview = findViewById(R.id.puzzleImage);
        BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
        Bitmap bitmap = (Bitmap) drawable.getBitmap();


        int coords[] = new int[2];
        //gets the onscreen x and y coordinates (top-left) of the imageView
       imageview.getLocationOnScreen(coords);
       //gap between the puzzle pieces when starting will be 40 px
       int gap = 40;
       //gets the scale that should be used
       float scale = getScale();
       //calculates the number of gaps that should exist
       int numOfGaps = puzzleDim-1;
       //calculates the scaled width and hieght of the image
       int scaledImageWidth = (int) (scale * bitmap.getWidth());
       int puzzleImageViewWidth = imageview.getWidth();
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
           int yStart = (coords[1] + imageview.getHeight()) ;
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

    //creates the unscaled puzzle pieces
    protected ArrayList<Bitmap> createPieces(){
        //load the imageView
        ImageView puzzleImage = (ImageView) findViewById(R.id.puzzleImage);
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
            //gets the scale from image matrix and appleis it to create the scaled pieces
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

        if (stop == 1) {
            return true;
        }

        // Get the actual position of the view on screen
        int[] screenCoords = new int[2];
        v.getLocationOnScreen(screenCoords);


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xDelta = x - screenCoords[0];
                yDelta = y - screenCoords[1];
                break;

            case MotionEvent.ACTION_MOVE:
                // Update the puzzle pieces position based on the new touch coordinates
                v.setX(x - xDelta);
                v.setY(y - yDelta);
                break;

            case MotionEvent.ACTION_UP:
                // whem the user lets go of a piece, checks the coordinates to see if its in the correct location
                int[] onScreenCoords = new int[2];
                v.getLocationOnScreen(onScreenCoords);
                double xDiff = abs(sol[0] - onScreenCoords[0]);
                double yDiff = abs(sol[1] - onScreenCoords[1]);
                int tolerance = 50;
                //checks if piece is within a tolerance
                if (xDiff <= tolerance && yDiff <= tolerance) {
                    sol[2] = 1; // updates the puzzle pieces information to mark that it is in the correct place and shouldn't be moved again

                    //snaps the puzzle piece to the correct location
                    v.setX((float) setx);
                    v.setY((float) sety - 60);
                    //updates the number of pieces correct
                    numPiecesCorrect++;

                    //correct puzzle pieces become part of the background so other pieces go over it instead of under
                    Drawable puzzlePieceDrawable = ((ImageView)v).getDrawable();
                    v.setBackground(puzzlePieceDrawable);

                    // Check if the puzzle is complete
                    if (numPiecesCorrect == totalNumPieces) {
                        Toast.makeText(puzzle.this, "Congrats! You Won the Game!", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(200); // Wait for a moment before moving to next screen
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Intent i = new Intent(puzzle.this, MainActivity.class);
                        startActivity(i);
                    }
                }
                break;
        }
        return true;
    }
}
