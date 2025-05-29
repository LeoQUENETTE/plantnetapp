package com.example.plantnetapp

import com.example.plantnetapp.back.api.PlantNetAPI
import com.example.plantnetapp.back.api.ReturnType
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Test
import java.io.File

class PlantNetAPITest {
    private val api : PlantNetAPI = PlantNetAPI.createInstance()
    @Test
    fun apiConnectionTest() {
        try{
            val returnType : ReturnType = api.checkStatus()
            assertEquals(200,returnType.status)
            assertEquals("\"ok\"",returnType.values.get("status").toString())
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetLanguagesTest(){
        try {
            val returnType : ReturnType = api.languages()
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetProjectsAllParametersTest(){
        try {
            val returnType : ReturnType = api.projects("fr", 20F, 20F,"kt")
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetProjectsLanguageOnlyTest(){
        try {
            val returnType : ReturnType = api.projects("fr",null,null,null)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetProjectsLatOnlyTest(){
        try {
            val returnType : ReturnType = api.projects(null,20F,null,null)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetProjectsLonOnlyTest(){
        try {
            val returnType : ReturnType = api.projects(null,null,20F,null)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetProjectsTypeOnlyTest(){
        try {
            val returnType : ReturnType = api.projects(null,null,null,"kt")
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetProjectsNoParameterTest(){
        try {
            val returnType : ReturnType = api.projects(null,null,null,null)
            assertEquals(200,returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetSpeciesAllParameter(){
        try{

        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetSubscriptionTest(){
        try{
            val returnType : ReturnType = api.subscription()
            assertEquals(403, returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetDailyQuotaNoParameterTest(){
        try{
            val returnType : ReturnType = api.dailyQuota(null)
            assertEquals(200, returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetDailyQuotaTest(){
        try{
            val returnType : ReturnType = api.dailyQuota("2025-04-14")
            assertEquals(200, returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetHistoryQuotaNoParameterTest(){
        try{
            val returnType : ReturnType = api.historyQuota(0)
            assertEquals(403, returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiGetHistoryQuotaTest(){
        try{
            val returnType : ReturnType = api.historyQuota(2020)
            assertEquals(403, returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
    @Test
    fun apiIdentifyPostNoProjectTest(){
        try {
            val currentDirectory = System.getProperty("user.dir") + "/src/test/java/com/example/plantnetapp/"
            val imageFile = File(currentDirectory + "testImages/rose-rouge.jpeg");
            val returnType : ReturnType = api.identify(imageFile,null, null, null, 0, null, null, null)
            println(returnType)
            assertEquals(200, returnType.status)
        }catch (e : Exception){
            Assert.fail(e.message)
        }
    }
}