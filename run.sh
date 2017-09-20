#!/bin/bash

javac -verbose $( find ./src/* | grep .java ) -d out/
java -cp out grbcp5.hw01.Main $1 $2
