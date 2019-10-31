#!/bin/bash

java DicoServer "../dict/dictalphabet.txt" &
sleep 1
scrabblos-server -port $2 &
java -cp ".:../jar/*" Politicien $1 $2 &
java -cp ".:../jar/*" Auteur $1 $2 &


