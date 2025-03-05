import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState = _signUpState.asStateFlow()

    fun signUp(
        nombre: String,
        apellidos: String,
        numeroContacto: String,
        correoElectronico: String,
        password: String
    ) {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                // Primero, intentar crear el usuario en Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(correoElectronico, password).await()

                // Si la creación es exitosa, guardar información adicional en Firestore
                val userId = authResult.user?.uid
                if (userId != null) {
                    val userData = hashMapOf(
                        "nombre" to nombre,
                        "apellidos" to apellidos,
                        "numeroContacto" to numeroContacto,
                        "correoElectronico" to correoElectronico
                    )

                    // Guardar datos adicionales en Firestore
                    firestore.collection("users").document(userId).set(userData).await()

                    _signUpState.value = SignUpState.Success
                } else {
                    _signUpState.value = SignUpState.Error("Error al crear usuario")
                }
            } catch (e: Exception) {
                // Manejar errores específicos de Firebase
                val errorMessage = when (e) {
                    is FirebaseAuthUserCollisionException ->
                        "El correo electrónico ya está registrado"
                    is FirebaseAuthWeakPasswordException ->
                        "La contraseña es demasiado débil"
                    is FirebaseAuthInvalidCredentialsException ->
                        "Correo electrónico inválido"
                    else -> e.localizedMessage ?: "Error de registro"
                }
                _signUpState.value = SignUpState.Error(errorMessage)
            }
        }
    }
}

// Estado del proceso de registro
sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}