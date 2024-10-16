package com.elssiany.klydo.worker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform