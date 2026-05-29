package com.example.autodrive

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

        // Le presenter vérifie si une session existe déjà
        presenter.verifierSessionExistante()

        setContent {
            AutoDriveTheme {
                var nom    by remember { mutableStateOf("") }
                var prenom by remember { mutableStateOf("") }
                var email  by remember { mutableStateOf("") }

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
                        onValueChange = { nom = it; erreurState = "" },
                        label = { Text("Nom") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = prenom,
                        onValueChange = { prenom = it; erreurState = "" },
                        label = { Text("Prénom") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; erreurState = "" },
                        label = { Text("Email") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        // ── Validation visuelle du format email ──────────
                        isError = email.isNotBlank() && !email.contains("@")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Message d'erreur ─────────────────────────────────
                    if (erreurState.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            )
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
                            // ── Validation format email avant d'appeler le presenter ──
                            if (email.isNotBlank() && !email.contains("@")) {
                                erreurState = "Format d'email invalide."
                            } else {
                                presenter.connecter(nom, prenom, email)
                            }
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

    // ── LoginContract.View ───────────────────────────────────────
    override fun afficherErreur(message: String) {
        erreurState = message
    }

    override fun ouvrirMenuPrincipal() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}