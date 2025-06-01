package com.example.plantnetapp

import com.example.plantnetapp.back.api.ExternalBDDApi
import com.example.plantnetapp.back.api.PlantNetAPI
import com.example.plantnetapp.back.api.ReturnType
import com.example.plantnetapp.back.entity.Plant
import com.example.plantnetapp.back.entity.PlantCollection
import com.example.plantnetapp.back.entity.User
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException

class ExternalBDDApiTest {
    private val     api : ExternalBDDApi = ExternalBDDApi.createInstance()
    private val testUser : User = User(null,"TestUser","2ee9edd7b02f41082bc33f0276bdedf1", "firstname","lastname","email", "phone")
    private val testCollection : PlantCollection = PlantCollection("testCollection",arrayListOf())
    private val testPlant : Plant = Plant("","rose", 0.5F, 0.75F, 0.6F)
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
            val returnType : ReturnType = api.login(testUser.login,testUser.mdp)
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
        }finally {
            addUser()
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
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val returnType : ReturnType = api.getAllPlantCollections(userID)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }

    @Test
    fun addPlantCollection(){
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val returnType : ReturnType = api.addPlantCollection(userID,testCollection.name)
            assertEquals(201,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }

    @Test
    fun deletePlantCollection(){
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val returnType : ReturnType = api.deletePlantCollection(userID,testCollection.name)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }finally {
            try {
                addPlantCollection()
            }catch (e : Exception){
                Assert.fail(e.message)
            }
        }
    }
    @Test
    fun addPlantWithoutCollection(){
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val currentDirectory = System.getProperty("user.dir") + "/src/test/java/com/example/plantnetapp/"
            val returnType : ReturnType = api.addPlantWithoutCollection(userID, testPlant, File(currentDirectory+"testImages/rose-rouge.jpeg"))
            assertEquals(201,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun addPlant(){
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val getCollectionResponse = api.getPlantCollection(userID,testCollection.name)
            if (getCollectionResponse.status != 200){
                Assert.fail("Error getting collection : "+getUserResponse.values)
            }
            val currentDirectory = System.getProperty("user.dir") + "/src/test/java/com/example/plantnetapp/"
            val collectionID = getCollectionResponse.values.getAsJsonObject("PlantCollection").get("id").asString
            val returnType : ReturnType = api.addPlant(collectionID, testPlant, File(currentDirectory+"testImages/rose-rouge.jpeg"))
            assertEquals(201,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun getPlant(){
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val getCollectionResponse = api.getPlantCollection(userID,testCollection.name)
            if (getCollectionResponse.status != 200){
                Assert.fail("Error getting collection : "+getUserResponse.values)
            }
            val collectionID = getCollectionResponse.values.getAsJsonObject("PlantCollection").get("id").asString
            val returnType : ReturnType = api.getPlant(collectionID, testPlant.name)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun getAllPlants(){
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val getCollectionResponse = api.getPlantCollection(userID,testCollection.name)
            if (getCollectionResponse.status != 200){
                Assert.fail("Error getting collection : "+getUserResponse.values)
            }
            val collectionID = getCollectionResponse.values.getAsJsonObject("PlantCollection").get("id").asString
            val returnType : ReturnType = api.getAllPlants(collectionID)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun deletePlant(){
        try{
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val getCollectionResponse = api.getPlantCollection(userID,testCollection.name)
            if (getCollectionResponse.status != 200){
                Assert.fail("Error getting collection : "+getUserResponse.values)
            }
            val collectionID = getCollectionResponse.values.getAsJsonObject("PlantCollection").get("id").asString
            try {
                val returnType : ReturnType = api.deletePlant(collectionID, testPlant.name)
                assertEquals(200,returnType.status)
            }catch (e : IOException){
                Assert.fail(e.message)
            }finally {
                val currentDirectory = System.getProperty("user.dir") + "/src/test/java/com/example/plantnetapp/"
                api.addPlant(collectionID, testPlant, File(currentDirectory+"testImages/rose-rouge.jpeg"))
            }
        }catch (e : IOException){
            Assert.fail(e.message)
        }

    }

    @Test
    fun userFromJSON(){
        try {
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val user = User.userFromJSON(getUserResponse.values)
            assertEquals(user.login, testUser.login)
            assertEquals(user.mail, testUser.mail)
            assertEquals(user.firstName, testUser.firstName)
            assertEquals(user.lastName, testUser.lastName)
            assertEquals(user.phone, testUser.phone)
        }
        catch (e : IOException){
            Assert.fail(e.message)
        }
    }
    @Test
    fun collectionFromJSON(){
        try {
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val getCollectionResponse = api.getPlantCollection(userID,testCollection.name)
            val collection = PlantCollection.plantCollectionFromJSON(getCollectionResponse.values)
            assertEquals(collection.name , testCollection.name)
        }
        catch (e : IOException){
            Assert.fail(e.message)
        }
    }
    @Test
    fun plantFromJSON(){
        try {
            val getUserResponse : ReturnType = api.login(testUser.login,testUser.mdp)
            if (getUserResponse.status != 200){
                Assert.fail("Error getting user : "+getUserResponse.values)
            }
            val userID = getUserResponse.values.getAsJsonObject("User").get("id").asString
            val getCollectionResponse = api.getPlantCollection(userID,testCollection.name)
            if (getCollectionResponse.status != 200){
                Assert.fail("Error getting collection : "+getCollectionResponse.values.asString)
            }
            val collectionID = getCollectionResponse.values.getAsJsonObject("PlantCollection").get("id").asString
            val getPlantResponse = api.getPlant(collectionID, testPlant.name)
            if (getPlantResponse.status != 200){
                Assert.fail("Error getting plant : "+getPlantResponse.values.asString)
            }
            val plant = Plant.plantFromJSON(getPlantResponse.values, true)
            assertEquals(plant.name , testPlant.name)
        }
        catch (e : IOException){
            Assert.fail(e.message)
        }
    }
}