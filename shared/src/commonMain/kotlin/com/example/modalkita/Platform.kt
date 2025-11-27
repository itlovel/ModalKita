package com.example.modalkita

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform