#!/bin/bash

mkdir log
mkdir sol

mkdir log/50Shapes
mkdir sol/50Shapes
mkdir log/100Shapes
mkdir sol/100Shapes
mkdir log/100ShapesComplex
mkdir log/100ShapesComplex

javac  -verbose $( find ./src/* -not -name "*Test.java" | grep .java ) -d out/
java -cp out grbcp5.hw01.Main $1 $2
