package com.martin.myhelper.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.martin.myhelper.R;
import com.martin.myhelper.model.ElderlyModel;
import com.martin.myhelper.model.GenericModel;
import com.martin.myhelper.model.VolunteerModel;
import com.martin.myhelper.views.ElderlyHomeActivity;
import com.martin.myhelper.views.LoginActivity;
import com.martin.myhelper.views.VolunteerHomeActivity;
import com.martin.myhelper.views.VolunteerRegistrationActivity;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_FAILED_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_FAILED_TITLE;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_TITLE;
import static com.martin.myhelper.helpers.Utility.PASSWORD_RESET_TITLE;

public class ElderlyVolunteerCRUDHelper extends Activity {

    private static final String STORAGE_REF_PROFILE_IMAGES_CONTAINER = "profile-images";
    private boolean isUserElder = false;
    private boolean imageUploadIsSuccess = false;
    private boolean isUserCreated = false;
    private boolean isProfileCreated = false;
    private static boolean emailIsSent = false;
    private boolean isUserLoginSuccessful;

    private EditText firstName, lastName, email, mobileNumber;
    public static Uri profileImageUri;
    //private ImageView profileImage;
    //public ImageView profileImageView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private GenericModel genericModel = new GenericModel();
    private OpenActivity openActivity;
    private Utility utility;
    private ProgressBar progressBar;

    @ServerTimestamp Date time;

