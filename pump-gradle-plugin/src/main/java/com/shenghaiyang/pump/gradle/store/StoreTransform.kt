package com.shenghaiyang.pump.gradle.store


interface StoreTransform {

    fun transform(mapping: Map<String, String>)

}