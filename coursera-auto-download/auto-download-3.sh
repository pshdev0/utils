#!/bin/bash

input="./data.txt"

while IFS= read -r studentid
do
  echo "student id = $studentid"

  read -r number
  echo "number = $number"

  for ((i = 0; i < $number; i++));
  do
    read -r line

    filename=${line//%20/ }
    filename=${filename##*/}

    curl -o "./$studentid/$filename" --create-dirs "$line"
  done

echo "";

done < "$input"

