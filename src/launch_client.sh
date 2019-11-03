#!/bin/bash

java DicoServer "../dict/dictalphabet.txt" &
sleep 1
scrabblos-server -port $2 -nb-turns 1 &

java -cp ".:../jar/*" ScoreBlockchainServer $1 $2 &
sleep 1
java -cp ".:../jar/*" Auteur $1 $2 &
java -cp ".:../jar/*" Politicien $1 $2 &

: '
for i in {1..10}
do
  java -cp ".:../jar/*" Auteur $1 $2 &
done

for i in {1..5}
do
  java -cp ".:../jar/*" Politicien $1 $2 &
done
'

