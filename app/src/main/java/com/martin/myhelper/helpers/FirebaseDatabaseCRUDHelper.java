package com.martin.myhelper.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.martin.myhelper.R;
import com.martin.myhelper.model.ElderlyModel;
import com.martin.myhelper.model.GenericModel;
import com.martin.myhelper.model.VolunteerModel;
import com.martin.myhelper.views.ElderlyLoginActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class FirebaseDatabaseCRUDHelper extends Activity {

    private final String NULL_OBJECT_DETECTED = "Null Object Detected";
    private final String NULL_FIELD_MESSAGE = "Field validation failed. The passed model is null";
    private final String CREATE_RECORD_SUCCESS_TITLE = "RECORD CREATED";
    private final String CREATE_RECORD_SUCCESS_MSG = "Record created successfully!";
    private final String CREATE_RECORD_FAILED_TITLE = "RECORD CREATION FAILED";
    private final String CREATE_RECORD_FAILED_MSG = "Record NOT created successfully!";
    private final String PASSWORD_RESET_TITLE = "PASSWORD RESET";
    private final int MODEL_ARRAY_LENGTH = 4;
    private final int REQUEST_CODE = 1000;

    private boolean userIsElder = false;
    private boolean imageUploadIsSuccess = false;
    private Boolean isSuccess = false;

    private EditText firstName, lastName, email, mobileNumber,  userType;
    private ImageView profileImage;
    public ImageView profileImageView;

    public void createUserRecord(final AppCompatActivity appCompatActivity, final FirebaseAuth firebaseAuth, Class model,
                                 String _email, String _password, final String tableAsCollection, final String[] modelArray){

        // if user account already exists
        if (getSystemCurrentUser()){
            Utility.showInformationDialog("RECORD EXISTS", "The user account already exist and cannot be created.", appCompatActivity);
            OpenActivity.openAnActivityScreen(appCompatActivity, model);
            return;
        } else {

            // if model passed is not empty or null
            if (model == null) {
                Utility.showInformationDialog(NULL_OBJECT_DETECTED, NULL_FIELD_MESSAGE, appCompatActivity);
                return;
            } else {

                // create the user account via firebase auth if not null
                firebaseAuth.createUserWithEmailAndPassword(_email, _password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //create the user profile
                            createProfileRecord(tableAsCollection, appCompatActivity, modelArray);

                        } else {
                            Utility.showInformationDialog("ERROR!", task.getException().getMessage(), appCompatActivity);
                            return;
                        }
                    }
                });
            }
        }
    }

    private void createProfileRecord(String collectionName, final AppCompatActivity appCompatActivity, String[] _modelArray){
        final FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
        FirebaseFirestore firebaseFirestore = Utility.getFirebaseFireStore();

        String currentUserID = firebaseAuth.getCurrentUser().getUid();

        // create an instance of the DocumentReference class of FirebaseStore
        DocumentReference documentReference = firebaseFirestore.collection(collectionName).document(currentUserID);

        // create a hash map of the object to be stored
        Map<String, Object> modelMap = new HashMap<>();

        if (MODEL_ARRAY_LENGTH == _modelArray.length) {
            modelMap.put("firstName", _modelArray[0]);
            modelMap.put("lastName", _modelArray[1]);
            modelMap.put("email", _modelArray[2]);
            modelMap.put("mobileNumber", _modelArray[3]);
            modelMap.put("userType", _modelArray[4]);
        } else {
            modelMap.put("firstName", _modelArray[0]);
            modelMap.put("lastName", _modelArray[1]);
            modelMap.put("email", _modelArray[2]);
            modelMap.put("mobileNumber", _modelArray[3]);
            modelMap.put("userType", _modelArray[4]);
            modelMap.put("profileImage", _modelArray[5]);
        }

        documentReference.set(modelMap).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {

                // send an e-mail for verification
                sendVerificationEmail(appCompatActivity);

                // show a message for successful record recreation
                Utility.showInformationDialog(CREATE_RECORD_SUCCESS_TITLE, CREATE_RECORD_SUCCESS_MSG, appCompatActivity);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
            }
        });

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
        Intent extraIntentData = getIntent();
        if(getUserType("elders")){

            elderlyModel.setFirstName(extraIntentData.getStringExtra("firstName"));
            elderlyModel.setLastName(extraIntentData.getStringExtra("lastName"));
            elderlyModel.setEmail(extraIntentData.getStringExtra("email"));
            elderlyModel.setMobileNumber(extraIntentData.getStringExtra("mobileNumber"));
            elderlyModel.setMobileNumber(extraIntentData.getStringExtra("userType"));
        }else {
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
        profileImage = findViewById(R.id.profileImage);

        if(getUserType("elders")){
            firstName.setText(elderlyModel.getFirstName());
            lastName.setText(elderlyModel.getLastName());
            //email.setText(elderlyModel.getEmail());
            mobileNumber.setText(elderlyModel.getMobileNumber());
            userType.setText(elderlyModel.getUserType());
        } else {
            firstName.setText(volunteerModel.getFirstName());
            lastName.setText(volunteerModel.getLastName());
            //email.setText(volunteerModel.getEmail());
            mobileNumber.setText(volunteerModel.getMobileNumber());
            userType.setText(volunteerModel.getUserType());
            profileImage.setImageURI(Uri.parse(volunteerModel.getProfileImage().toString()));
        }

        final FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
        FirebaseFirestore firebaseFirestore = Utility.getFirebaseFireStore();

        String currentUserID = firebaseAuth.getCurrentUser().getUid();
        String tableAsCollectionName;
        if(getUserType("elders")){
            tableAsCollectionName = "elders";
        } else {
            tableAsCollectionName = "volunteers";
        }

        // create an instance of the DocumentReference class of FirebaseStore
        DocumentReference documentReference = firebaseFirestore.collection(tableAsCollectionName).document(currentUserID);

        // create a hash map of the object to be stored
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("firstName", firstName.getText().toString());
        modelMap.put("lastName", lastName.getText().toString());
        modelMap.put("email", email.getText().toString());
        modelMap.put("mobileNumber", mobileNumber.getText().toString());
        modelMap.put("userType", userType.getText().toString());
        modelMap.put("profileImage", profileImage.toString());

        documentReference.update(modelMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utility.showInformationDialog("RECORD UPDATE", "You have successfully updated your account information", appCompatActivity);

                // take user to his home page
                OpenActivity.openAnActivityScreen(sourceActivity,destinationActivity);
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
        final FirebaseUser firebaseUser = this.getFireBaseUser();
        final FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();

        firebaseUser.updateEmail(_email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utility.showInformationDialog("E-MAIL CHANGE", "You have successfully changed your e-mail. You have to re-login to the app", appCompatActivity);
                firebaseAuth.signOut();

                OpenActivity.openAnActivityScreen(sourceActivity, destinationActivity);
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
        final FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
        firebaseAuth.sendPasswordResetEmail(_email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // sign the user out
                firebaseAuth.signOut();

                // redirect user to login
                OpenActivity.openAnActivityScreen(appCompatActivity, destinationActivity);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog("RESET PASSWORD", "Error sending PASSWORD RESET link. " + e.getMessage(), appCompatActivity);
            }
        });
    }

    private void setProfileDataToIntent(Intent intent, String[] _modelArray){

        if(getUserType("elders")){
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

    public void uploadProfilePhoto(ImageView imageView){

        // open the image gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // start the activity of the media intent results
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                Uri imagePath = data.getData();

                this.pushImageToFirebaseStorage(imagePath);
            }
        }
    }

    private boolean pushImageToFirebaseStorage(final Uri _imagePath){

        // create a child reference to the
        StorageReference storageReference = Utility.getFirebaseStorage();
        storageReference.child(getUserID()).child( getUserID() + "profileImage");

        storageReference.putFile(_imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileImageView.setImageURI(_imagePath);
                imageUploadIsSuccess = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageUploadIsSuccess = false;
            }
        });
        return  imageUploadIsSuccess;
    }

    private void pushImageToFirebaseStorage(final Uri _imagePath, final AppCompatActivity appCompatActivity){

        // create a child reference to the
        StorageReference storageReference = Utility.getFirebaseStorage();
        storageReference.child(getUserID()).child( getUserID() + "profileImage");

        storageReference.putFile(_imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileImageView.setImageURI(_imagePath);

                Utility.showInformationDialog("IMAGE UPLOAD", "Image UPLOADED successfully. ", appCompatActivity );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog("IMAGE UPLOAD", "Image did NOT UPLOADED successfully. ", appCompatActivity );
                return;
            }
        });
    }

    private void pushImageToFirebaseStorage(Uri _imagePath, final AppCompatActivity appCompatActivity, final Context _sourceActivity, final Class _destinationActivity){

        // create a child reference to the
        StorageReference storageReference = Utility.getFirebaseStorage();
        storageReference.child(getUserID()).child( getUserID() + "profileImage");

        storageReference.putFile(_imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Utility.showInformationDialog("IMAGE UPLOAD", "Image UPLOADED successfully. ", appCompatActivity );
                    OpenActivity.openAnActivityScreen(_sourceActivity, _destinationActivity);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog("IMAGE UPLOAD", "Image did NOT UPLOADED successfully. ", appCompatActivity );
                return;
            }
        });
    }

    public void showUserProfileImage(){
        // create a child reference to the
        StorageReference storageReference = Utility.getFirebaseStorage();
        storageReference.child("users/" + getUserID() + "profileImage").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });
    }

    public String getUserID(){
        final FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
        String currentUserID = firebaseAuth.getCurrentUser().getUid();
        return currentUserID;
    }

    /***
     * Gets or fetches a user's record from the database
     * @param collectionName
     */
    public void getUserRecord(String collectionName){
        FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
        FirebaseFirestore firebaseFirestore = Utility.getFirebaseFireStore();

        String userID = getFireBaseUser().getUid();
        final GenericModel genericModel = new GenericModel();

        DocumentReference documentReference = firebaseFirestore.collection(collectionName).document(userID);
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

               } else {
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

    /***
     * Gets the current user and checks if the UserType is an Elder or a Volunteer.
     * @param modelTableAsCollection
     * @return true if the user is an elder else returns false
     */
    public boolean getUserType(String modelTableAsCollection){

        FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
        FirebaseFirestore firebaseFirestore = Utility.getFirebaseFireStore();

        // get the user id
        String userID = getFireBaseUser().getUid();

        DocumentReference documentReference = firebaseFirestore.collection(modelTableAsCollection).document(userID);
        documentReference.addSnapshotListener((Executor) this, new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String userType = value.getString("userType");

                // if type of user is elderly
                if( Integer.parseInt(userType) == GenericModel.USER_TYPE_ELDER ){
                    userIsElder = true;
                } else{
                    userIsElder = false;
                }
            }
        });

        return userIsElder;
    }

    /***
     * Sends an email to a User after account is created or when it is called
     * @param appCompatActivity
     */
    public void sendVerificationEmail(final AppCompatActivity appCompatActivity){
        FirebaseUser firebaseUser = getFireBaseUser();
        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utility.showInformationDialog("User Account Created!",
                        "Your account is created successfully. \nA verification e-mail is sent to your inbox to activate your account.",
                        appCompatActivity);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog("Error !", e.getMessage(), appCompatActivity);
                return;
            }
        });
    }

    /***
     * Checks if a User has verified his email after registration
     * @return
     */
    public Boolean checkIfEmailIsVerify(){
        FirebaseUser firebaseUser = getFireBaseUser();
        boolean isVerified = false;

        if(firebaseUser.isEmailVerified()){
            isVerified = true;
        } else {
            isVerified = false;
        }

        return isVerified;
    }

    /***
     *
     * @return
     */
    public FirebaseUser getFireBaseUser(){
        FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
        return  firebaseAuth.getCurrentUser();
    }

    /***
     *
     * @param sourceActivity
     * @param destinationActivity
     * @param appCompatActivity
     */
    public void logOutUser(Context sourceActivity, Class destinationActivity, AppCompatActivity appCompatActivity){
        FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
        firebaseAuth.signOut();

        OpenActivity.openAnActivityScreen(sourceActivity, destinationActivity);

        Utility.showInformationDialog("Sign Out", "You have successfully signed out from the application", appCompatActivity);
    }

    /***
     * Logs-in a user into the application after a successful authentication using FirebaseAuth. It opens
     * a new activity or screen if successful else remains on the same page
     * @param modelClass
     * @param context
     * @param appCompatActivity
     * @param _email
     * @param _password
     */
    public void loginFireStoreUser(final Class modelClass, final Context context, final AppCompatActivity appCompatActivity, final String _email, String _password){
        FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();

        firebaseAuth.signInWithEmailAndPassword(_email, _password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    // check if the user has verified his e-mail address or account
                    if (checkIfEmailIsVerify()){
                        OpenActivity.openAnActivityScreen(context, modelClass);
                    } else {
                        Utility.promptUserBeforeEmailResetLinkIsSent(appCompatActivity);
                        return;
                    }

                } else {
                    Utility.showInformationDialog("LOGIN ERROR", task.getException().getMessage(), appCompatActivity );
                    return;
                }
            }
        });
    }

    /***
     * Sends a link for a user to reset his/her password
     * @param _email
     * @param _sourceActivity
     * @param _destinationActivity
     * @param appCompatActivity
     */
    public void resetUserPassword(String _email, final Context _sourceActivity, final Class _destinationActivity, final AppCompatActivity appCompatActivity){
        FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
        firebaseAuth.sendPasswordResetEmail(_email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utility.showInformationDialog(PASSWORD_RESET_TITLE, "A password reset link IS SENT your e-mail successfully", appCompatActivity);
                OpenActivity.openAnActivityScreen(_sourceActivity, _destinationActivity);
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
    public boolean getSystemCurrentUser(){
        FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
        if (firebaseAuth.getCurrentUser() != null){
            return true;
        } else {
            return false;
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
