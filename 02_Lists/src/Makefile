compile:
	javac -cp .:commons-math3-3.6.1.jar lists_benchmark/*.java

benchmark-1:
	java -cp .:commons-math3-3.6.1.jar lists_benchmark/Benchmark

benchmark-2:
	java -cp .:commons-math3-3.6.1.jar lists_benchmark/Benchmark 0 10000 1000 PerThread 0.05 0.05 0.9 5000 true 10 1 2 4 6 8 12 16 20 24

benchmark-3:
	java -cp .:commons-math3-3.6.1.jar lists_benchmark/Benchmark 0 1000000 1000 PerThread 1.0 0.0 0.0 0 true 10 1 2 4 6 8 12 16 20 24