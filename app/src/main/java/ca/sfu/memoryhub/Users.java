package ca.sfu.memoryhub;

public class Users {
    String username;
    int difficulty; //Match game difficulty setting

    //Constructors
    public Users() {

    }
    public Users(String username) {
        this.username = username;
        this.difficulty = 1; //Default difficulty is medium
    }

    //Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setMatchDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
