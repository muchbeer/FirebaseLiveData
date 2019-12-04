package muchbeer.raum.firebaselivedata.model;

public class Entity implements Message {

    private String text;
    private String userName;
    private String photoUrl;


    public Entity(){};

    public Entity(String text, String userName,String photoUrl) {
        this.text = text;
        this.userName = userName;
        this.photoUrl = photoUrl;
    }
    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String name) {
        this.userName = name;
    }

    @Override
    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
