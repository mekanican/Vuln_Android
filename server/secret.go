package main

import (
	"math/rand"
	"net/http"

	"github.com/gin-gonic/gin"
)

func secretExchange(c *gin.Context) {
	// clientSecret := c.PostForm("secret")

	serverSecret := rand.Int63()

	c.String(http.StatusOK, "secret=%d", serverSecret)
}
