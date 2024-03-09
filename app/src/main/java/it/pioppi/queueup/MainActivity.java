package it.pioppi.queueup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.pioppi.queueup.dao.NumberDao;
import it.pioppi.queueup.entity.NumberEntity;
import it.pioppi.queueup.utillity.Constants;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    public static final String ZERO = "0";
    public static final String TWO_ZEROS = "00";
    public static final String THREE_ZEROS = "000";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final String EIGHT = "8";
    public static final String NINE = "9";
    public static final Integer MAX_NUMBER = 999;

    private DatabaseReference database;

    private Button numberOne;
    private Button numberTwo;
    private Button numberThree;
    private Button numberFour;
    private Button numberFive;
    private Button numberSix;
    private Button numberSeven;
    private Button numberEight;
    private Button numberNine;
    private Button numberZero;
    private Button canc;
    private Button send;
    private Button minus;
    private Button plus;
    private TextView currentNumber;
    private TextView newNumber;

    private String currentNumberValue;
    private String newNumberValue;
    private NumberDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        database = FirebaseDatabase.getInstance(Constants.DATABASE_URL).getReference();

        //Buttons
        numberOne = findViewById(R.id.one);
        numberTwo = findViewById(R.id.two);
        numberThree = findViewById(R.id.three);
        numberFour = findViewById(R.id.four);
        numberFive = findViewById(R.id.five);
        numberSix = findViewById(R.id.six);
        numberSeven = findViewById(R.id.seven);
        numberEight = findViewById(R.id.eight);
        numberNine = findViewById(R.id.nine);
        numberZero = findViewById(R.id.zero);
        canc = findViewById(R.id.canc);
        send = findViewById(R.id.send);
        minus = findViewById(R.id.minus);
        plus = findViewById(R.id.plus);

        //TextViews
        currentNumber = findViewById(R.id.currentNumber);
        newNumber = findViewById(R.id.newNumber);

        //toolbar
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.my_statusbar_color));
        }

        currentNumberValue = "";
        newNumberValue = "";

        //default value
        newNumber.setText(newNumberValue);
        dao = new NumberDao();

    }

    @Override
    protected void onResume() {
        super.onResume();

        database.child("number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                value = formatNumberWithLeadingZeros(value);
                currentNumber.setText(value);
                currentNumberValue = value;
                logger.info("Data has been changed, current stored number retrieved from database: {}", value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                logger.error("Error while retrieving number from database: {}, {}", error.getMessage(), error.getDetails());
            }
        });

        handleNumber(numberOne, ONE);
        handleNumber(numberTwo, TWO);
        handleNumber(numberThree, THREE);
        handleNumber(numberFour, FOUR);
        handleNumber(numberFive, FIVE);
        handleNumber(numberSix, SIX);
        handleNumber(numberSeven, SEVEN);
        handleNumber(numberEight, EIGHT);
        handleNumber(numberNine, NINE);

        numberZero.setOnClickListener(v -> {
            if (!newNumberValue.equals(ZERO)) {
                newNumberValue += ZERO;
                newNumber.setText(newNumberValue);
            }
        });

        canc.setOnClickListener(v -> {
            newNumberValue = ZERO;
            newNumber.setText(newNumberValue);
        });

        plus.setOnClickListener(v -> {
            int currentValue = Integer.parseInt(currentNumberValue) + 1;
            currentNumberValue = String.valueOf(currentValue);
            currentNumberValue = formatNumberWithLeadingZeros(currentNumberValue);
            currentNumber.setText(currentNumberValue);

            dao.writeValue(database, currentNumberValue);
        });

        minus.setOnClickListener(v -> {
            int currentValue = Integer.parseInt(currentNumberValue);
            if (currentValue >= 1) {
                currentValue--;
                currentNumberValue = String.valueOf(currentValue);
                currentNumberValue = formatNumberWithLeadingZeros(currentNumberValue);
                currentNumber.setText(currentNumberValue);
                dao.writeValue(database, currentNumberValue);
            }
        });

        send.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.confirm_choice)
                    .setPositiveButton(R.string.yes, (dialog, id) -> {

                        if (!newNumberValue.equals(currentNumberValue)) {
                            newNumberValue = formatNumberWithLeadingZeros(newNumberValue);
                            dao.writeValue(this.database, newNumberValue);
                            currentNumber.setText(newNumberValue);

                        }
                        newNumber.setText(ZERO);
                        newNumberValue = ZERO;
                    })
                    .setNegativeButton(R.string.no, (dialog, id) -> {
                    }).show();
        });
    }

    private void handleNumber(Button button, String value) {
        button.setOnClickListener(v -> {
            if (newNumberValue.equals(ZERO)) {
                newNumberValue = "";
            }
            newNumberValue += value;
            newNumber.setText(newNumberValue);
        });
    }

    private String formatNumberWithLeadingZeros(String input) {

        int number = Integer.parseInt(input);
        if (number < 0 || number > MAX_NUMBER) {
            return THREE_ZEROS;
        }

        if (number < 10) {
            return TWO_ZEROS + number;
        } else if (number < 100) {
            return ZERO + number;
        } else {
            return String.valueOf(number);
        }
    }
}