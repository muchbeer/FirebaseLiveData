package muchbeer.raum.firebaselivedata.viewmodel;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import muchbeer.raum.firebaselivedata.model.Entity;
import muchbeer.raum.firebaselivedata.ui.MessageListFragment;

public class MessageListViewModel extends ViewModel {


    private static final DatabaseReference dataRef =
            FirebaseDatabase.getInstance().getReference().child("messagelivedata");
    private static final String LOG_DATA = MessageListFragment.class.getSimpleName();

    private List<Entity> mList = new ArrayList<>();

    @NonNull
    public LiveData<List<Entity>> getMessageListLiveData() {

        FirebaseQueryLiveData queryLiveData = new FirebaseQueryLiveData(dataRef);

        LiveData<List<Entity>> getMessageLive = Transformations.map(queryLiveData,
                                            new Deserializer());

                return getMessageLive;
    }

    private class Deserializer implements Function<DataSnapshot, List<Entity>> {

        @Override
        public List<Entity> apply(DataSnapshot dataSnapshot) {
            mList.clear();
            for(DataSnapshot snap : dataSnapshot.getChildren()){
                Entity msg = snap.getValue(Entity.class);
                mList.add(msg);
            }
            return mList;
        }
    }

    private String mPhoto = "";

    private final MutableLiveData<Boolean> pictureUploadIsSuccessful = new MutableLiveData<>();
    private final MutableLiveData<Boolean> messageUploadIsSuccessful = new MutableLiveData<>();

    public void setPhotoUrl(String photoUrl){
        mPhoto = photoUrl;
    }

    public String getPhotoUrl(){
        return mPhoto;
    }

    //NewPostFragment
    public MutableLiveData<Boolean> getPictureUploadIsSuccessful(){
        return pictureUploadIsSuccessful;
    }

    public MutableLiveData<Boolean> getMessageUploadIsSuccessful(){
        return messageUploadIsSuccessful;
    }

    public void createAndSendToDataBase(String userName, String descriptionText,String mPhoto){
        Entity entity = new Entity(descriptionText,userName,mPhoto);

        // push the new message to Firebase
        Task uploadTask = FirebaseDatabase.getInstance()
                .getReference()
                .child("messagelivedata")
                .push()
                .setValue(entity);
        uploadTask.addOnSuccessListener(o -> messageUploadIsSuccessful.setValue(true));
    }

    public void uploadPicture(Intent intentData){
        Uri selectedUri = intentData.getData();
        StorageReference photoRef = FirebaseStorage.getInstance()
                .getReference().child("messagelivephoto")
                .child(selectedUri.getLastPathSegment());

        UploadTask uploadTask = photoRef.putFile(selectedUri);

        Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()) {
                    Log.d(LOG_DATA,  "Something went wrong here " );
                    throw task.getException();

                }

                return photoRef.getDownloadUrl();
            }
        });

        getDownloadUriTask.addOnCompleteListener((Executor) this, new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {

                    Uri downloadUri = task.getResult();
                   // deal.setImageUrl(downloadUri.toString());
                    // deal.setImageName(pictureName);
                  //  showImage(downloadUri.toString());
                    mPhoto = String.valueOf(photoRef.getDownloadUrl());
                    pictureUploadIsSuccessful.setValue(true);
                    Log.d(LOG_DATA, "The best link url now is : " + downloadUri);
                } else {

                }
            }
        });

getDownloadUriTask.addOnFailureListener(e -> {
    pictureUploadIsSuccessful.setValue(false);
});
       /* uploadTask.addOnSuccessListener(taskSnapshot -> {
            mPhoto = String.valueOf(taskSnapshot.getDownloadUrl());
            pictureUploadIsSuccessful.setValue(true);
        });
        uploadTask.addOnFailureListener(e -> pictureUploadIsSuccessful.setValue(false));*/
    }
}
