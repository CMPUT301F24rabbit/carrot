import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EntrantProfileView extends AppCompatActivity {

    private EditText nameInput, emailInput, phoneInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_form);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String phone = phoneInput.getText().toString().trim();

                // Validate required fields
                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(EntrantFormActivity.this, "Name and Email are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                //  phone number check
                if (!phone.isEmpty() && !isValidPhoneNumber(phone)) {
                    Toast.makeText(EntrantFormActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Process or save the entrant's information
                saveEntrantData(name, email, phone);
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10}");
    }

    private void saveEntrantData(String name, String email, String phone) {
        // Save the data ******
        Toast.makeText(this, "Information saved: " + name + ", " + email, Toast.LENGTH_LONG).show();
    }
}