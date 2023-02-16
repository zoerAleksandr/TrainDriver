package com.example.traindriver.ui.screen

import com.example.traindriver.ui.util.Constants.ROUTE

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
}