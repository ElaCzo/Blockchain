#!/bin/bash
for i in {1..50}
do
  java -cp ".:../jar/*" Auteur $1 $2 &
done
