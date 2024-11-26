package ca.sfu.memoryhub;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;



/**
 * Represents a card in the game, containing the card's image, location, and matching information.
 */
public class Card {

    private int location;
    private Context context;
    private int matchingLoc;
    private ImageView img;
    private String imgResource;
    boolean showCard;
    boolean correct;
    String description;


    //Constructor
    public Card(int location, int matchingLoc, ImageView img,
                String imgResource, boolean showCard, boolean correct, Context context, String description) {
        this.location = location;
        this.matchingLoc = matchingLoc;
        this.img = img;
        this.showCard = showCard;
        this.imgResource = imgResource;
        this.correct = correct;
        this.context = context;
        this.description = description;
    }

    //Getters and Setters
    public int getLoc() {
        return location;
    }

    public void setLoc(int location) {
        this.location = location;
    }

    public int getMatchingLoc() {
        return matchingLoc;
    }

    public void setMatchingLoc(int matchingLoc) {
        this.matchingLoc = matchingLoc;
    }
    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public String getImgResource() {
        return imgResource;
    }

    public void setImgResource(String imgResource) {
        this.imgResource = imgResource;
    }

    public boolean isShowCard() {
        return showCard;
    }

    public void setShowCard(boolean showCard) {
        this.showCard = showCard;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //Methods

    /**
     * Flips the card's image. If the card is showing its image, it flips to the back of the card;
     * if it's showing the back, it flips to the card's image. This function also has built-in
     * rotation animation.
     */
    public void flipImg() {
        if(this.showCard){
            //If the card is facing up, flip to the back of the card
            this.img.animate().setDuration(300).rotationYBy(-90f).start();
            this.img.setImageResource(R.drawable.sharpcornercard);
            this.img.setRotationY(90f);
            this.img.animate().setDuration(300).rotationYBy(-90f).start();
        }else{
            //If the back of the card is showing, show the card's image
            this.img.animate().setDuration(300).rotationYBy(90f).start();
            // Puts the imaage resource into the imageView
            Glide.with(context).load(imgResource).into(img);
            this.img.setRotationY(-90f);
            this.img.animate().setDuration(300).rotationYBy(90f).start();
        }
        this.showCard = !this.showCard;
    }
}

