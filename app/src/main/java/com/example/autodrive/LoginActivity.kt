package com.example.autodrive

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autodrive.model.repository.UtilisateurRepository
import com.example.autodrive.model.session.UserSession
import com.example.autodrive.presenter.LoginPresenter
import com.example.autodrive.presenter.contract.LoginContract
import com.example.autodrive.ui.theme.AutoDriveTheme

class LoginActivity : ComponentActivity(), LoginContract.View {

    private lateinit var presenter: LoginPresenter
    private var erreurState by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = LoginPresenter(
            this,
            UtilisateurRepository(applicationContext),
            UserSession(applicationContext)
        )
        presenter.verifierSessionExistante()
        if (isFinishing) return

        setContent {
            AutoDriveTheme {
                var nom by rememberSaveable { mutableStateOf("") }
                var prenom by rememberSaveable { mutableStateOf("") }
                var email by rememberSaveable { mutableStateOf("") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF7F7F7))
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "AutoDrive",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        "Connectez-vous pour continuer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF888888)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    OutlinedTextField(
                        value = nom,
                        onValueChange = { nom = it },
                        label = { Text("Nom") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = prenom,
                        onValueChange = { prenom = it },
                        label = { Text("Prenom") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (erreurState.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Text(
                                erreurState,
                                modifier = Modifier.padding(14.dp),
                                color = Color(0xFFC62828),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Button(
                        onClick = {
                            erreurState = ""
                            presenter.connecter(nom, prenom, email)
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Entrer", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }

    override fun afficherErreur(message: String) {
        erreurState = message
    }

    override fun ouvrirMenuPrincipal() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
