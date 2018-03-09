package com.scythe.developertools

/**
 * Created by Camilo on 3/9/2018.
 */
interface LocalBinder<out T> {
    fun getService() : T

}