package sample;

public class User {

    //smisliti elegantniji nacin za rjesiti ovo
    //private final String path = "/home/ognjen/IdeaProjects/KriptoProject/root/";
    private String username;
    private String password;

    User(){}

    public String getPath(){ return "/home/ognjen/IdeaProjects/KriptoProject/root/"; }

    public String getUsername(){
        return this.username;
    }
    public String getPassword(){
        return this.password;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }

}
