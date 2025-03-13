package com.example.auth.data

import java.util.regex.Pattern

object SignUpValidator {
    // Patrones de validación
    private val namePattern = Pattern.compile("^[A-Za-zÁ-ÿ]{2,50}(\\s[A-Za-zÁ-ÿ]{2,50})*$")
    private val phonePattern = Pattern.compile("^\\+?[0-9\\s-]{9,15}$")
    private val emailPattern = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
    )
    private val passwordPattern = Pattern.compile(
        "^(?=.*[a-z])" +  // al menos 1 minúscula
                "(?=.*[A-Z])" +   // al menos 1 mayúscula
                "(?=.*\\d)" +     // al menos 1 número
                "\\S{8,}$\n" // mínimo 8 caracteres
    )

    // Función de validación con errores detallados
    fun validate(
        nombre: String,
        apellidos: String,
        numeroContacto: String,
        correoElectronico: String,
        password: String,
        confirmPassword: String
    ): Map<String, List<String>> {
        val errors = mutableMapOf<String, List<String>>()

        // Validación de nombre
        val nombreValidations = mutableListOf<String>()
        when {
            nombre.trim().isEmpty() -> nombreValidations.add("El nombre no puede estar vacío.")

            nombre.length < 2 -> nombreValidations.add("El nombre debe tener al menos 2 caracteres.")

            nombre.length > 50 -> nombreValidations.add("El nombre no puede exceder 50 caracteres.")

            !namePattern.matcher(nombre)
                .matches() -> nombreValidations.add("El nombre solo puede contener letras y espacios.")
        }
        if (nombreValidations.isNotEmpty()) errors["nombre"] = nombreValidations

        // Validación de apellidos
        val apellidosValidations = mutableListOf<String>()
        when {
            apellidos.trim()
                .isEmpty() -> apellidosValidations.add("Los apellidos no pueden estar vacíos.")

            apellidos.length < 2 -> apellidosValidations.add("Los apellidos deben tener al menos 2 caracteres.")

            apellidos.length > 50 -> apellidosValidations.add("Los apellidos no pueden exceder 50 caracteres.")

            !namePattern.matcher(apellidos)
                .matches() -> apellidosValidations.add("Los apellidos solo pueden contener letras y espacios.")
        }
        if (apellidosValidations.isNotEmpty()) errors["apellidos"] = apellidosValidations

        // Validación de número de contacto
        val contactoValidations = mutableListOf<String>()
        when {
            numeroContacto.trim()
                .isEmpty() -> contactoValidations.add("El número de contacto no puede estar vacío.")

            numeroContacto.length < 10 -> contactoValidations.add("El número de contacto es demasiado corto.")

            numeroContacto.length > 10 -> contactoValidations.add("El número de contacto es demasiado largo.")

            !phonePattern.matcher(numeroContacto).matches() -> contactoValidations.addAll(
                listOf(
                    "El número solo puede contener dígitos", "Se permiten '+', espacios y guiones"
                )
            )
        }
        if (contactoValidations.isNotEmpty()) errors["numeroContacto"] = contactoValidations

        // Validación de correo electrónico
        val correoValidations = mutableListOf<String>()
        when {
            correoElectronico.trim()
                .isEmpty() -> correoValidations.add("El correo electrónico no puede estar vacío.")

            correoElectronico.length > 100 -> correoValidations.add("El correo electrónico es demasiado largo.")

            !emailPattern.matcher(correoElectronico).matches() -> correoValidations.add(
                    "Formato de correo electrónico inválido."
            )
        }
        if (correoValidations.isNotEmpty()) errors["correoElectronico"] = correoValidations

        // Validación de contraseña
        val passwordValidations = mutableListOf<String>()
        if (!passwordPattern.matcher(password).matches()) {
            passwordValidations.add("La contraseña debe tener al menos 8 caracteres, una letra minúscula, una letra mayúscula y un número.")
        }
        if (passwordValidations.isNotEmpty()) {
            errors["password"] = passwordValidations
        }

        // Validación de confirmación de contraseña
        val confirmPasswordValidations = mutableListOf<String>()
        when {
            confirmPassword.trim()
                .isEmpty() -> confirmPasswordValidations.add("Debe confirmar la contraseña.")

            password != confirmPassword -> confirmPasswordValidations.add("Las contraseñas no coinciden.")
        }
        if (confirmPasswordValidations.isNotEmpty()) errors["confirmPassword"] =
            confirmPasswordValidations

        return errors
    }
}