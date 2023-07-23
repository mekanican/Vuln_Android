package main

import (
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

var global_db *gorm.DB

func main() {
	// proxy := setUpProxy()
	// defer proxy.Close()

	global_db = initializeDatabase()

	// Set up 2 api
	router := gin.Default()
	router.POST("/login", postPassword)
	router.GET("/content/:id", getContent)

	// API for ddos (challenge client-server)
	router.POST("/checkPrime", checkPrime)
	router.GET("/getRandom", getRandom)
	router.POST("/checkChallenge", checkChallenge)

	//  API for secret
	router.POST("/secret", secretExchange)

	//  admin unauthorized
	router.GET("/admin", adminPage)

	//  command injection
	router.GET("/command", getCommand)

	// start server at port 7999
	router.Run("localhost:7999")
}