    public void createUserRecord(final AppCompatActivity appCompatActivity, final String[] modelArray){

        firebaseAuth = Utility.getFirebaseAuthenticationInstance();

        /*if (isUserTheCurrentFirebaseUser() == false){

            // check if user email exists
            if (getCurrentUserEmail() == modelArray[2]) {
                Utility.showInformationDialog("RECORD EXISTS", "The user account already exist and cannot be created.", appCompatActivity);
                 return;
            }

        } else {*/

            // create the user account via firebase auth if not null
            firebaseAuth.createUserWithEmailAndPassword(modelArray[2], modelArray[4]).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Log.d("URI", modelArray[6]);
                    // create the user profile
                    createElderlyOrVolunteerRecord(appCompatActivity, modelArray, Uri.parse(modelArray[6]));

                } else {
                    Utility.showInformationDialog("ERROR!", task.getException().getMessage(), appCompatActivity);
                    isUserCreated = false;
                    return;
                }
                }
            });
        //}

    }

    private void createElderlyOrVolunteerRecord(final AppCompatActivity appCompatActivity, final String[] _modelArray, Uri profileImageUri){

        String collectionName = "";
        final int  USER_TYPE = Integer.parseInt(_modelArray[5]);

        if (USER_TYPE == GenericModel.USER_TYPE_ELDER){
            collectionName = GenericModel.ELDERS;
        } if (USER_TYPE == GenericModel.USER_TYPE_VOLUNTEER){
            collectionName = GenericModel.VOLUNTEERS;
        }

        // create an instance of the DocumentReference class of FirebaseStore
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        DocumentReference documentReference = firebaseFirestore.collection(collectionName).document(this.getCurrentUserID());

        // create a hash map of the object to be stored
        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put("userID", this.getCurrentUserID());
        modelMap.put("firstName", _modelArray[0]);
        modelMap.put("lastName", _modelArray[1]);
        modelMap.put("email", _modelArray[2]);
        modelMap.put("mobileNumber", _modelArray[3]);
        modelMap.put("userType", _modelArray[5]);
        /*if (USER_TYPE == GenericModel.USER_TYPE_VOLUNTEER){
            modelMap.put("profileImageUri", "");
        }*/
        modelMap.put("createdAt", FieldValue.serverTimestamp());
        modelMap.put("updatedAt", FieldValue.serverTimestamp());


        if (USER_TYPE == GenericModel.USER_TYPE_ELDER ) {

            documentReference.set(modelMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    // send an e-mail for verification
                    boolean _emailIsSent = isVerificationEmailAtRegistrationSent(appCompatActivity);

                    if (!_emailIsSent) {
                        Utility.showInformationDialog("E-MAIL NOT DELIVERED", CREATE_RECORD_SUCCESS_MSG + "\n" + Utility.CREATE_RECORD_EMAIL_FAILURE_MSG, appCompatActivity);
                    } else {
                        Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG , appCompatActivity);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
                    return;
                }
            });


        } else if ( USER_TYPE == GenericModel.USER_TYPE_VOLUNTEER) {

            // user is a volunteer
            //1. create the image record first
            //2. create the volunteer record next on success of 1.
            //if (volunteerRegistrationActivity.storageTask != null && volunteerRegistrationActivity.storageTask.isInProgress()) {
                //Utility.showInformationDialog("DUPLICATE IMAGE", "There is an existing image for your photograph. Please change and try again", appCompatActivity);
                //return;
            //} else {
                this.uploadImageToFirebase(profileImageUri, _modelArray[7], documentReference, modelMap, appCompatActivity);
            //}
        }

    }

    // this method upload the file into FireStorage database
    private  void uploadImageToFirebase(Uri profileImageUri, String imageExtension, final DocumentReference documentReference, final  Map<String, Object> modelMap, final AppCompatActivity appCompatActivity){
        utility = new Utility();

        if (profileImageUri != null){

            Log.d("EXTENSION", imageExtension);

            storageReference = FirebaseStorage.getInstance().getReference("images"); //Utility.getFirebaseStorageReference();
            StorageReference fileReference = storageReference.child(this.getCurrentUserID() + "." + imageExtension);
            //storageReference.child("images/volunteers/" + this.getCurrentUserID() + "." + imageExtension);

            //volunteerRegistrationActivity.storageTask =
            fileReference.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //VolunteerRegistrationActivity.progressBar.setProgress(0);
                        }
                    }, 500);

                    // create the volunteer record including adding the profileUrl after saving the image
                    modelMap.put("profilePhotoUrl", taskSnapshot.getUploadSessionUri().toString());// set the image path from the taskSnapshot
                    modelMap.put("createdAt", FieldValue.serverTimestamp());
                    modelMap.put("updatedAt", FieldValue.serverTimestamp());
                    documentReference.set(modelMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            // send an e-mail for verification
                            boolean _emailIsSent = isVerificationEmailAtRegistrationSent(appCompatActivity);
                            /*if (_emailIsSent == false) {
                                Utility.showInformationDialog("E-MAIL NOT DELIVERED", CREATE_RECORD_SUCCESS_MSG + "\n" + Utility.CREATE_RECORD_EMAIL_FAILURE_MSG, appCompatActivity);
                            } else {*/
                                Utility.showInformationDialog(CREATE_RECORD_SUCCESS_TITLE, CREATE_RECORD_SUCCESS_MSG , appCompatActivity);
                            //}

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
                            return;
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // show error message when it fails to save the image
                }
            });/*.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar  = findViewById(R.id.volunteerRegistrationProgressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    double progressValue = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progressValue);
                }
            });*/

        }
    }


    public void updateProfileRecord(final Context sourceActivity, final Class destinationActivity, String[] _modelArray, final AppCompatActivity appCompatActivity){
        ElderlyModel elderlyModel = new ElderlyModel();
        VolunteerModel volunteerModel = new VolunteerModel();

        //1. get the current user record and pass to an intent
        Intent intent = new Intent(sourceActivity, destinationActivity);
        this.setProfileDataToIntent(intent, _modelArray);

        // start the intent
        startActivity(intent);

        //2.get the extra data from intent and pass to model
        //this.getUserType(GenericModel.ELDERS);
        Intent extraIntentData = getIntent();

        if(GenericModel.GLOBAL_USERTYPE == GenericModel.USER_TYPE_ELDER){

            elderlyModel.setElderlyId(extraIntentData.getStringExtra("elderlyId"));
            elderlyModel.setFirstName(extraIntentData.getStringExtra("firstName"));
            elderlyModel.setLastName(extraIntentData.getStringExtra("lastName"));
            elderlyModel.setEmail(extraIntentData.getStringExtra("email"));
            elderlyModel.setMobileNumber(extraIntentData.getStringExtra("mobileNumber"));
            elderlyModel.setMobileNumber(extraIntentData.getStringExtra("userType"));

        } else {

            volunteerModel.setVolunteerID(extraIntentData.getStringExtra("volunteerId"));
            volunteerModel.setFirstName(extraIntentData.getStringExtra("firstName"));
            volunteerModel.setLastName(extraIntentData.getStringExtra("lastName"));
            volunteerModel.setEmail(extraIntentData.getStringExtra("email"));
            volunteerModel.setMobileNumber(extraIntentData.getStringExtra("mobileNumber"));
            volunteerModel.setMobileNumber(extraIntentData.getStringExtra("profileImage"));
            volunteerModel.setMobileNumber(extraIntentData.getStringExtra("userType"));
        }

        //4. set the value from the model to the edit text fields
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        mobileNumber = findViewById(R.id.mobileNumber);
        //userType = findViewById(R.id.userType);
        //profileImage = findViewById(R.id.profileImage);

        if(GenericModel.GLOBAL_USERTYPE == GenericModel.USER_TYPE_ELDER){

            firstName.setText(elderlyModel.getFirstName());
            lastName.setText(elderlyModel.getLastName());
            //email.setText(elderlyModel.getEmail());
            mobileNumber.setText(elderlyModel.getMobileNumber());
            //userType.setText(elderlyModel.getUserType());
        } else {
            firstName.setText(volunteerModel.getFirstName());
            lastName.setText(volunteerModel.getLastName());
            //email.setText(volunteerModel.getEmail());
            mobileNumber.setText(volunteerModel.getMobileNumber());
            //userType.setText(volunteerModel.getUserType());
            //profileImage.setImageURI(Uri.parse(volunteerModel.getProfileImage().toString()));
        }

        String tableAsCollectionName = "";
        if( GenericModel.GLOBAL_USERTYPE == GenericModel.USER_TYPE_ELDER){
            tableAsCollectionName = GenericModel.ELDERS;
        } else {
            tableAsCollectionName = GenericModel.VOLUNTEERS;
        }

        // create an instance of the DocumentReference class of FirebaseStore
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        DocumentReference documentReference = firebaseFirestore.collection(tableAsCollectionName).document(getCurrentUserID());

        // create a hash map of the object to be stored
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("firstName", firstName.getText().toString());
        modelMap.put("lastName", lastName.getText().toString());
        modelMap.put("email", email.getText().toString());
        modelMap.put("mobileNumber", mobileNumber.getText().toString());
        //modelMap.put("userType", userType.getText().toString());
        //modelMap.put("profileImage", profileImage.toString());

        documentReference.update(modelMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utility.showInformationDialog("RECORD UPDATE", "You have successfully updated your account information", appCompatActivity);

                // take user to his home page
                //openActivity.openAnActivityScreen(sourceActivity,destinationActivity);
                Intent intent = new Intent(sourceActivity, destinationActivity);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog("RECORD UPDATE", "Update of your account failed. " + e.getMessage(), appCompatActivity);
                return;
            }
        });
    }

    public void updateUserEmail(final String _email, final AppCompatActivity appCompatActivity, final Context sourceActivity, final Class destinationActivity ){

        firebaseUser = getFireBaseUser();
        firebaseUser.updateEmail(_email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utility.showInformationDialog("E-MAIL CHANGE", "You have successfully changed your e-mail. You have to re-login to the app", appCompatActivity);
                firebaseAuth.signOut();

                //openActivity.openAnActivityScreen(sourceActivity, destinationActivity);
                Intent intent = new Intent(sourceActivity, destinationActivity);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog("UPDATE E-MAIL", "Email failed to update " + e.getMessage(), appCompatActivity);
                return;
            }
        });
    }

    public void requestPasswordReset(final String _email, final AppCompatActivity appCompatActivity, final Class destinationActivity){

        Utility.promptUserBeforePasswordResetLinkIsSent(_email, appCompatActivity, destinationActivity);
    }

    public void sendPasswordResetLink(final String _email, final AppCompatActivity appCompatActivity, final Class destinationActivity){
        firebaseAuth.sendPasswordResetEmail(_email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // sign the user out
                firebaseAuth.signOut();

                // redirect user to login
                //openActivity.openAnActivityScreen(appCompatActivity, destinationActivity);
                Intent intent = new Intent(appCompatActivity, destinationActivity);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog("RESET PASSWORD", "Error sending PASSWORD RESET link. " + e.getMessage(), appCompatActivity);
            }
        });
    }

    private void setProfileDataToIntent(Intent intent, String[] _modelArray){
        //this.getUserType(GenericModel.ELDERS);
        if( GenericModel.GLOBAL_USERTYPE == GenericModel.USER_TYPE_ELDER){
            intent.putExtra("firstName", _modelArray[0]);
            intent.putExtra("lastName", _modelArray[1]);
            intent.putExtra("email", _modelArray[2]);
            intent.putExtra("mobileNumber", _modelArray[3]);
            intent.putExtra("userType", _modelArray[4]);

        } else {

            intent.putExtra("firstName", _modelArray[0]);
            intent.putExtra("lastName", _modelArray[1]);
            intent.putExtra("email", _modelArray[2]);
            intent.putExtra("mobileNumber", _modelArray[3]);
            intent.putExtra("userType", _modelArray[4]);
            intent.putExtra("profileImage", _modelArray[5]);
        }
    }

    public void uploadProfilePhoto(String imageUrl){

    }

    public void showUserProfileImage(final ImageView imageView){
        // create a child reference to the
        storageReference = Utility.getFirebaseStorageReference();
        storageReference.child( STORAGE_REF_PROFILE_IMAGES_CONTAINER + "/" + getCurrentUserID() ).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        });
    }

    public String getCurrentUserID(){
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        return firebaseAuth.getCurrentUser().getUid();
    }

    public String getCurrentUserID(FirebaseAuth firebaseAuth){
        return firebaseAuth.getCurrentUser().getUid();
    }

    /***
     * Gets or fetches a user's record from the database
     * @param collectionName
     */
    public void getCurrentUserRecord(String collectionName){

        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        DocumentReference documentReference = firebaseFirestore.collection(collectionName).document(this.getCurrentUserID());

        documentReference.addSnapshotListener((Executor) this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String userType = value.getString("userType");

                // if type of user is elderly
               if( Integer.parseInt(userType) == genericModel.USER_TYPE_ELDER ){

                   // create an instance of the elderly model class
                   ElderlyModel elderlyModel = new ElderlyModel();

                   elderlyModel.setElderlyId(value.getId());
                   elderlyModel.setFirstName(value.getString("firstName"));
                   elderlyModel.setLastName(value.getString("lastName"));
                   elderlyModel.setEmail(value.getString("email"));
                   elderlyModel.setUserType(Integer.parseInt(value.getString(" userType")));
                   elderlyModel.setMobileNumber(value.getString(" mobileNumber"));

               } else if( Integer.parseInt(userType) == genericModel.USER_TYPE_ADMIN ){
                   // the user is a volunteer

                   // create an instance of the volunteer model class
                   VolunteerModel volunteerModel = new VolunteerModel();

                   volunteerModel.setVolunteerID(value.getId());
                   volunteerModel.setFirstName(value.getString("firstName"));
                   volunteerModel.setLastName(value.getString("lastName"));
                   volunteerModel.setEmail(value.getString("email"));
                   volunteerModel.setUserType(Integer.parseInt(value.getString(" userType")));
                   volunteerModel.setMobileNumber(value.getString(" mobileNumber"));
               }
            }
        });
    }

    public String getCurrentUserEmail(){
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        return firebaseUser.getEmail();
    }


    /***
     * Gets the current user and checks if the UserType is an Elder or a Volunteer.
     * @param modelTableAsCollection
     * @return true if the user is an elder else returns false
     */
    /*public int getUserType(String modelTableAsCollection){
        Log.d("USERID", this.getCurrentUserID() + " " + modelTableAsCollection);
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        DocumentReference documentReference = firebaseFirestore.collection(modelTableAsCollection).document(this.getCurrentUserID());
        documentReference.addSnapshotListener((Executor) this, new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userType = Integer.parseInt(value.getString("userType"));
            }
        });

        return userType;
    }*/

    public void getUserType(String modelTableAsCollection, final Context sourceActivity){

        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        final DocumentReference documentReference = firebaseFirestore.collection(modelTableAsCollection).document(this.getCurrentUserID());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){

                        String ut = documentSnapshot.getString("userType");

                        //openActivity = new OpenActivity();
                        if (Integer.parseInt(ut) == GenericModel.USER_TYPE_ELDER){
                            //openActivity.openAnActivityScreen(sourceActivity, ElderlyHomeActivity.class);
                            Intent intent = new Intent(sourceActivity, ElderlyHomeActivity.class);
                            startActivity(intent);
                        } if (Integer.parseInt(ut) == GenericModel.USER_TYPE_VOLUNTEER){
                            //openActivity.openAnActivityScreen(sourceActivity, VolunteerHomeActivity.class);
                            Intent intent = new Intent(sourceActivity, ElderlyHomeActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }


    /***
     * Sends an email to a User after account is created or when it is called
     * @param appCompatActivity
     */
    public void reSendVerificationEmail(final AppCompatActivity appCompatActivity){

        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utility.showInformationDialog("E-MAIL SENT",
                        "A verification e-mail is sent to your inbox to activate your account.",
                        appCompatActivity);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog("E-MAIL NOT SENT", e.getMessage(), appCompatActivity);
                return;
            }
        });
    }

    public boolean isVerificationEmailAtRegistrationSent(final AppCompatActivity appCompatActivity){

        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

       if (!checkIfEmailIsVerify()){

           firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                   emailIsSent = true;
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   emailIsSent = false;
               }
           });
       }

        return  emailIsSent;
    }

    /***
     * Checks if a User has verified his email after registration
     * @return boolean
     */
    public Boolean checkIfEmailIsVerify(){
        boolean isVerified;

        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser.isEmailVerified()){
            isVerified = true;
        } else isVerified = false;

        return isVerified;
    }

    /***
     *
     * @return
     */
    public FirebaseUser getFireBaseUser(){
        return  firebaseAuth.getCurrentUser();
    }

    /***
     *
     * @param sourceActivity
     * @param destinationActivity
     * @param appCompatActivity
     */
    public void logOutFireStoreUser(Context sourceActivity, Class destinationActivity, AppCompatActivity appCompatActivity){
        firebaseAuth.signOut();

        //openActivity.openAnActivityScreen(sourceActivity, destinationActivity);
        Intent intent = new Intent(sourceActivity, destinationActivity);
        startActivity(intent);

        Utility.showInformationDialog("SIGN OUT", "You have successfully signed out from the application", appCompatActivity);
    }




    /***
     * Sends a link for a user to reset his/her password
     * @param _email
     * @param _sourceActivity
     * @param _destinationActivity
     * @param appCompatActivity
     */
    public void resetUserPassword(String _email, final Context _sourceActivity, final Class _destinationActivity, final AppCompatActivity appCompatActivity){

        firebaseAuth.sendPasswordResetEmail(_email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utility.showInformationDialog(PASSWORD_RESET_TITLE, "A password reset link IS SENT your e-mail successfully", appCompatActivity);
                //openActivity.openAnActivityScreen(_sourceActivity, _destinationActivity);
                Intent intent = new Intent(_sourceActivity, _destinationActivity);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog(PASSWORD_RESET_TITLE, "A password reset link IS NOT SENT. " + e.getMessage(), appCompatActivity);
                return;
            }
        });
    }

    /***
     * Gets the current user from the system
     * @return true if the record is found else false
     */
    public boolean isUserTheCurrentFirebaseUser(){
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        if (firebaseAuth.getCurrentUser() != null){
            return false;
        } else {
            return true;
        }
    }

    // create a record
   /* public void createFirebaseRealtimeRecord(final AppCompatActivity appCompatActivity, final DatabaseReference modelDatabaseReference, final ElderlyModel elderlyModel){

        //check if the object passed is an elderly model type
        if (elderlyModel == null){
            Utility.showInformationDialog(NULL_OBJECT_DETECTED, NULL_FIELD_MESSAGE, appCompatActivity);
//            Utility.showInfoDialog(appCompatActivity, "ERROR: Field validation failed. Elderly is null");
            return;
        } else {

            // push data to Firebase table or child called Elders
            modelDatabaseReference.child("elders").push().setValue(elderlyModel).addOnCompleteListener( new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task){
                        if (task.isSuccessful()){
                            OpenActivity.openAnActivityScreen(appCompatActivity, ElderlyLoginActivity.class);
                            Utility.showInformationDialog("Record Created", "Elderly record created successfully", appCompatActivity);
                        } else {
                            Log.e( "Create Elder", task.getException().getMessage());
                            Utility.showInformationDialog("Create Record Error", task.getException().getMessage().toString(), appCompatActivity);
                        }
                    }
                }
            );
        }
    }*/

   /* public void selectRecord(final AppCompatActivity appCompatActivity, DatabaseReference modelDatabaseReference){}*/
}
