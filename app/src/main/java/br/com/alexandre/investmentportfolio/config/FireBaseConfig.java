package br.com.alexandre.investmentportfolio.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Base64;
import java.util.Objects;

import static br.com.alexandre.investmentportfolio.enums.TableName.COMPANIES;
import static br.com.alexandre.investmentportfolio.enums.TableName.INVESTMENTS;

public class FireBaseConfig {

    private static FirebaseAuth auth;

    public static FirebaseAuth getFirebaseAuth(){
        if (Objects.isNull(auth)){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static DatabaseReference getReferenceInvestment(){
        return FirebaseDatabase.getInstance().getReference()
                .child(INVESTMENTS.name()).child(getEncodedEmail());
    }

    public static DatabaseReference getReferenceCompany(){
        return FirebaseDatabase.getInstance().getReference()
                .child(COMPANIES.name()).child(getEncodedEmail());
    }

    public static String getEncodedEmail() {
        String emailUser = getFirebaseAuth().getCurrentUser().getEmail();
        String encodedEmail = Base64.getEncoder().encodeToString(emailUser.getBytes());
        return encodedEmail;
    }

}
