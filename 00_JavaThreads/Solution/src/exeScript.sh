#!/bin/bash

mkdir ./output
cd ./bin

for j in {1..4}
do
    for i in 1 2 4 6 8 10 12 16 20 24
    do
        java Driver ${j} ${i} ${i} 96000 >> ../output/q${j}.out
    done
done