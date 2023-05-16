package com.example.traindriver.ui.screen


const val ROUTE = "route"
const val LOCO_ID = "loco_id"
const val TRAIN_ID = "train_id"
const val PASSENGER_ID = "passenger_id"
const val NOTES_ID = "notes_id"

sealed class Screen(val route: String) {
    object SignIn : Screen(route = "sign_in_screen")
    object Home : Screen(route = "home_screen")
    object PasswordConfirmation : Screen(route = "password_confirmation_screen")
    object ViewingRoute : Screen(route = "viewing_route_screen/{$ROUTE}") {
        fun passId(id: String): String {
            return this.route.replace(oldValue = "{$ROUTE}", newValue = id)
        }
    }

    object Profile : Screen(route = "profile_screen")
    object SearchRoute : Screen(route = "search_route_screen")
    object Setting : Screen(route = "setting_screen")
    object Adding : Screen(route = "adding_screen/{$ROUTE}") {
        fun setId(id: String): String {
            return this.route.replace(oldValue = "{$ROUTE}", newValue = id)
        }
    }

    object AddingLoco : Screen(route = "adding_loco_screen/{$LOCO_ID}") {
        fun setId(id: String): String {
            return this.route.replace(oldValue = "{$LOCO_ID}", newValue = id)
        }
    }

    object AddingTrain : Screen(route = "adding_train_screen/{$TRAIN_ID}") {
        fun setId(id: String): String {
            return this.route.replace(oldValue = "{$TRAIN_ID}", newValue = id)
        }
    }

    object AddingPassenger : Screen(route = "adding_passenger_screen/{$PASSENGER_ID}") {
        fun setId(id: String): String {
            return this.route.replace(oldValue = "{$PASSENGER_ID}", newValue = id)
        }
    }

    object AddingNotes : Screen(route = "adding_notes_screen/{$NOTES_ID}") {
        fun setId(id: String): String {
            return this.route.replace(oldValue = "{$NOTES_ID}", newValue = id)
        }
    }
}