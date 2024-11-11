package ca.sfu.memoryhub;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class puzzle extends AppCompatActivity{
    private boolean puzzleComeplete = false;
//    private Bitmap[] piece;
    private ArrayList<Bitmap> pieces;
    private int puzzleDim = 2; // This is the x and y for puzzles
    private int numPiecesCorrect = 0;
    final private int totalNumPieces = getPuzzleDim()*getPuzzleDim();

    private ArrayList<Double> coords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_game);

        final RelativeLayout layout = findViewById(R.id.layout);
        ImageView puzzleImage = (ImageView) findViewById(R.id.puzzleImage);

        puzzleImage.post(new Runnable() {
            @Override
            public void run() {
                pieces = createPieces();
                TouchListener touchListener = new TouchListener();
                ArrayList<ImageView> piecesToAdd = scalePieces();
                addSoltution(puzzleImage);
                int idNum =0;

                for( ImageView piece: piecesToAdd){
                    double[] sol = {coords.get(idNum), coords.get(idNum+1), 0.0};
//                    ImageView temp = new ImageView(getApplicationContext());
//                    temp.setImageBitmap(piece);
                    piece.setTag(sol);
                    piece.setOnTouchListener(touchListener);

                    layout.addView(piece);
                    idNum+=2;
                }
            }
        });


    }
    private void addSoltution(View v){

        ImageView puzzleImage = (ImageView) findViewById(R.id.puzzleImage);
        BitmapDrawable imageBitmap = (BitmapDrawable) puzzleImage.getDrawable();
        Bitmap bitmap = imageBitmap.getBitmap();

        int puzzlePieceWidth = bitmap.getWidth()/puzzleDim;
        int puzzlePieceHeight = bitmap.getHeight()/puzzleDim;
        int y =0;



        float[] f = new float[9];
        puzzleImage.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        int[] onScreenCoords = new int[2];
        v.getLocationOnScreen(onScreenCoords);
        Log.i("Location", Arrays.toString(onScreenCoords));
        for(int row = 0; row< puzzleDim; row++){
            int x = 0;
            for(int col =0; col<puzzleDim; col++){
//                PuzzlePiece piece = new PuzzlePiece(getApplicationContext());
                coords.add((double) (onScreenCoords[0]+ x));
                coords.add((double)(onScreenCoords[1]+ y));

                x+= (int) (puzzlePieceWidth);
            }
            y+= (int) (puzzlePieceHeight);
        }
        Log.i("COORDS", String.valueOf(puzzlePieceWidth) + " & " + String.valueOf(puzzlePieceHeight));
        Log.i("COORDS", String.valueOf(coords));
//        Log.i("COORDS", String.valueOf(coords));
    }

    protected boolean isPuzzleCompleted(){
        int puzzleLength = getPuzzleDim();
        if( totalNumPieces == numPiecesCorrect){
            puzzleComeplete = true;
        }
        return puzzleComeplete;
    }

    protected ArrayList<Bitmap> createPieces(){
        //load the imageView
        ImageView puzzleImage = (ImageView) findViewById(R.id.puzzleImage);

        pieces = new ArrayList<>(totalNumPieces);


        BitmapDrawable imageBitmap = (BitmapDrawable) puzzleImage.getDrawable();
        Bitmap bitmap = imageBitmap.getBitmap();

        int puzzlePieceWidth = bitmap.getWidth()/puzzleDim;
        int puzzlePieceHeight = bitmap.getHeight()/puzzleDim;

        int y = 0;
        for(int row = 0; row< puzzleDim; row++){
            int x = 0;
            for(int col =0; col<puzzleDim; col++){
//                PuzzlePiece piece = new PuzzlePiece(getApplicationContext());
                Bitmap temp = Bitmap.createBitmap(bitmap, x,y,puzzlePieceWidth, puzzlePieceHeight);
                pieces.add(temp);

                x+= puzzlePieceWidth;
            }
            y+= puzzlePieceHeight;
        }
//        pieces.add(Bitmap.createBitmap(bitmap,0,0,10,10));
//        pieces.add(Bitmap.createBitmap(bitmap,10,0,10,10));

        return pieces;
    }

    protected ArrayList<ImageView> scalePieces(){
            ArrayList<ImageView> ret = new ArrayList<ImageView>(totalNumPieces);
        ImageView puzzleImage = (ImageView) findViewById(R.id.puzzleImage);

//        pieces = new ArrayList<>(totalNumPieces);


        BitmapDrawable imageBitmap = (BitmapDrawable) puzzleImage.getDrawable();
        Bitmap bitmap = imageBitmap.getBitmap();

        int puzzlePieceWidth = bitmap.getWidth()/puzzleDim;
        int puzzlePieceHeight = bitmap.getHeight()/puzzleDim;
    for(int i =0; i< totalNumPieces; i++){
        Bitmap piece = pieces.get(i);
//        BitmapDrawable d = (BitmapDrawable) piece.getDrawable();
//        Bitmap b = (Bitmap)d.getBitmap();
        float[] f = new float[9];
        puzzleImage.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];
        Log.i("SCALED PIECES", String.valueOf((int) (puzzlePieceWidth)) + "  &  " +String.valueOf( (int) (puzzlePieceHeight)));
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

    public void setPuzzleDim(int puzzleDim) {
        this.puzzleDim = puzzleDim;
    }
}
