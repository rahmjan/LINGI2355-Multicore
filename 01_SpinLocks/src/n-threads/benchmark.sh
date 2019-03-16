#!/bin/bash

mkdir ./output
cd ./bin

numOfTests=48000
#numOfTests=4800

for lock in {0..9}
do
    for threads in 1 2 4 6 8 10 12 16 20 24
    do
        java Worker ${lock} ${threads} ${numOfTests} >> ../output/q${lock}.out
    done
done