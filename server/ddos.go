package main

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

// Vulnerable endpoint which use tons of resource
func checkPrime(c *gin.Context) {
	num, err := strconv.ParseInt(c.PostForm("num"), 10, 64)
	if err != nil {
		c.String(http.StatusNotAcceptable, "Not Accepted")
		return
	}
	for i := int64(2); i < num; i++ {
		if num%i == 0 {
			c.String(http.StatusOK, "Not Prime")
			return
		}
	}
	c.String(http.StatusOK, "Prime")
}

func getRandom(c *gin.Context) {
	//  Returned value is absolutely hard to factor
	n := int64(90348884270113069) //  282292301 * 320054369
	c.String(http.StatusOK, "%d", n)
}

func checkChallenge(c *gin.Context) {
	chall, err := strconv.ParseInt(c.PostForm("chall"), 10, 64)
	if err != nil {
		c.String(http.StatusNotAcceptable, "Not Accepted")
		return
	}

	factor, err := strconv.ParseInt(c.PostForm("factor"), 10, 64)
	if err != nil {
		c.String(http.StatusNotAcceptable, "Not Accepted")
		return
	}

	if chall%factor == 0 {
		c.String(http.StatusOK, "Correct")
	} else {
		c.String(http.StatusOK, "Incorrect")
	}
}
