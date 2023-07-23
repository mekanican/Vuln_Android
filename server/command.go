package main

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func getCommand(c *gin.Context) {
	c.String(http.StatusOK, "echo original && echo injected")
}
