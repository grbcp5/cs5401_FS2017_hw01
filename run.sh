#!/bin/bash

mkdir log
mkdir sol

mkdir log/1d
mkdir sol/1d

mkdir log/1d/50Shapes
mkdir sol/1d/50Shapes
mkdir log/1d/100Shapes
mkdir sol/1d/100Shapes
mkdir log/1d/100ComplexShapes
mkdir sol/1d/100ComplexShapes

javac  -verbose $( find ./src/* -not -name "*Test.java" | grep .java ) -d out/
java -cp out grbcp5.hw01.Main $1 $2
