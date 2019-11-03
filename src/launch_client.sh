#!/bin/bash

java DicoServer "../dict/dict_100000_1_10.txt" &
sleep 1
scrabblos-server -port $2 -nb-turns 5 &
sleep 1
java -cp ".:../jar/*" ScoreBlockchainServer $1 $2 &
sleep 1
for i in {1..10}
do
  java -cp ".:../jar/*" Auteur $1 $2 &
done

for i in {1..5}
do
  java -cp ".:../jar/*" Politicien $1 $2 &
done


