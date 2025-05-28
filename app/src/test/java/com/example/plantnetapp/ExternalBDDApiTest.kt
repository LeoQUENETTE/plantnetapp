package com.example.plantnetapp

import com.example.plantnetapp.back.api.ExternalBDDApi
import com.example.plantnetapp.back.api.PlantNetAPI
import com.example.plantnetapp.back.api.ReturnType
import com.example.plantnetapp.back.entity.PlantCollection
import com.example.plantnetapp.back.entity.User
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Test

class ExternalBDDApiTest {
    private val api : ExternalBDDApi = ExternalBDDApi.createInstance()
    private val testUser : User = User("TestUser","2ee9edd7b02f41082bc33f0276bdedf1", "firstname","lastname","email", "phone")
    private val testCollection : PlantCollection = PlantCollection("testCollection",arrayListOf())
    @Test
    fun apiConnectionTest() {
        try{
            val returnType : ReturnType = api.checkStatus()
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun getUser(){
        try{
            val returnType : ReturnType = api.login("Polpate","test")
            assertEquals(200,returnType.status)
            assertEquals("TestUser", returnType.values.getAsJsonObject("User").get("username").asString)
            assertEquals("email", returnType.values.getAsJsonObject("User").get("email").asString)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun addUser(){
        try{
            val returnType : ReturnType = api.addUser(testUser)
            assertEquals(201,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun deleteUser(){
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val returnType : ReturnType = api.deleteUser(userID)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }

    @Test
    fun getPlantCollection(){
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val returnType : ReturnType = api.getPlantCollection(userID,testCollection.name)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun getAllPlantCollections(){

    }

    @Test
    fun addPlantCollection(){
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val returnType : ReturnType = api.getPlantCollection(userID,testCollection.name)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }

    @Test
    fun deletePlantCollection(){

    }
    @Test
    fun deletePlantCollections(){

    }
}