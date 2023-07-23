package main

import (
	"errors"
	"fmt"
	"net/http"
	"strconv"
	"strings"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func queryPasswordId(db *gorm.DB, password string) (uint, error) {
	user := &User{}
	// Sql-injection
	if err := db.Where(fmt.Sprintf("Password = %v", password)).First(user).Error; err != nil {
		return 0, err
	}
	return user.ID, nil
}

func queryIdContent(db *gorm.DB, id uint) (string, error) {
	user := &User{}

	if err := db.First(user, id).Error; err != nil {
		return "", err
	}

	return user.Content, nil
}

func postPassword(c *gin.Context) {
	password := c.PostForm("password")
	id, err := queryPasswordId(global_db, password)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.String(http.StatusNotFound, "No password match")
		} else {
			c.String(http.StatusInternalServerError, err.Error())
		}
	} else {
		c.String(http.StatusOK, "%d", id)
	}
}

func getContent(c *gin.Context) {
	id, err := strconv.Atoi(strings.Trim(c.Param("id"), "/"))

	if err != nil || id < 0 {
		c.String(http.StatusNotAcceptable, "Wrong id format")
		return
	}

	content, err := queryIdContent(global_db, uint(id))

	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			c.String(http.StatusNotFound, "No id match")
		} else {
			c.String(http.StatusInternalServerError, err.Error())
		}
	} else {
		c.String(http.StatusOK, "%s", content)
	}
}
