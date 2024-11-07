package ca.sfu.memoryhub;

import android.widget.ImageView;

public class Card {

    private int location;
    private int matchingLoc;
    private ImageView img;
    private int imgResource;
    boolean showCard;
    boolean correct;


    //Constructor
    public Card(int location, int matchingLoc, ImageView img,
                int imgResource, boolean showCard, boolean correct) {
        this.location = location;
        this.matchingLoc = matchingLoc;
        this.img = img;
        this.showCard = showCard;
        this.imgResource = imgResource;
        this.correct = correct;
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

    public int getImgResource() {
        return imgResource;
    }

    public void setImgResource(int imgResource) {
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

    //Methods
    public void flipImg() {
        if(this.showCard){
            //If the card is facing up, show the back of the card
            this.img.setImageResource(R.drawable.sample_card);
        }else{
            //If the back of the card is showing, show the card's image
            this.img.setImageResource(this.imgResource);
        }
        this.showCard = !this.showCard;
    }
}
