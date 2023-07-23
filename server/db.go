package main

import (
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

type User struct {
	gorm.Model
	Password string
	Content  string
}

func initializeDatabase() *gorm.DB {
	db, err := gorm.Open(sqlite.Open("/tmp/test.db"), &gorm.Config{})
	if err != nil {
		panic("Failed to load sqlite database")
	}
	db.AutoMigrate(&User{}) // Loading the model

	// Setting up some initial value to db
	db.Create(&User{
		Password: "12345@Aa",
		Content:  "Normal User!",
	})

	db.Create(&User{
		Password: "12345678",
		Content:  "Normal User!",
	})

	db.Create(&User{
		Password: "00000000",
		Content:  "Normal User!",
	})

	db.Create(&User{
		Password: "UltraS3cr3t",
		Content:  "Admin User!!!!",
	})

	return db
}
