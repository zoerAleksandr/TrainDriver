package com.example.traindriver.ui.screen


const val ROUTE = "route"
const val LOCO_ID = "loco_id"

sealed class Screen(val route: String) {
    object SignIn: Screen(route = "sign_in_screen")
    object Home: Screen(route = "home_screen")
    object PasswordConfirmation: Screen(route = "password_confirmation_screen")
    object ViewingRoute: Screen(route = "viewing_route_screen/{$ROUTE}"){
        fun passId(id: String): String{
            return this.route.replace(oldValue = "{$ROUTE}", newValue = id)
        }
    }
    object Profile: Screen(route = "profile_screen")
    object SearchRoute: Screen(route = "search_route_screen")
    object Setting: Screen(route = "setting_screen")
    object Adding: Screen(route = "adding_screen/{$ROUTE}"){
        fun setId(id: String): String{
            return this.route.replace(oldValue = "{$ROUTE}", newValue = id)
        }
    }
    object AddingLoco: Screen(route = "adding_loco_screen/{$LOCO_ID}"){
        fun setId(id: String): String{
            return this.route.replace(oldValue = "{$LOCO_ID}", newValue = id)
        }
    }
}