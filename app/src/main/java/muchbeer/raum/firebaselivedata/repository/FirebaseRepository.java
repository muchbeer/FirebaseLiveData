package muchbeer.raum.firebaselivedata.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseRepository {

    private static  FirebaseRepository sInstance;

    public String mUsername;
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mMessageDbReference;

    public FirebaseRepository() {
        mFirebaseDb = FirebaseDatabase.getInstance();
        mMessageDbReference = mFirebaseDb.getReference();
      }

    public static FirebaseRepository getInstance() {
        if (sInstance == null) {
            synchronized (FirebaseRepository.class) {
                if (sInstance == null) {
                    sInstance = new FirebaseRepository();
                }
            }
        }
        return sInstance;
    }

    public FirebaseDatabase getFirebaseDbReference(){
        return mFirebaseDb;
    }

    public void setUserName(String userName){
        mUsername = userName;
    }

    public String getUserName(){
        return mUsername;
    }
}
