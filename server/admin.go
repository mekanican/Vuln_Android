package main

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func adminPage(c *gin.Context) {
	c.String(http.StatusOK, "For admin only")
}
