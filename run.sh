#!/bin/bash

javac  -verbose $( find ./src/* -not -name "*Test.java" | grep .java ) -d out/
java -cp out grbcp5.hw01.Main $1 $2
